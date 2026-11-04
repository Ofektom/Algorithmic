package com.algorithm.web.controller;

import com.algorithm.web.service.AIFallbackService;
import com.algorithm.web.service.MissedQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIFallbackService aiService;
    private final MissedQuestionService missedQuestions;

    public AIController(AIFallbackService aiService, MissedQuestionService missedQuestions) {
        this.aiService = aiService;
        this.missedQuestions = missedQuestions;
    }

    @GetMapping("/solution")
    public CompletableFuture<ResponseEntity<AIFallbackService.AISolution>> getSolution(
            @RequestParam String problemStatement,
            @RequestParam(defaultValue = "Java") String language) {
        // Reaching the generator means no built-in problem matched this question
        missedQuestions.record(problemStatement);

        if (!aiService.isConfigured()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(AIFallbackService.AISolution.failure(AIFallbackService.UNAVAILABLE_MESSAGE)));
        }

        return aiService.getSolution(problemStatement, language)
                .thenApply(AIController::toResponse);
    }

    @PostMapping("/solution-with-inputs")
    public CompletableFuture<ResponseEntity<AIFallbackService.AISolution>> getSolutionWithInputs(
            @RequestBody AISolutionRequest request) {
        if (request.problemStatement == null || request.problemStatement.isBlank()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(AIFallbackService.AISolution.failure("Ask a question first.")));
        }
        AIFallbackService.Guidance guidance = new AIFallbackService.Guidance(
                request.signature, request.instructions, request.previousCode);
        // A refinement re-asks a question already recorded on the first attempt
        if (guidance.isEmpty()) {
            missedQuestions.record(request.problemStatement);
        }

        if (!aiService.isConfigured()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(AIFallbackService.AISolution.failure(AIFallbackService.UNAVAILABLE_MESSAGE)));
        }

        return aiService.getSolutionWithInputs(
                request.problemStatement,
                request.methodName,
                request.inputs,
                request.paramTypes,
                request.language,
                guidance)
                .thenApply(AIController::toResponse);
    }

    // A failed generation is an upstream problem, not a successful answer
    private static ResponseEntity<AIFallbackService.AISolution> toResponse(AIFallbackService.AISolution solution) {
        return solution.error != null
                ? ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(solution)
                : ResponseEntity.ok(solution);
    }

    public static class AISolutionRequest {
        public String problemStatement;
        public String methodName;
        public Object[] inputs;
        public String[] paramTypes;
        public String language = "Java";
        /** Signature or starter code the answer must use exactly, e.g. "char* solve(int* arr, int n)". */
        public String signature;
        /** Extra guidance when an answer didn't fit, e.g. "return the result as a string". */
        public String instructions;
        /** The previous answer's code, so a refinement fixes it instead of starting over. */
        public String previousCode;
    }
}