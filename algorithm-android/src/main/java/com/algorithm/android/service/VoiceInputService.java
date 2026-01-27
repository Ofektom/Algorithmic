package com.algorithm.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Service to handle voice input using Android's SpeechRecognizer
 */
public class VoiceInputService {
    
    private static final String TAG = "VoiceInputService";
    private SpeechRecognizer speechRecognizer;
    private VoiceInputListener listener;
    private Context context;
    private boolean isListening = false;
    
    public interface VoiceInputListener {
        void onResult(String result);
        void onError(String error);
        void onListeningStarted();
        void onListeningStopped();
    }
    
    public VoiceInputService(Context context) {
        this.context = context;
        initializeSpeechRecognizer();
    }
    
    private void initializeSpeechRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Log.d(TAG, "Ready for speech");
                    if (listener != null) {
                        listener.onListeningStarted();
                    }
                }
                
                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "Beginning of speech");
                }
                
                @Override
                public void onRmsChanged(float rmsdB) {
                    // Audio level changed
                }
                
                @Override
                public void onBufferReceived(byte[] buffer) {
                    // Audio buffer received
                }
                
                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "End of speech");
                    isListening = false;
                    if (listener != null) {
                        listener.onListeningStopped();
                    }
                }
                
                @Override
                public void onError(int error) {
                    isListening = false;
                    String errorMessage = getErrorMessage(error);
                    Log.e(TAG, "Speech recognition error: " + errorMessage);
                    if (listener != null) {
                        listener.onError(errorMessage);
                    }
                }
                
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String result = matches.get(0);
                        Log.d(TAG, "Speech recognition result: " + result);
                        if (listener != null) {
                            listener.onResult(result);
                        }
                    } else {
                        if (listener != null) {
                            listener.onError("No speech detected");
                        }
                    }
                    isListening = false;
                }
                
                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        // Could show partial results in real-time
                        Log.d(TAG, "Partial result: " + matches.get(0));
                    }
                }
                
                @Override
                public void onEvent(int eventType, Bundle params) {
                    // Additional events
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing speech recognizer", e);
            speechRecognizer = null;
        }
    }
    
    /**
     * Start listening for voice input
     */
    public void startListening(VoiceInputListener listener) {
        if (speechRecognizer == null) {
            if (listener != null) {
                listener.onError("Speech recognition not available");
            }
            return;
        }
        
        if (isListening) {
            stopListening();
        }
        
        this.listener = listener;
        isListening = true;
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        
        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting speech recognition", e);
            isListening = false;
            if (listener != null) {
                listener.onError("Failed to start listening: " + e.getMessage());
            }
        }
    }
    
    /**
     * Stop listening for voice input
     */
    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.stopListening();
            isListening = false;
        }
    }
    
    /**
     * Cancel ongoing recognition
     */
    public void cancel() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.cancel();
            isListening = false;
        }
    }
    
    /**
     * Check if speech recognition is available
     */
    public static boolean isRecognitionAvailable(Context context) {
        return SpeechRecognizer.isRecognitionAvailable(context);
    }
    
    /**
     * Check if this instance is available
     */
    public boolean isAvailable() {
        return speechRecognizer != null;
    }
    
    /**
     * Check if currently listening
     */
    public boolean isListening() {
        return isListening;
    }
    
    /**
     * Destroy the service and release resources
     */
    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        listener = null;
        isListening = false;
    }
    
    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No match found";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input";
            default:
                return "Unknown error";
        }
    }
}

