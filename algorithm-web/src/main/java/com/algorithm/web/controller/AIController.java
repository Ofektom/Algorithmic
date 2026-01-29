package com.algorithm.web.controller;

import com.algorithm.web.service.AIFallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private final AIFallbackService aiService;

    public AIController(AIFallbackService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/solution")
    public CompletableFuture<ResponseEntity<AIFallbackService.AISolution>> getSolution(
            @RequestParam String problemStatement,
            @RequestParam(defaultValue = "Java") String language) {

        if (!aiService.isConfigured()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(
                            new AIFallbackService.AISolution(null, "AI service not configured", null)));
        }

        return aiService.getSolution(problemStatement, language)
                .thenApply(solution -> ResponseEntity.ok(solution));
    }

    @PostMapping("/solution-with-inputs")
    public CompletableFuture<ResponseEntity<AIFallbackService.AISolution>> getSolutionWithInputs(
            @RequestBody AISolutionRequest request) {

        if (!aiService.isConfigured()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(
                            new AIFallbackService.AISolution(null, "AI service not configured", null)));
        }

        return aiService.getSolutionWithInputs(
                request.problemStatement,
                request.methodName,
                request.inputs,
                request.paramTypes,
                request.language)
                .thenApply(solution -> ResponseEntity.ok(solution));
    }

    public static class AISolutionRequest {
        public String problemStatement;
        public String methodName;
        public Object[] inputs;
        public String[] paramTypes;
        public String language = "Java";
    }
}