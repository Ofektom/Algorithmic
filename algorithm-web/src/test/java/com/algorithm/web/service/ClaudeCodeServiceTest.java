package com.algorithm.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class ClaudeCodeServiceTest {

    private static final String SIGNED_IN_SUBSCRIPTION = "{\"loggedIn\": true, \"authMethod\": \"claude.ai\"}";

    @TempDir
    Path dir;
    private Path fakeClaude;

    @BeforeEach
    void createFakeCli() throws IOException {
        // Stands in for the `claude` CLI: answers `auth status` from status.json; for `-p` it records
        // its arguments and stdin, optionally sleeps, then prints reply.json and exits with code `exit`
        fakeClaude = dir.resolve("claude");
        Files.writeString(fakeClaude, """
                #!/bin/sh
                D='%s'
                if [ "$1" = "auth" ]; then cat "$D/status.json"; exit 0; fi
                printf '%%s\\n' "$@" > "$D/args"
                cat > "$D/stdin"
                [ -f "$D/sleep" ] && sleep "$(cat "$D/sleep")"
                cat "$D/reply.json"
                exit "$(cat "$D/exit" 2>/dev/null || echo 0)"
                """.formatted(dir));
        assertTrue(fakeClaude.toFile().setExecutable(true));
        Files.writeString(dir.resolve("status.json"), SIGNED_IN_SUBSCRIPTION);
    }

    private ClaudeCodeService service(long timeoutSeconds) {
        return new ClaudeCodeService(fakeClaude.toString(), true, timeoutSeconds);
    }

    @Test
    void availableWhenSignedInWithSubscription() {
        assertTrue(service(5).isAvailable());
    }

    @Test
    void unavailableWhenSignedInWithApiKey() throws IOException {
        // API-key auth would bill the API, which this engine must never do
        Files.writeString(dir.resolve("status.json"), "{\"loggedIn\": true, \"authMethod\": \"api_key\"}");
        assertFalse(service(5).isAvailable());
    }

    @Test
    void unavailableWhenSignedOut() throws IOException {
        Files.writeString(dir.resolve("status.json"), "{\"loggedIn\": false}");
        assertFalse(service(5).isAvailable());
    }

    @Test
    void unavailableWhenCliMissing() {
        // The hosted site: no `claude` on the machine
        assertFalse(new ClaudeCodeService(dir.resolve("missing").toString(), true, 5).isAvailable());
    }

    @Test
    void unavailableWhenDisabled() {
        assertFalse(new ClaudeCodeService(fakeClaude.toString(), false, 5).isAvailable());
    }

    @Test
    void askSendsPromptOnStdinWithToolsDisabled() throws Exception {
        Files.writeString(dir.resolve("reply.json"), "{\"type\": \"result\", \"is_error\": false, \"result\": \"EXPLANATION: ok\"}");
        String prompt = "Problem: two \"sum\"\nwith newline";

        assertEquals("EXPLANATION: ok", service(5).ask(prompt));

        assertEquals(prompt, Files.readString(dir.resolve("stdin")));
        List<String> args = Files.readAllLines(dir.resolve("args"));
        assertEquals("-p", args.get(0));
        assertTrue(args.containsAll(List.of("--output-format", "json", "--no-session-persistence", "--strict-mcp-config")));
        // `--tools ""` (no tools) is last so the variadic flag can't swallow anything else
        assertEquals("--tools", args.get(args.size() - 2));
        assertEquals("", args.get(args.size() - 1));
    }

    @Test
    void askFailsOnErrorResult() throws IOException {
        Files.writeString(dir.resolve("reply.json"), "{\"type\": \"result\", \"is_error\": true, \"result\": \"usage limit reached\"}");
        IOException error = assertThrows(IOException.class, () -> service(5).ask("q"));
        assertTrue(error.getMessage().contains("usage limit reached"));
    }

    @Test
    void askFailsOnNonZeroExit() throws IOException {
        Files.writeString(dir.resolve("reply.json"), "not json");
        Files.writeString(dir.resolve("exit"), "1");
        assertThrows(IOException.class, () -> service(5).ask("q"));
    }

    @Test
    void askTimesOut() throws IOException {
        Files.writeString(dir.resolve("reply.json"), "{\"result\": \"late\"}");
        Files.writeString(dir.resolve("sleep"), "5");
        assertThrows(TimeoutException.class, () -> service(1).ask("q"));
    }
}
