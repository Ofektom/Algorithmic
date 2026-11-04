package com.algorithm.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Questions the built-in library couldn't answer, so the owner can see which problems to add next.
 *
 * <p>Kept in memory only (no database): counts reset when the server restarts or redeploys. Each miss
 * is also written to the log as "Unmatched question", which survives restarts for as long as the host
 * keeps logs. Only the question text is kept: nothing about who asked it.
 */
@Service
public class MissedQuestionService {

    private static final Logger log = LoggerFactory.getLogger(MissedQuestionService.class);
    static final int MAX_QUESTIONS = 500;
    static final int MAX_TEXT_LENGTH = 200;

    private final Map<String, Miss> misses = new HashMap<>();
    private final Clock clock;
    private final Instant startedAt;

    public MissedQuestionService() {
        this(Clock.systemUTC());
    }

    MissedQuestionService(Clock clock) {
        this.clock = clock;
        this.startedAt = clock.instant();
    }

    /** One question as the owner sees it: the first wording asked, how often, and when. */
    public record Miss(String question, int count, Instant firstAsked, Instant lastAsked) {
    }

    public synchronized void record(String question) {
        String key = normalize(question);
        if (key.isEmpty()) {
            return;
        }
        Instant now = clock.instant();
        Miss existing = misses.get(key);
        Miss updated = existing == null
                ? new Miss(truncate(question.trim()), 1, now, now)
                : new Miss(existing.question(), existing.count() + 1, existing.firstAsked(), now);
        misses.put(key, updated);
        if (misses.size() > MAX_QUESTIONS) {
            evictOldest();
        }
        log.info("Unmatched question (asked {} time{}): {}", updated.count(), updated.count() == 1 ? "" : "s",
                updated.question());
    }

    /** Most asked first; ties go to the most recent. */
    public synchronized List<Miss> top(int limit) {
        List<Miss> list = new ArrayList<>(misses.values());
        list.sort(Comparator.comparingInt(Miss::count).reversed()
                .thenComparing(Miss::lastAsked, Comparator.reverseOrder()));
        return list.subList(0, Math.min(limit, list.size()));
    }

    public synchronized int size() {
        return misses.size();
    }

    public Instant startedAt() {
        return startedAt;
    }

    // "Two-Sum?" and "two sum" are the same question
    static String normalize(String question) {
        if (question == null) {
            return "";
        }
        return question.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }

    private static String truncate(String text) {
        String oneLine = text.replaceAll("\\s+", " ");
        return oneLine.length() <= MAX_TEXT_LENGTH ? oneLine : oneLine.substring(0, MAX_TEXT_LENGTH) + "…";
    }

    private void evictOldest() {
        String oldest = null;
        Instant oldestTime = null;
        for (Map.Entry<String, Miss> e : misses.entrySet()) {
            if (oldestTime == null || e.getValue().lastAsked().isBefore(oldestTime)) {
                oldest = e.getKey();
                oldestTime = e.getValue().lastAsked();
            }
        }
        misses.remove(oldest);
    }
}
