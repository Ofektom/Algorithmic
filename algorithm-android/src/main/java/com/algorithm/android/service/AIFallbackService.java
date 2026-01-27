package com.algorithm.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.algorithm.android.BuildConfig;
import com.algorithm.android.data.ProblemInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Service to handle AI fallback for unknown algorithms
 * Connects to LLM API to get step-by-step solutions
 */
public class AIFallbackService {
    
    private static final String TAG = "AIFallbackService";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private Context context;
    private OkHttpClient httpClient;
    private String apiKey; // Should be stored securely
    private String apiUrl; // LLM API endpoint
    
    public interface AIResponseListener {
        void onSuccess(String solution, String explanation, String code);
        void onError(String error);
    }
    
    public AIFallbackService(Context context) {
        this.context = context;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        
        // Load API key from BuildConfig (set from gradle.properties)
        this.apiKey = BuildConfig.GROQ_API_KEY;
        this.apiUrl = BuildConfig.GROQ_API_URL;
        
        // Check SharedPreferences for runtime override
        SharedPreferences prefs = context.getSharedPreferences("ai_config", Context.MODE_PRIVATE);
        String runtimeKey = prefs.getString("api_key", null);
        String runtimeProvider = prefs.getString("provider", null);
        
        // Use runtime config if available, otherwise use BuildConfig
        if (runtimeKey != null && !runtimeKey.isEmpty()) {
            this.apiKey = runtimeKey;
            if (runtimeProvider != null) {
                this.apiUrl = getApiUrlForProvider(runtimeProvider);
            }
        } else if (apiKey == null || apiKey.isEmpty()) {
            // Fallback: try SharedPreferences if BuildConfig is empty
            this.apiKey = prefs.getString("api_key", null);
            this.apiUrl = prefs.getString("api_url", BuildConfig.GROQ_API_URL);
        }
    }
    
    /**
     * Set API configuration
     */
    public void setApiConfig(String apiKey, String apiUrl) {
        this.apiKey = apiKey;
        if (apiUrl != null && !apiUrl.isEmpty()) {
            this.apiUrl = apiUrl;
        }
    }
    
