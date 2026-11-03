package com.algorithm.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIFallbackService {

    private static final Logger log = LoggerFactory.getLogger(AIFallbackService.class);
    private static final int CACHE_SIZE = 200;
    // Long solutions (code + explanation) exceed 2000 tokens on the 70B model; still < $0.003 per answer
    private static final int MAX_COMPLETION_TOKENS = 3000;
    // Wait at most this long for a rate-limit retry before giving up (the user is watching "Loading…")
    private static final long MAX_RETRY_WAIT_SECONDS = 10;
    private static final String GROQ_SYSTEM_PROMPT = "You are an expert algorithm tutor. Explain clearly, "
            + "write correct, complete Java, and follow the requested section format exactly.";
    // Users see one neutral message; which provider failed and why goes to the log
    public static final String UNAVAILABLE_MESSAGE = "Couldn't load a solution right now. Please try again.";
    private static final Pattern SECTION_HEADER = Pattern.compile(
            // "EXPLANATION:", "**Solution:**", "## Code" — header alone on its line or followed by a colon
            "(?im)^[ \\t]*(?:#+[ \\t]*)?(?:\\*\\*)?(EXPLANATION|SOLUTION|CODE|COMPLEXITY)(?:\\*\\*)?[ \\t]*(?::(?:\\*\\*)?|$)");
    private static final Pattern CODE_BLOCK = Pattern.compile("```[a-zA-Z]*\\s*\\n(.*?)```", Pattern.DOTALL);

    // Generation blocks for seconds (minutes for Claude Code); keep it off the common pool
    private static final ExecutorService GENERATORS = Executors.newFixedThreadPool(4, runnable -> {
        Thread thread = new Thread(runnable, "solution-generator");
        thread.setDaemon(true);
        return thread;
    });

    private final ClaudeCodeService claudeCode;
    private final RestTemplate restTemplate = createRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Repeated questions (re-asked, or the same page refreshed) are answered without another API call
    private final Map<String, AISolution> cache = Collections.synchronizedMap(
            new LinkedHashMap<>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, AISolution> eldest) {
                    return size() > CACHE_SIZE;
                }
            });

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.api.model:llama-3.3-70b-versatile}")
    private String model;

    public AIFallbackService(ClaudeCodeService claudeCode) {
        this.claudeCode = claudeCode;
    }

    private static RestTemplate createRestTemplate() {
        // Fail fast instead of holding the request until the servlet async timeout
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(40_000);
        return new RestTemplate(factory);
    }

    /** True when at least one engine can answer: the owner's local Claude Code, or Groq. */
    public boolean isConfigured() {
        return claudeCode.isAvailable() || isGroqConfigured();
    }

    private boolean isGroqConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    public CompletableFuture<AISolution> getSolution(String problemStatement, String language) {
        return getSolutionWithInputs(problemStatement, null, null, null, language);
    }

    public CompletableFuture<AISolution> getSolutionWithInputs(String problemStatement,
            String methodName,
            Object[] inputs,
            String[] paramTypes,
            String language) {
        if (!isConfigured()) {
            log.warn("Solution requested but no engine is available (Claude Code not signed in, GROQ_API_KEY not set)");
            return CompletableFuture.completedFuture(AISolution.failure(UNAVAILABLE_MESSAGE));
        }

        String prompt = buildPrompt(problemStatement, methodName, inputs, paramTypes, language);
        String cacheKey = cacheKey(problemStatement, methodName, inputs, language);
        AISolution cached = cache.get(cacheKey);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return CompletableFuture.supplyAsync(() -> {
            AISolution solution = generate(prompt);
            if (solution.error == null) {
                cache.put(cacheKey, solution);
            }
            return solution;
        }, GENERATORS);
    }

    // Claude Code first where the owner runs the app signed in; Groq otherwise or if Claude Code fails.
    // Callers can't tell which answered: both produce the same sections.
    private AISolution generate(String prompt) {
        if (claudeCode.isAvailable()) {
            try {
                return parseSolutionContent(claudeCode.ask(prompt));
            } catch (Exception e) {
                log.warn("Claude Code request failed, falling back to Groq", e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (!isGroqConfigured()) {
            return AISolution.failure(UNAVAILABLE_MESSAGE);
        }
        try {
            return parseSolutionContent(callGroqApi(prompt));
        } catch (Exception e) {
            log.error("Groq request failed", e);
            return AISolution.failure(UNAVAILABLE_MESSAGE);
        }
    }

    // Spoken questions differ in case/punctuation between attempts; those shouldn't miss the cache
    private static String cacheKey(String problemStatement, String methodName, Object[] inputs, String language) {
        String statement = problemStatement == null ? ""
                : problemStatement.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
        return String.join("|", statement, String.valueOf(methodName),
                inputs == null ? "" : Arrays.deepToString(inputs), String.valueOf(language));
    }

    private String buildPrompt(String problemStatement, String methodName,
            Object[] inputs, String[] paramTypes, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert algorithm tutor. Solve the following problem step by step:\n\n");
        sb.append("Problem: ").append(problemStatement).append("\n\n");

        if (methodName != null) {
            sb.append("Method name: ").append(methodName).append("\n\n");
        }

        if (inputs != null && paramTypes != null && inputs.length > 0) {
            sb.append("Input parameters:\n");
            for (int i = 0; i < inputs.length && i < paramTypes.length; i++) {
                sb.append("- ").append(paramTypes[i]).append(": ");
                sb.append(inputs[i] != null ? inputs[i].toString() : "null");
                sb.append("\n");
            }
            sb.append("\n");
        }

        sb.append("Please provide in this exact format:\n");
        sb.append("EXPLANATION:\n[clear step-by-step explanation]\n\n");
        sb.append("SOLUTION:\n[step-by-step solution]\n\n");
        sb.append("CODE:\n```java\n[complete Java code]\n```\n\n");
        sb.append("COMPLEXITY:\n[time and space complexity analysis]");

        return sb.toString();
    }

    private String callGroqApi(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Serialize with Jackson: the prompt has newlines, quotes and backslashes that must be escaped.
        // The role goes in a system message, which Llama 3.3 follows more reliably than user-message preamble.
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", GROQ_SYSTEM_PROMPT),
                        Map.of("role", "user", "content", prompt)),
                "temperature", 0.2,
                "max_completion_tokens", MAX_COMPLETION_TOKENS));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String response = postWithRateLimitRetry(request);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choice = root.path("choices").get(0);
            JsonNode contentNode = choice.path("message").path("content");
            if (contentNode.isMissingNode()) {
                throw new RuntimeException("No content in Groq response");
            }
            if ("length".equals(choice.path("finish_reason").asText())) {
                log.warn("Groq answer hit max_completion_tokens ({}) and may be cut off", MAX_COMPLETION_TOKENS);
            }
            return contentNode.asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq response: " + e.getMessage(), e);
        }
    }

    // One retry on 429 when Groq asks for a short wait; the 70B model's per-minute limits are tighter
    private String postWithRateLimitRetry(HttpEntity<String> request) throws InterruptedException {
        try {
            return restTemplate.postForObject(apiUrl, request, String.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            long waitSeconds = retryAfterSeconds(e);
            if (waitSeconds > MAX_RETRY_WAIT_SECONDS) {
                throw e;
            }
            log.info("Groq rate limited; retrying in {}s", waitSeconds);
            Thread.sleep(waitSeconds * 1000);
            return restTemplate.postForObject(apiUrl, request, String.class);
        }
    }

    private static long retryAfterSeconds(HttpClientErrorException e) {
        String header = e.getResponseHeaders() != null ? e.getResponseHeaders().getFirst("retry-after") : null;
        try {
            return header == null ? 1 : Math.max(0, (long) Math.ceil(Double.parseDouble(header)));
        } catch (NumberFormatException ignored) {
            return Long.MAX_VALUE; // HTTP-date or garbage: don't guess, fail now
        }
    }

    /** Split the model's reply into the EXPLANATION / SOLUTION / CODE / COMPLEXITY sections it was asked for. */
    AISolution parseSolutionContent(String content) {
        Map<String, String> sections = new LinkedHashMap<>();
        Matcher header = SECTION_HEADER.matcher(content);
        String current = null;
        int bodyStart = 0;
        while (header.find()) {
            if (current != null) {
                sections.put(current, content.substring(bodyStart, header.start()).trim());
            }
            current = header.group(1).toUpperCase();
            bodyStart = header.end();
        }
        if (current != null) {
            sections.put(current, content.substring(bodyStart).trim());
        }

        // Code comes from the fenced block wherever the model put it
        String code = "";
        Matcher block = CODE_BLOCK.matcher(content);
        if (block.find()) {
            code = block.group(1).trim();
        }

        if (sections.isEmpty()) {
            // Model ignored the format: show everything outside the code block as the explanation
            return new AISolution("", stripCodeBlocks(content), code, "");
        }
        return new AISolution(
                stripCodeBlocks(sections.getOrDefault("SOLUTION", "")),
                stripCodeBlocks(sections.getOrDefault("EXPLANATION", "")),
                code,
                stripCodeBlocks(sections.getOrDefault("COMPLEXITY", "")));
    }

    private static String stripCodeBlocks(String text) {
        return CODE_BLOCK.matcher(text).replaceAll("").trim();
    }

    public static class AISolution {
        public final String solution;
        public final String explanation;
        public final String code;
        public final String complexity;
        /** Set (and the other fields empty) when no solution could be produced. */
        public final String error;

        public AISolution(String solution, String explanation, String code) {
            this(solution, explanation, code, null, null);
        }

        public AISolution(String solution, String explanation, String code, String complexity) {
            this(solution, explanation, code, complexity, null);
        }

        private AISolution(String solution, String explanation, String code, String complexity, String error) {
            this.solution = solution;
            this.explanation = explanation;
            this.code = code;
            this.complexity = complexity;
            this.error = error;
        }

        public static AISolution failure(String message) {
            // explanation kept for clients that only read that field
            return new AISolution(null, message, null, null, message);
        }
    }
}