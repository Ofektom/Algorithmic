package com.algorithm.android.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.algorithm.android.data.ProblemInfo;
import com.algorithm.android.service.ProblemRegistry;
import com.algorithm.android.service.VoiceInputService;

import java.util.Arrays;
import java.util.List;

/**
 * Handler for voice input navigation and problem matching
 */
public class VoiceInputHandler {
    
    private Context context;
    private VoiceInputService voiceService;
    private ProblemRegistry problemRegistry;
    private VoiceNavigationListener navigationListener;
    
    public interface VoiceNavigationListener {
        void onCategorySelected(String category);
        void onProblemSelected(ProblemInfo problem);
        void onMethodSelected(ProblemInfo problem, String methodName);
        void onInputProvided(ProblemInfo problem, String methodName, String input);
    }
    
    public VoiceInputHandler(Context context, VoiceNavigationListener listener) {
        this.context = context;
        this.navigationListener = listener;
        this.problemRegistry = ProblemRegistry.getInstance();
        
        if (VoiceInputService.isRecognitionAvailable(context)) {
            this.voiceService = new VoiceInputService(context);
        } else {
            Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Start listening for voice input
     */
    public void startListening() {
        if (voiceService == null) {
            Toast.makeText(context, "Voice input not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        voiceService.startListening(new VoiceInputService.VoiceInputListener() {
            @Override
            public void onResult(String result) {
                processVoiceInput(result);
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onListeningStarted() {
                Toast.makeText(context, "Listening...", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onListeningStopped() {
                // Listening stopped
            }
        });
    }
    
    /**
     * Process voice input and navigate accordingly
     */
    private void processVoiceInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            Toast.makeText(context, "No input detected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String lowerInput = input.toLowerCase().trim();
        android.util.Log.d("VoiceInputHandler", "Processing voice input: " + lowerInput);
        
        // Try to match category first
        String[] categories = problemRegistry.getCategories();
        for (String category : categories) {
            if (matchesCategory(lowerInput, category)) {
                android.util.Log.d("VoiceInputHandler", "Matched category: " + category);
                if (navigationListener != null) {
                    navigationListener.onCategorySelected(category);
                }
                return;
            }
        }
        
        // Try to match problem
        ProblemInfo problem = problemRegistry.findProblem(lowerInput);
        if (problem == null) {
            // Try matching from problem statement
            problem = problemRegistry.matchProblemFromStatement(lowerInput);
        }
        
        if (problem != null) {
            android.util.Log.d("VoiceInputHandler", "Matched problem: " + problem.name);
            if (navigationListener != null) {
                navigationListener.onProblemSelected(problem);
            }
            return;
        }
        
        // Try keyword search
        List<ProblemInfo> results = problemRegistry.searchProblems(lowerInput);
        if (!results.isEmpty()) {
            android.util.Log.d("VoiceInputHandler", "Found " + results.size() + " problems via search");
            if (navigationListener != null) {
                navigationListener.onProblemSelected(results.get(0));
            }
            return;
        }
        
        // If no match found, show helpful message
        String categoryList = String.join(", ", Arrays.asList(categories).subList(0, Math.min(5, categories.length)));
        Toast.makeText(context, 
            "No match found. Try: " + categoryList + "...", 
            Toast.LENGTH_LONG).show();
    }
    
    /**
     * Check if input matches a category
     */
    private boolean matchesCategory(String input, String category) {
        String lowerCategory = category.toLowerCase();
        String[] categoryWords = lowerCategory.split("\\s+");
        String[] inputWords = input.split("\\s+");
        
        // Check if any category word matches any input word
        for (String catWord : categoryWords) {
            for (String inWord : inputWords) {
                if (catWord.contains(inWord) || inWord.contains(catWord)) {
                    if (catWord.length() > 2 && inWord.length() > 2) { // Avoid matching short words
                        return true;
                    }
                }
            }
        }
        
        // Handle common variations
        if (lowerCategory.contains("arrays") && (input.contains("array") || input.contains("hash"))) {
            return true;
        }
        if (lowerCategory.contains("strings") && input.contains("string")) {
            return true;
        }
        if (lowerCategory.contains("trees") && input.contains("tree")) {
            return true;
        }
        if (lowerCategory.contains("linked list") && (input.contains("linked") || input.contains("list"))) {
            return true;
        }
        if (lowerCategory.contains("two pointers") && (input.contains("pointer") || input.contains("two"))) {
            return true;
        }
        if (lowerCategory.contains("sliding window") && (input.contains("window") || input.contains("sliding"))) {
            return true;
        }
        if (lowerCategory.contains("stack") && input.contains("stack")) {
            return true;
        }
        if (lowerCategory.contains("search") && input.contains("search")) {
            return true;
        }
        if (lowerCategory.contains("dynamic programming") && (input.contains("dp") || input.contains("dynamic"))) {
            return true;
        }
        if (lowerCategory.contains("graph") && input.contains("graph")) {
            return true;
        }
        if (lowerCategory.contains("heap") && (input.contains("heap") || input.contains("priority"))) {
            return true;
        }
        if (lowerCategory.contains("backtrack") && input.contains("backtrack")) {
            return true;
        }
        if (lowerCategory.contains("trie") && input.contains("trie")) {
            return true;
        }
        if (lowerCategory.contains("interval") && input.contains("interval")) {
            return true;
        }
        if (lowerCategory.contains("math") && (input.contains("math") || input.contains("geometry"))) {
            return true;
        }
        if (lowerCategory.contains("greedy") && input.contains("greedy")) {
            return true;
        }
        if (lowerCategory.contains("bit") && (input.contains("bit") || input.contains("manipulation"))) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Stop listening
     */
    public void stopListening() {
        if (voiceService != null) {
            voiceService.stopListening();
        }
    }
    
    /**
     * Clean up resources
     */
    public void destroy() {
        if (voiceService != null) {
            voiceService.destroy();
            voiceService = null;
        }
    }
    
    /**
     * Check if voice input is available
     */
    public boolean isAvailable() {
        return voiceService != null && voiceService.isAvailable();
    }
}

