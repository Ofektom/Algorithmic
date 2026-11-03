package com.algorithm.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Generates solutions through the locally installed Claude Code CLI ({@code claude -p}), using the
 * subscription it is signed in with. Only usable where the owner runs the app on their own machine;
 * everywhere else (e.g. the hosted site) it reports unavailable and Groq is used instead.
 */
@Service
public class ClaudeCodeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeCodeService.class);
    private static final long AVAILABILITY_TTL_MS = TimeUnit.MINUTES.toMillis(5);
    private static final long STATUS_TIMEOUT_SECONDS = 15;
    private static final String SYSTEM_PROMPT = "You are an expert algorithm tutor. Answer only with the "
            + "requested sections. Do not use tools, read files, or ask follow-up questions.";
    // Never let the CLI fall back to API-key billing: only the signed-in subscription should be used
    private static final List<String> STRIPPED_ENV = List.of("ANTHROPIC_API_KEY", "ANTHROPIC_AUTH_TOKEN");

    // Own threads for draining process output: callers already block a pool thread waiting on us
    private static final ExecutorService STREAM_READERS = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "claude-code-io");
        thread.setDaemon(true);
        return thread;
    });

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String command;
    private final boolean enabled;
    private final long timeoutSeconds;

    private volatile Boolean available;
    private volatile long availabilityCheckedAt;

    public ClaudeCodeService(@Value("${solution.claude-code.command:claude}") String command,
            @Value("${solution.claude-code.enabled:true}") boolean enabled,
            @Value("${solution.claude-code.timeout-seconds:120}") long timeoutSeconds) {
        this.command = command;
        this.enabled = enabled;
        this.timeoutSeconds = timeoutSeconds;
    }

    /** True when the CLI is installed and signed in with a claude.ai subscription (cached for 5 minutes). */
    public boolean isAvailable() {
        if (!enabled) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (available == null || now - availabilityCheckedAt > AVAILABILITY_TTL_MS) {
            available = checkSignedIn();
            availabilityCheckedAt = now;
            log.info("Claude Code engine {}", available ? "available" : "not available");
        }
        return available;
    }

    /** Send one prompt and return the model's text; throws on any failure so the caller can fall back. */
    public String ask(String prompt) throws IOException, InterruptedException, TimeoutException {
        // Empty working directory: no project files or project instructions get pulled into the answer
        Path workDir = Files.createTempDirectory("solution-claude-");
        try {
            Result result = run(List.of(command, "-p",
                    "--output-format", "json",
                    "--no-session-persistence",
                    "--strict-mcp-config",
                    "--system-prompt", SYSTEM_PROMPT,
                    // variadic flag last; the prompt goes in on stdin
                    "--tools", ""),
                    prompt, workDir, timeoutSeconds);
            JsonNode json = parseJson(result.stdout());
            if (result.exitCode() != 0 || json == null || json.path("is_error").asBoolean(false)
                    || !json.path("result").isTextual()) {
                throw new IOException("claude -p failed (exit " + result.exitCode() + "): "
                        + (json != null ? json.path("result").asText("") : "") + " " + result.stderr());
            }
            return json.path("result").asText();
        } finally {
            Files.deleteIfExists(workDir);
        }
    }

    private boolean checkSignedIn() {
        try {
            Result result = run(List.of(command, "auth", "status", "--json"), "", null, STATUS_TIMEOUT_SECONDS);
            JsonNode status = parseJson(result.stdout());
            return result.exitCode() == 0 && status != null
                    && status.path("loggedIn").asBoolean(false)
                    && "claude.ai".equals(status.path("authMethod").asText());
        } catch (Exception e) {
            // Not installed, not on PATH, or hung: the hosted site lands here
            log.debug("Claude Code status check failed", e);
            return false;
        }
    }

    private JsonNode parseJson(String text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            return null;
        }
    }

    private Result run(List<String> args, String stdin, Path workDir, long timeout)
            throws IOException, InterruptedException, TimeoutException {
        ProcessBuilder builder = new ProcessBuilder(args);
        if (workDir != null) {
            builder.directory(workDir.toFile());
        }
        Map<String, String> env = builder.environment();
        STRIPPED_ENV.forEach(env::remove);

        Process process = builder.start();
        // Drain both streams concurrently so a full pipe can't block the process
        CompletableFuture<String> stdout = readAsync(process.getInputStream());
        CompletableFuture<String> stderr = readAsync(process.getErrorStream());
        try (OutputStream in = process.getOutputStream()) {
            in.write(stdin.getBytes(StandardCharsets.UTF_8));
        }
        if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new TimeoutException(args.get(0) + " did not finish within " + timeout + "s");
        }
        return new Result(process.exitValue(), stdout.join(), stderr.join());
    }

    private static CompletableFuture<String> readAsync(InputStream stream) {
        return CompletableFuture.supplyAsync(() -> {
            try (stream) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                return "";
            }
        }, STREAM_READERS);
    }

    private record Result(int exitCode, String stdout, String stderr) {
    }
}
