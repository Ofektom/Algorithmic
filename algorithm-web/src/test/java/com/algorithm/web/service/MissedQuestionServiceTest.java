package com.algorithm.web.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissedQuestionServiceTest {

    /** A clock the test moves forward by hand. */
    private static class StepClock extends Clock {
        private Instant now = Instant.parse("2026-11-04T10:00:00Z");
        void advance() { now = now.plusSeconds(60); }
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    private final StepClock clock = new StepClock();
    private final MissedQuestionService service = new MissedQuestionService(clock);

    @Test
    void sameQuestionInDifferentWordingIsCountedOnce() {
        service.record("Longest palindrome in a string?");
        clock.advance();
        service.record("  longest PALINDROME in a string ");

        List<MissedQuestionService.Miss> top = service.top(10);
        assertEquals(1, top.size());
        assertEquals("Longest palindrome in a string?", top.get(0).question());
        assertEquals(2, top.get(0).count());
        assertTrue(top.get(0).lastAsked().isAfter(top.get(0).firstAsked()));
    }

    @Test
    void mostAskedComesFirstThenMostRecent() {
        service.record("a question");
        clock.advance();
        service.record("b question");
        clock.advance();
        service.record("c question");
        service.record("c question");

        List<MissedQuestionService.Miss> top = service.top(10);
        assertEquals(List.of("c question", "b question", "a question"),
                top.stream().map(MissedQuestionService.Miss::question).toList());
        assertEquals(2, service.top(2).size());
    }

    @Test
    void blankQuestionsAreIgnored() {
        service.record(null);
        service.record("   ");
        service.record("?!");
        assertEquals(0, service.size());
    }

    @Test
    void longQuestionsAreShortened() {
        service.record("x".repeat(500));
        String kept = service.top(1).get(0).question();
        assertEquals(MissedQuestionService.MAX_TEXT_LENGTH + 1, kept.length());
        assertTrue(kept.endsWith("…"));
    }

    @Test
    void oldestQuestionIsDroppedWhenFull() {
        for (int i = 0; i <= MissedQuestionService.MAX_QUESTIONS; i++) {
            service.record("question " + i);
            clock.advance();
        }
        assertEquals(MissedQuestionService.MAX_QUESTIONS, service.size());
        assertTrue(service.top(MissedQuestionService.MAX_QUESTIONS).stream()
                .noneMatch(m -> m.question().equals("question 0")));
    }
}