    /**
     * Get API URL for provider
     */
    private String getApiUrlForProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "groq":
                return "https://api.groq.com/openai/v1/chat/completions";
            case "huggingface":
                return "https://api-inference.huggingface.co/models/meta-llama/Llama-3-8b-chat-hf";
            case "together":
                return "https://api.together.xyz/v1/chat/completions";
            case "openai":
                return "https://api.openai.com/v1/chat/completions";
            default:
                return BuildConfig.GROQ_API_URL;
        }
    }
    
    /**
     * Check if AI service is configured
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
    
    /**
     * Request solution for an unknown algorithm problem
     */
    public void getSolution(String problemStatement, String language, AIResponseListener listener) {
        if (!isConfigured()) {
            if (listener != null) {
                listener.onError("AI service not configured. Please set API key in settings.");
            }
            return;
        }
        
        try {
            String prompt = buildPrompt(problemStatement, language);
            makeAPIRequest(prompt, listener);
        } catch (Exception e) {
            Log.e(TAG, "Error requesting AI solution", e);
            if (listener != null) {
                listener.onError("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Request solution with input values
     */
    public void getSolutionWithInputs(String problemStatement, String methodName, 
                                     Object[] inputs, String[] paramTypes, 
                                     AIResponseListener listener) {
        if (!isConfigured()) {
            if (listener != null) {
                listener.onError("AI service not configured. Please set API key in settings.");
            }
            return;
        }
        
        try {
            String prompt = buildPromptWithInputs(problemStatement, methodName, inputs, paramTypes);
            makeAPIRequest(prompt, listener);
        } catch (Exception e) {
            Log.e(TAG, "Error requesting AI solution with inputs", e);
            if (listener != null) {
                listener.onError("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Build prompt for AI request
     */
    private String buildPrompt(String problemStatement, String language) {
        return "You are an expert algorithm tutor. Solve the following problem step by step:\n\n" +
               "Problem: " + problemStatement + "\n\n" +
               "Please provide:\n" +
               "1. A clear explanation of the approach\n" +
               "2. Step-by-step solution\n" +
               "3. Complete " + language + " code implementation\n" +
               "4. Time and space complexity analysis\n\n" +
               "Format your response with clear sections.";
    }
    
    /**
     * Build prompt with input values
     */
    private String buildPromptWithInputs(String problemStatement, String methodName,
                                        Object[] inputs, String[] paramTypes) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert algorithm tutor. Solve the following problem:\n\n");
        prompt.append("Problem: ").append(problemStatement).append("\n\n");
        prompt.append("Method name: ").append(methodName).append("\n\n");
        prompt.append("Input parameters:\n");
        
        for (int i = 0; i < inputs.length && i < paramTypes.length; i++) {
            prompt.append("- ").append(paramTypes[i]).append(": ");
            if (inputs[i] != null) {
                prompt.append(inputs[i].toString());
            } else {
                prompt.append("null");
            }
            prompt.append("\n");
        }
        
        prompt.append("\nPlease provide:\n");
        prompt.append("1. A clear explanation of the approach\n");
        prompt.append("2. Step-by-step solution\n");
        prompt.append("3. Complete Java code implementation\n");
        prompt.append("4. Expected output for the given inputs\n");
        prompt.append("5. Time and space complexity analysis\n\n");
        prompt.append("Format your response with clear sections.");
        
        return prompt.toString();
    }
    
    /**
     * Make API request to LLM
     */
    private void makeAPIRequest(String prompt, AIResponseListener listener) {
        try {
            JSONObject requestBody = new JSONObject();
            
            // Get model based on provider
            SharedPreferences prefs = context.getSharedPreferences("ai_config", Context.MODE_PRIVATE);
            String provider = prefs.getString("provider", "groq");
            String model = getModelForProvider(provider);
            requestBody.put("model", model);
            
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            
            requestBody.put("messages", new org.json.JSONArray().put(message));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API request failed", e);
                    if (listener != null) {
                        listener.onError("Network error: " + e.getMessage());
                    }
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "API error: " + response.code() + " - " + errorBody);
                        if (listener != null) {
                            listener.onError("API error: " + response.code());
                        }
                        return;
                    }
                    
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        
                        // Parse OpenAI-style response
                        String content = parseAIResponse(jsonResponse);
                        
                        // Extract solution, explanation, and code
                        String[] parsed = parseSolutionContent(content);
                        
                        if (listener != null) {
                            listener.onSuccess(parsed[0], parsed[1], parsed[2]);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing AI response", e);
                        if (listener != null) {
                            listener.onError("Error parsing response: " + e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating API request", e);
            if (listener != null) {
                listener.onError("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Parse AI API response
     */
    private String parseAIResponse(JSONObject jsonResponse) throws Exception {
        // OpenAI format: { "choices": [{ "message": { "content": "..." } }] }
        if (jsonResponse.has("choices")) {
            org.json.JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                if (choice.has("message")) {
                    JSONObject message = choice.getJSONObject("message");
                    if (message.has("content")) {
                        return message.getString("content");
                    }
                }
            }
        }
        throw new Exception("Invalid response format");
    }
    
    /**
     * Parse solution content into explanation, solution, and code
     */
    private String[] parseSolutionContent(String content) {
        String explanation = "";
        String solution = "";
        String code = "";
        
        // Try to extract code blocks
        int codeStart = content.indexOf("```java");
        if (codeStart == -1) {
            codeStart = content.indexOf("```");
        }
        
        if (codeStart != -1) {
            int codeEnd = content.indexOf("```", codeStart + 3);
            if (codeEnd != -1) {
                code = content.substring(codeStart + 3, codeEnd).trim();
                if (code.startsWith("java")) {
                    code = code.substring(4).trim();
                }
                explanation = content.substring(0, codeStart).trim();
                solution = content.substring(codeEnd + 3).trim();
            } else {
                explanation = content;
            }
        } else {
            explanation = content;
        }
        
        return new String[]{solution, explanation, code};
    }
    
    /**
     * Get model name for provider
     */
    private String getModelForProvider(String provider) {
        switch (provider.toLowerCase()) {
            case "groq":
                return BuildConfig.GROQ_MODEL; // Default: "llama-3.1-8b-instant"
            case "huggingface":
                return "meta-llama/Llama-3-8b-chat-hf";
            case "together":
                return "meta-llama/Llama-3-8b-chat-hf";
            case "openai":
                return "gpt-3.5-turbo";
            default:
                return BuildConfig.GROQ_MODEL;
        }
    }
    
    /**
     * Clean up resources
     */
    public void destroy() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
        }
    }
}

