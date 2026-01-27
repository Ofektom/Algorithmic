package com.algorithm.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import com.algorithm.android.R;
import com.algorithm.android.data.ProblemInfo;
import com.algorithm.android.service.AlgorithmExecutor;
import com.algorithm.android.service.ProblemRegistry;

/**
 * Activity to display algorithm execution results and source code
 */
public class ResultActivity extends AppCompatActivity {

    private TextView resultTextView;
    private TextView executionTimeTextView;
    private TextView sourceCodeTextView;
    private ProblemInfo problem;
    private String methodName;
    private AlgorithmExecutor executor;
    private String resultString;
    private long executionTimeMs;
    private boolean success;
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get problem, method, and result from intent
        problem = getIntent().getParcelableExtra("PROBLEM");
        methodName = getIntent().getStringExtra("METHOD");
        resultString = getIntent().getStringExtra("RESULT");
        executionTimeMs = getIntent().getLongExtra("EXECUTION_TIME", 0);
        success = getIntent().getBooleanExtra("SUCCESS", false);
        error = getIntent().getStringExtra("ERROR");
        
        if (problem == null || methodName == null) {
            finish();
            return;
        }

        executor = AlgorithmExecutor.getInstance();
        executor.setContext(this); // Set context for reading assets

        initializeViews();
        setupToolbar();
        displayResults();
    }

    private void initializeViews() {
        resultTextView = findViewById(R.id.resultTextView);
        executionTimeTextView = findViewById(R.id.executionTimeTextView);
        sourceCodeTextView = findViewById(R.id.sourceCodeTextView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(problem.name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ai_solution) {
            // Check if problem exists in registry
            ProblemRegistry registry = ProblemRegistry.getInstance();
            ProblemInfo foundProblem = registry.findProblem(problem.name);
            
            if (foundProblem == null) {
                // Problem not in registry - show AI solution
                Intent intent = new Intent(this, AISolutionActivity.class);
                intent.putExtra("PROBLEM", problem);
                intent.putExtra("METHOD", methodName);
                intent.putExtra("PARAM_TYPES", problem.paramTypes);
                startActivity(intent);
            } else {
                // Problem exists - AI solution is optional
                android.widget.Toast.makeText(this, 
                    "This problem is already in the app. AI solution available as alternative.", 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayResults() {
        if (success) {
            // Display result
            resultTextView.setText(resultString != null ? resultString : "null");
            executionTimeTextView.setText(
                String.format("Execution Time: %.4f ms", executionTimeMs / 1000.0));
            
            // Display source code
            String sourceCode = executor.getMethodSourceCode(problem.className, methodName);
            sourceCodeTextView.setText(sourceCode);
            
        } else {
            // Display error
            resultTextView.setText("Error: " + (error != null ? error : "Unknown error"));
            executionTimeTextView.setText("");
            sourceCodeTextView.setText("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

