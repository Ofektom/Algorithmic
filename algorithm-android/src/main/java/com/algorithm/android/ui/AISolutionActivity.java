package com.algorithm.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import com.algorithm.android.R;
import com.algorithm.android.data.ProblemInfo;
import com.algorithm.android.service.AIFallbackService;
import com.algorithm.android.service.AlgorithmExecutor;

/**
 * Activity to display AI-generated solution for unknown algorithms
 */
public class AISolutionActivity extends AppCompatActivity {

    private TextView explanationTextView;
    private TextView solutionTextView;
    private TextView codeTextView;
    private TextView loadingTextView;
    private ProblemInfo problem;
    private String methodName;
    private Object[] inputs;
    private AIFallbackService aiService;
    private AlgorithmExecutor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_solution);

        // Get problem info from intent
        problem = getIntent().getParcelableExtra("PROBLEM");
        methodName = getIntent().getStringExtra("METHOD");
        String[] paramTypes = getIntent().getStringArrayExtra("PARAM_TYPES");
        
        // Reconstruct inputs from strings (simplified - in production, use proper serialization)
        String[] inputStrings = getIntent().getStringArrayExtra("INPUTS");
        if (inputStrings != null && paramTypes != null) {
            inputs = reconstructInputs(inputStrings, paramTypes);
        }

        aiService = new AIFallbackService(this);
        executor = AlgorithmExecutor.getInstance();
        executor.setContext(this); // Set context for reading assets

        initializeViews();
        setupToolbar();
        requestAISolution();
    }

    private void initializeViews() {
        explanationTextView = findViewById(R.id.explanationTextView);
        solutionTextView = findViewById(R.id.solutionTextView);
        codeTextView = findViewById(R.id.codeTextView);
        loadingTextView = findViewById(R.id.loadingTextView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String title = problem != null ? problem.name : "AI Solution";
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void requestAISolution() {
        if (!aiService.isConfigured()) {
            loadingTextView.setText("AI service not configured. Please set API key in settings.");
            return;
        }

        loadingTextView.setText("Requesting AI solution...");
        
        String problemStatement = problem != null ? problem.description : "Unknown problem";
        
        if (inputs != null && methodName != null) {
            String[] paramTypes = problem != null ? problem.paramTypes : new String[]{};
            aiService.getSolutionWithInputs(problemStatement, methodName, inputs, paramTypes,
                new AIFallbackService.AIResponseListener() {
                    @Override
                    public void onSuccess(String solution, String explanation, String code) {
                        runOnUiThread(() -> {
                            loadingTextView.setVisibility(android.view.View.GONE);
                            explanationTextView.setText(explanation);
                            solutionTextView.setText(solution);
                            codeTextView.setText(code.isEmpty() ? "Code not provided" : code);
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            loadingTextView.setText("Error: " + error);
                        });
                    }
                });
        } else {
            aiService.getSolution(problemStatement, "Java",
                new AIFallbackService.AIResponseListener() {
                    @Override
                    public void onSuccess(String solution, String explanation, String code) {
                        runOnUiThread(() -> {
                            loadingTextView.setVisibility(android.view.View.GONE);
                            explanationTextView.setText(explanation);
                            solutionTextView.setText(solution);
                            codeTextView.setText(code.isEmpty() ? "Code not provided" : code);
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            loadingTextView.setText("Error: " + error);
                        });
                    }
                });
        }
    }

    private Object[] reconstructInputs(String[] inputStrings, String[] paramTypes) {
        // Simplified reconstruction - in production, use proper serialization
        Object[] inputs = new Object[inputStrings.length];
        for (int i = 0; i < inputStrings.length && i < paramTypes.length; i++) {
            // Basic parsing - can be enhanced
            inputs[i] = inputStrings[i];
        }
        return inputs;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aiService != null) {
            aiService.destroy();
        }
    }
}

