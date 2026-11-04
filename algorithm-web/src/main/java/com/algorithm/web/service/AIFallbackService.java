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
    // Two code blocks (the solution and the full program) plus explanation; still well under a cent per answer
    private static final int MAX_COMPLETION_TOKENS = 4000;
    // Wait at most this long for a rate-limit retry before giving up (the user is watching "Loading…")
    private static final long MAX_RETRY_WAIT_SECONDS = 10;
    private static final String GROQ_SYSTEM_PROMPT = "You are an expert algorithm tutor. Explain clearly, "
            + "write correct code in the requested language, follow any required signature exactly, "
            + "and follow the requested section format exactly.";
    // User-supplied guidance is free text; cap it so one request can't blow the prompt budget
    private static final int MAX_GUIDANCE_CHARS = 4000;
    // Users see one neutral message; which provider failed and why goes to the log
    public static final String UNAVAILABLE_MESSAGE = "Couldn't load a solution right now. Please try again.";
    private static final Pattern SECTION_HEADER = Pattern.compile(
            // "EXPLANATION:", "**Solution:**", "## Code" — header alone on its line or followed by a colon
            "(?im)^[ \\t]*(?:#+[ \\t]*)?(?:\\*\\*)?(PROBLEM|EXPLANATION|SOLUTION|CODE|(?:FULL[ \\t]+)?PROGRAM|COMPLEXITY)(?:\\*\\*)?[ \\t]*(?::(?:\\*\\*)?|$)");
    private static final Pattern CODE_BLOCK = Pattern.compile("```[a-zA-Z+#]*\\s*\\n(.*?)```", Pattern.DOTALL);

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
        return getSolutionWithInputs(problemStatement, methodName, inputs, paramTypes, language, Guidance.NONE);
    }

    /**
     * Same as above, with the user's corrections applied: a required signature (e.g. the platform's
     * {@code char* solve(int* arr, int n)} stub), extra instructions, and the previous code to fix.
     */
    public CompletableFuture<AISolution> getSolutionWithInputs(String problemStatement,
            String methodName,
            Object[] inputs,
            String[] paramTypes,
            String language,
            Guidance guidance) {
        // Language goes into the prompt; keep it to a plain name such as "C", "C++", "C#" or "Python"
        String cleanLanguage = language == null ? "" : language.replaceAll("[^A-Za-z0-9+# ]", "").trim();
        language = cleanLanguage.isEmpty() || cleanLanguage.length() > 20 ? "Java" : cleanLanguage;
        guidance = guidance == null ? Guidance.NONE : guidance;
        if (!isConfigured()) {
            log.warn("Solution requested but no engine is available (Claude Code not signed in, GROQ_API_KEY not set)");
            return CompletableFuture.completedFuture(AISolution.failure(UNAVAILABLE_MESSAGE));
        }

        String prompt = buildPrompt(problemStatement, methodName, inputs, paramTypes, language, guidance);
        String cacheKey = cacheKey(problemStatement, methodName, inputs, language, guidance);
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
    private static String cacheKey(String problemStatement, String methodName, Object[] inputs, String language,
            Guidance guidance) {
        String statement = problemStatement == null ? ""
                : problemStatement.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
        // Guidance is kept exact: "int*" vs "int" matters there. Language is case-insensitive.
        return String.join("|", statement, String.valueOf(methodName),
                inputs == null ? "" : Arrays.deepToString(inputs), language.toLowerCase(),
                String.valueOf(guidance.signature()), String.valueOf(guidance.instructions()),
                String.valueOf(guidance.previousCode()));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String clip(String s) {
        String trimmed = s.trim();
        return trimmed.length() <= MAX_GUIDANCE_CHARS ? trimmed : trimmed.substring(0, MAX_GUIDANCE_CHARS);
    }

    String buildPrompt(String problemStatement, String methodName,
            Object[] inputs, String[] paramTypes, String language, Guidance guidance) {
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

        if (!isBlank(guidance.signature())) {
            sb.append("Required signature / starter code. Use it exactly as given: the same function name, ")
                    .append("parameter types (e.g. a pointer such as int* stays a pointer, with its length passed ")
                    .append("as given) and return type. Do not substitute other types:\n```\n")
                    .append(clip(guidance.signature())).append("\n```\n")
                    .append("CODE is this starter code with the body filled in: keep its class and function ")
                    .append("declaration as given, and leave out any main or driver class it contains.\n\n");
        }
        if (!isBlank(guidance.previousCode())) {
            sb.append("A previous answer did not fit. Here is its code; fix it rather than repeating its mistakes:\n```\n")
                    .append(clip(guidance.previousCode())).append("\n```\n\n");
        }
        if (!isBlank(guidance.instructions())) {
            sb.append("Additional instructions from the user. These override any assumption you would ")
                    .append("otherwise make:\n").append(clip(guidance.instructions())).append("\n\n");
        }

        sb.append("Write the code in ").append(language).append(".\n\n");
        sb.append("Please provide in this exact format:\n");
        sb.append("PROBLEM:\n[the exact problem you are solving, restated in one or two sentences, "
                + "then one example input and its output]\n\n");
        sb.append("EXPLANATION:\n[clear step-by-step explanation]\n\n");
        sb.append("SOLUTION:\n[step-by-step solution]\n\n");
        // The solution alone, as a platform such as HackerRank or LeetCode expects it pasted in;
        // the runnable program with an entry point goes in its own section
        sb.append("CODE:\n```").append(fenceTag(language)).append("\n[only the solution: the function that ")
                .append("solves the problem, plus any helper functions and imports it uses. No main method or other ")
                .append("entry point, no reading input, no printing. Where ").append(language)
                .append(" needs a class, use the starter code's class if one was given, otherwise a class named Solution.]")
                .append("\n```\n\n");
        sb.append("PROGRAM:\n```").append(fenceTag(language)).append("\n[a complete program that runs as is: ")
                .append("the solution above unchanged, plus a main method or entry point that calls it on the ")
                .append("example from PROBLEM, hard-coded rather than read from input, and prints the result]")
                .append("\n```\n\n");
        sb.append("COMPLEXITY:\n[time and space complexity analysis]");

        return sb.toString();
    }

    private static String fenceTag(String language) {
        String tag = language.toLowerCase().replaceAll("[^a-z0-9+#]", "");
        return tag.isEmpty() ? "java" : tag;
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

    /** Split the model's reply into the PROBLEM / EXPLANATION / SOLUTION / CODE / COMPLEXITY sections it was asked for. */
    AISolution parseSolutionContent(String content) {
        Map<String, String> sections = new LinkedHashMap<>();
        Matcher header = SECTION_HEADER.matcher(content);
        String current = null;
        int bodyStart = 0;
        while (header.find()) {
            if (current != null) {
                sections.put(current, content.substring(bodyStart, header.start()).trim());
            }
            String name = header.group(1).toUpperCase();
            current = name.endsWith("PROGRAM") ? "PROGRAM" : name;
            bodyStart = header.end();
        }
        if (current != null) {
            sections.put(current, content.substring(bodyStart).trim());
        }

        // The full program comes only from its own section; the solution from CODE, or else the
        // first fenced block wherever the model put it
        String program = firstCodeBlock(sections.getOrDefault("PROGRAM", ""));
        String code = firstCodeBlock(sections.getOrDefault("CODE", ""));
        if (code.isEmpty()) {
            Matcher block = CODE_BLOCK.matcher(content);
            while (block.find()) {
                String candidate = block.group(1).trim();
                if (!candidate.equals(program)) {
                    code = candidate;
                    break;
                }
            }
        }

        if (sections.isEmpty()) {
            // Model ignored the format: show everything outside the code block as the explanation
            return new AISolution("", stripCodeBlocks(content), code, "");
        }
        return new AISolution(
                stripCodeBlocks(sections.getOrDefault("SOLUTION", "")),
                stripCodeBlocks(sections.getOrDefault("EXPLANATION", "")),
                code,
                stripCodeBlocks(sections.getOrDefault("COMPLEXITY", "")),
                stripCodeBlocks(sections.getOrDefault("PROBLEM", "")),
                program);
    }

    private static String firstCodeBlock(String text) {
        Matcher block = CODE_BLOCK.matcher(text);
        return block.find() ? block.group(1).trim() : "";
    }

    private static String stripCodeBlocks(String text) {
        return CODE_BLOCK.matcher(text).replaceAll("").trim();
    }

    /** User corrections for a generated answer that didn't fit the problem as their platform states it. */
    public record Guidance(String signature, String instructions, String previousCode) {
        public static final Guidance NONE = new Guidance(null, null, null);

        public boolean isEmpty() {
            return isBlank(signature) && isBlank(instructions) && isBlank(previousCode);
        }
    }

    public static class AISolution {
        public final String solution;
        public final String explanation;
        public final String code;
        public final String complexity;
        /** The problem as the model understood it, so the user can check it solved what they meant. */
        public final String problem;
        /** The solution with a main method that runs it on the example; empty if the model left it out. */
        public final String program;
        /** Set (and the other fields empty) when no solution could be produced. */
        public final String error;

        public AISolution(String solution, String explanation, String code) {
            this(solution, explanation, code, null, null, null, null);
        }

        public AISolution(String solution, String explanation, String code, String complexity) {
            this(solution, explanation, code, complexity, null, null, null);
        }

        public AISolution(String solution, String explanation, String code, String complexity, String problem) {
            this(solution, explanation, code, complexity, problem, "", null);
        }

        public AISolution(String solution, String explanation, String code, String complexity, String problem,
                String program) {
            this(solution, explanation, code, complexity, problem, program, null);
        }

        private AISolution(String solution, String explanation, String code, String complexity, String problem,
                String program, String error) {
            this.solution = solution;
            this.explanation = explanation;
            this.code = code;
            this.complexity = complexity;
            this.problem = problem;
            this.program = program;
            this.error = error;
        }

        public static AISolution failure(String message) {
            // explanation kept for clients that only read that field
            return new AISolution(null, message, null, null, null, null, message);
        }
    }
}