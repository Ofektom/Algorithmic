package com.algorithm.web.controller;

import com.algorithm.web.service.MissedQuestionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * The owner's view of questions that fell through to the generator: /insights?key=YOUR_KEY.
 * Off (404) unless INSIGHTS_KEY is set, because the list holds what other people typed.
 */
@Controller
public class InsightsController {

    private static final int LIMIT = 100;

    private final MissedQuestionService missedQuestions;
    private final byte[] key;

    public InsightsController(MissedQuestionService missedQuestions,
            @Value("${app.insights.key:}") String key) {
        this.missedQuestions = missedQuestions;
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/insights")
    public String insights(@RequestParam(value = "key", required = false) String supplied, Model model) {
        if (key.length == 0 || supplied == null
                || !MessageDigest.isEqual(supplied.getBytes(StandardCharsets.UTF_8), key)) {
            // Same answer for "off" and "wrong key", so the page's existence isn't revealed
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("misses", missedQuestions.top(LIMIT));
        model.addAttribute("total", missedQuestions.size());
        model.addAttribute("since", missedQuestions.startedAt());
        return "insights";
    }
}
