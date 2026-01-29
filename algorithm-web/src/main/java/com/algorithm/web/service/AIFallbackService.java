package com.algorithm.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class AIFallbackService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.api.model:llama-3.1-8b-instant}")
    private String model;

    // No custom constructor needed — Spring injects via @Value

    public boolean isConfigured() {
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
            return CompletableFuture.completedFuture(
                    new AISolution(null, "AI service not configured. Please set GROQ_API_KEY.", null));
        }

        String prompt = buildPrompt(problemStatement, methodName, inputs, paramTypes, language);

        return CompletableFuture.supplyAsync(() -> {
            try {
                String responseContent = callGroqApi(prompt);
                String[] parsed = parseSolutionContent(responseContent);
                return new AISolution(parsed[0], parsed[1], parsed[2]);
            } catch (Exception e) {
                return new AISolution(null, "Error: " + e.getMessage(), null);
            }
        });
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

    private String callGroqApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Escape double quotes in prompt
        String escapedPrompt = prompt.replace("\"", "\\\"");

        String requestBody = String.format("""
                {
                  "model": "%s",
                  "messages": [{"role": "user", "content": "%s"}],
                  "temperature": 0.7,
                  "max_tokens": 2000
                }
                """, model, escapedPrompt);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(apiUrl, request, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
            if (contentNode.isMissingNode()) {
                throw new RuntimeException("No content in Groq response");
            }
            return contentNode.asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq response: " + e.getMessage(), e);
        }
    }

    private String[] parseSolutionContent(String content) {
        String explanation = content;
        String solution = "";
        String code = "";

        // Extract code block
        int codeStart = content.indexOf("```java");
        if (codeStart == -1) {
            codeStart = content.indexOf("```");
        }

        if (codeStart != -1) {
            int codeEnd = content.indexOf("```", codeStart + 3);
            if (codeEnd != -1) {
                code = content
                        .substring(codeStart + (content.substring(codeStart).startsWith("```java") ? 7 : 3), codeEnd)
                        .trim();
                String before = content.substring(0, codeStart).trim();
                String after = content.substring(codeEnd + 3).trim();

                // Rough split for explanation/solution
                int solutionMarker = before.toUpperCase().indexOf("SOLUTION:");
                if (solutionMarker != -1) {
                    explanation = before.substring(0, solutionMarker).trim();
                    solution = before.substring(solutionMarker).trim();
                } else {
                    explanation = before;
                    solution = after;
                }
            }
        }

        return new String[] { solution, explanation, code };
    }

    public static class AISolution {
        public final String solution;
        public final String explanation;
        public final String code;

        public AISolution(String solution, String explanation, String code) {
            this.solution = solution;
            this.explanation = explanation;
            this.code = code;
        }
    }
}