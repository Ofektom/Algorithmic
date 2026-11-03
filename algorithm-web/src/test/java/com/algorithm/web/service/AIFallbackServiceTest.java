package com.algorithm.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AIFallbackServiceTest {

    private static final String REPLY = """
            **EXPLANATION:** Use a hash map.

            SOLUTION:
            1. Store each value's index.

            CODE:
            ```java
            int[] twoSum(int[] nums, int target) { return new int[0]; }
            ```

            ## Complexity
            O(n) time, O(n) space.""";

    private final ObjectMapper mapper = new ObjectMapper();
    private final List<JsonNode> groqRequests = new ArrayList<>();
    private int groqStatus = 200;
    // Statuses to send before falling back to groqStatus, e.g. a 429 then success
    private final Deque<Integer> nextStatuses = new ArrayDeque<>();
    private String retryAfter = "0";
    private HttpServer groq;
    private ClaudeCodeService claudeCode;
    private AIFallbackService service;

    @BeforeEach
    void setUp() throws IOException {
        // Stand-in for Groq's chat completions endpoint; strict JSON parsing, like the real API
        groq = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        groq.createContext("/chat", exchange -> {
            groqRequests.add(mapper.readTree(exchange.getRequestBody()));
            int status = nextStatuses.isEmpty() ? groqStatus : nextStatuses.poll();
            if (status == 429) {
                exchange.getResponseHeaders().add("retry-after", retryAfter);
            }
            byte[] body = mapper.writeValueAsBytes(mapper.createObjectNode().set("choices",
                    mapper.createArrayNode().add(mapper.createObjectNode().set("message",
                            mapper.createObjectNode().put("content", REPLY)))));
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        groq.start();

        claudeCode = mock(ClaudeCodeService.class);
        service = new AIFallbackService(claudeCode);
        ReflectionTestUtils.setField(service, "apiKey", "test-key");
        ReflectionTestUtils.setField(service, "apiUrl", "http://127.0.0.1:" + groq.getAddress().getPort() + "/chat");
        ReflectionTestUtils.setField(service, "model", "llama-3.3-70b-versatile");
    }

    @AfterEach
    void tearDown() {
        groq.stop(0);
    }

    private AIFallbackService.AISolution solve(String question) {
        return service.getSolutionWithInputs(question, null, null, null, "Java").join();
    }

    @Test
    void usesClaudeCodeWhenAvailable() throws Exception {
        when(claudeCode.isAvailable()).thenReturn(true);
        when(claudeCode.ask(anyString())).thenReturn(REPLY);

        AIFallbackService.AISolution solution = solve("two sum");

        assertNull(solution.error);
        assertEquals("Use a hash map.", solution.explanation);
        assertTrue(groqRequests.isEmpty(), "Groq must not be called when Claude Code answers");
    }

    @Test
    void fallsBackToGroqWhenClaudeCodeFails() throws Exception {
        when(claudeCode.isAvailable()).thenReturn(true);
        when(claudeCode.ask(anyString())).thenThrow(new IOException("usage limit reached"));

        AIFallbackService.AISolution solution = solve("two sum");

        assertNull(solution.error);
        assertEquals(1, groqRequests.size());
    }

    @Test
    void usesGroqWhenClaudeCodeUnavailable() throws Exception {
        when(claudeCode.isAvailable()).thenReturn(false);

        AIFallbackService.AISolution solution = solve("Problem with \"quotes\"\nand a newline");

        assertNull(solution.error);
        verify(claudeCode, never()).ask(anyString());
        // The multi-line prompt arrived as valid JSON with the question intact, after the system role
        JsonNode request = groqRequests.get(0);
        assertEquals("llama-3.3-70b-versatile", request.path("model").asText());
        assertEquals("system", request.at("/messages/0/role").asText());
        assertEquals("user", request.at("/messages/1/role").asText());
        assertTrue(request.at("/messages/1/content").asText().contains("Problem with \"quotes\"\nand a newline"));
        // Current parameter name; max_tokens is deprecated
        assertEquals(3000, request.path("max_completion_tokens").asInt());
        assertTrue(request.path("max_tokens").isMissingNode());
    }

    @Test
    void retriesOnceWhenRateLimitedBriefly() {
        when(claudeCode.isAvailable()).thenReturn(false);
        nextStatuses.add(429);
        retryAfter = "1";

        assertNull(solve("two sum").error);
        assertEquals(2, groqRequests.size());
    }

    @Test
    void doesNotWaitOutLongRateLimits() {
        when(claudeCode.isAvailable()).thenReturn(false);
        nextStatuses.add(429);
        retryAfter = "60";

        assertEquals(AIFallbackService.UNAVAILABLE_MESSAGE, solve("two sum").error);
        assertEquals(1, groqRequests.size());
    }

    @Test
    void failsNeutrallyWhenNoEngineIsAvailable() {
        when(claudeCode.isAvailable()).thenReturn(false);
        ReflectionTestUtils.setField(service, "apiKey", "");

        assertFalse(service.isConfigured());
        assertEquals(AIFallbackService.UNAVAILABLE_MESSAGE, solve("two sum").error);
    }

    @Test
    void failsNeutrallyWhenGroqErrors() {
        when(claudeCode.isAvailable()).thenReturn(false);
        groqStatus = 500;

        AIFallbackService.AISolution solution = solve("two sum");

        // No provider name or stack trace reaches the user
        assertEquals(AIFallbackService.UNAVAILABLE_MESSAGE, solution.error);
    }

    @Test
    void cachesSuccessfulAnswersAcrossWordingNoise() throws Exception {
        when(claudeCode.isAvailable()).thenReturn(true);
        when(claudeCode.ask(anyString())).thenReturn(REPLY);

        solve("Two Sum?");
        solve("two sum");

        verify(claudeCode, times(1)).ask(anyString());
    }

    @Test
    void doesNotCacheFailures() {
        when(claudeCode.isAvailable()).thenReturn(false);
        groqStatus = 500;
        solve("two sum");
        groqStatus = 200;

        assertNull(solve("two sum").error);
        assertEquals(2, groqRequests.size());
    }

    @Test
    void parsesSectionsInCommonHeaderStyles() {
        AIFallbackService.AISolution solution = service.parseSolutionContent(REPLY);

        assertEquals("Use a hash map.", solution.explanation);
        assertEquals("1. Store each value's index.", solution.solution);
        assertEquals("int[] twoSum(int[] nums, int target) { return new int[0]; }", solution.code);
        assertEquals("O(n) time, O(n) space.", solution.complexity);
    }

    @Test
    void unformattedReplyBecomesExplanation() {
        AIFallbackService.AISolution solution = service.parseSolutionContent(
                "Just loop over it.\n```java\nint x = 1;\n```");

        assertEquals("Just loop over it.", solution.explanation);
        assertEquals("int x = 1;", solution.code);
    }
}
