package com.algorithm.android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.algorithm.android.R;
import com.algorithm.android.data.ProblemInfo;
import com.algorithm.android.service.AlgorithmExecutor;
import com.algorithm.android.service.VoiceInputService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity for user to provide input values for the algorithm
 */
public class InputActivity extends AppCompatActivity {

    private LinearLayout inputContainer;
    private Button useExampleButton;
    private Button executeButton;
    private ProblemInfo problem;
    private String methodName;
    private EditText[] inputFields;
    private AlgorithmExecutor executor;
    private VoiceInputService voiceService;
    private int currentInputFieldIndex = -1;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // Get problem and method from intent
        problem = getIntent().getParcelableExtra("PROBLEM");
        methodName = getIntent().getStringExtra("METHOD");
        
        if (problem == null || methodName == null) {
            finish();
            return;
        }

        executor = AlgorithmExecutor.getInstance();
        executor.setContext(this); // Set context for reading assets
        
        // Initialize voice service
        if (VoiceInputService.isRecognitionAvailable(this)) {
            voiceService = new VoiceInputService(this);
        }

        initializeViews();
        setupToolbar();
        setupInputFields();
        setupButtons();
        checkPermissions();
    }
    
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Voice input enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeViews() {
        inputContainer = findViewById(R.id.inputContainer);
        useExampleButton = findViewById(R.id.useExampleButton);
        executeButton = findViewById(R.id.executeButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(problem.name + " - " + formatMethodName(methodName));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupInputFields() {
        inputFields = new EditText[problem.paramNames.length];
        
        for (int i = 0; i < problem.paramNames.length; i++) {
            // Create horizontal layout for input field and voice button
            LinearLayout fieldLayout = new LinearLayout(this);
            fieldLayout.setOrientation(LinearLayout.HORIZONTAL);
            fieldLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
            
            // Create label
            TextView label = new TextView(this);
            label.setText(problem.paramNames[i] + " (" + problem.paramTypes[i] + ")");
            label.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            label.setPadding(0, 16, 0, 8);
            
            // Create input field
            EditText editText = new EditText(this);
            editText.setHint(getInputHint(problem.paramTypes[i]));
            editText.setInputType(getInputType(problem.paramTypes[i]));
            editText.setId(View.generateViewId());
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            
            inputFields[i] = editText;
            
            // Create voice button for this field
            ImageButton voiceButton = new ImageButton(this);
            voiceButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            voiceButton.setContentDescription("Voice input for " + problem.paramNames[i]);
            voiceButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
            
            final int fieldIndex = i;
            voiceButton.setOnClickListener(v -> startVoiceInputForField(fieldIndex));
            
            // Add to field layout
            fieldLayout.addView(editText);
            fieldLayout.addView(voiceButton);
            
            // Add to container
            inputContainer.addView(label);
            inputContainer.addView(fieldLayout);
        }
    }
    
    private void startVoiceInputForField(int fieldIndex) {
        if (voiceService == null || !voiceService.isAvailable()) {
            Toast.makeText(this, "Voice input not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentInputFieldIndex = fieldIndex;
        voiceService.startListening(new VoiceInputService.VoiceInputListener() {
            @Override
            public void onResult(String result) {
                if (currentInputFieldIndex >= 0 && currentInputFieldIndex < inputFields.length) {
                    inputFields[currentInputFieldIndex].setText(result);
                }
                currentInputFieldIndex = -1;
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(InputActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                currentInputFieldIndex = -1;
            }
            
            @Override
            public void onListeningStarted() {
                Toast.makeText(InputActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onListeningStopped() {
                // Listening stopped
            }
        });
    }

    private void setupButtons() {
        useExampleButton.setOnClickListener(v -> fillExampleInputs());
        executeButton.setOnClickListener(v -> executeAlgorithm());
    }

    private void fillExampleInputs() {
        if (problem.exampleInputs == null) {
            return;
        }
        
        for (int i = 0; i < inputFields.length && i < problem.exampleInputs.length; i++) {
            Object example = problem.exampleInputs[i];
            if (example != null) {
                inputFields[i].setText(executor.formatInputValue(example));
            }
        }
    }

    private void executeAlgorithm() {
        try {
            // Parse inputs
            Object[] inputs = parseInputs();
            
            // Execute algorithm directly and pass result
            AlgorithmExecutor executor = AlgorithmExecutor.getInstance();
            AlgorithmExecutor.ExecutionResult result = executor.execute(problem, methodName, inputs);
            
            // Navigate to ResultActivity with result
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("PROBLEM", problem);
            intent.putExtra("METHOD", methodName);
            intent.putExtra("RESULT", result.result != null ? result.result.toString() : "null");
            intent.putExtra("EXECUTION_TIME", result.executionTimeMs);
            intent.putExtra("SUCCESS", result.success);
            intent.putExtra("ERROR", result.error != null ? result.error : "");
            startActivity(intent);
            
        } catch (Exception e) {
            // Show error
            android.widget.Toast.makeText(this, "Error parsing inputs: " + e.getMessage(), 
                android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private Object[] parseInputs() throws Exception {
        Object[] inputs = new Object[inputFields.length];
        
        for (int i = 0; i < inputFields.length; i++) {
            String input = inputFields[i].getText().toString().trim();
            String type = problem.paramTypes[i];
            
            inputs[i] = parseInput(input, type);
        }
        
        return inputs;
    }

    private Object parseInput(String input, String type) throws Exception {
        if (type.equals("int[]")) {
            return parseIntArray(input);
        } else if (type.equals("String[]")) {
            return parseStringArray(input);
        } else if (type.equals("String")) {
            return input;
        } else if (type.equals("int")) {
            return Integer.parseInt(input);
        } else if (type.equals("char[]")) {
            return input.toCharArray();
        } else if (type.contains("List")) {
            // For List types, parse as comma-separated strings
            String[] parts = input.split(",");
            List<String> list = new ArrayList<>();
            for (String part : parts) {
                list.add(part.trim());
            }
            return list;
        }
        
        throw new Exception("Unsupported type: " + type);
    }

    private int[] parseIntArray(String input) {
        input = input.trim();
        // Remove brackets if present
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1);
        }
        
        String[] parts = input.split("[,\\s]+");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    private String[] parseStringArray(String input) {
        input = input.trim();
        // Remove brackets if present
        if (input.startsWith("[") && input.endsWith("]")) {
            input = input.substring(1, input.length() - 1);
        }
        
        // Split by comma or space
        String[] parts = input.split("[,\\s]+");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim().replaceAll("^[\"']|[\"']$", "");
        }
        return parts;
    }

    private String getInputHint(String type) {
        switch (type) {
            case "int[]":
                return "e.g., [1,2,3] or 1,2,3";
            case "String[]":
                return "e.g., [\"a\",\"b\"] or a,b,c";
            case "String":
                return "Enter text";
            case "int":
                return "Enter number";
            case "char[]":
                return "e.g., abc or a,b,c";
            default:
                return "Enter value";
        }
    }

    private int getInputType(String type) {
        if (type.equals("int") || type.equals("int[]")) {
            return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
        }
        return InputType.TYPE_CLASS_TEXT;
    }

    private String formatMethodName(String methodName) {
        if (methodName.startsWith("compute")) {
            String rest = methodName.substring(7);
            if (rest.startsWith("With")) {
                rest = rest.substring(4);
            }
            return rest.replaceAll("([A-Z])", " $1").trim();
        }
        return methodName;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voiceService != null) {
            voiceService.destroy();
        }
    }
}

