package com.algorithm.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.algorithm.android.R;
import com.algorithm.android.data.ProblemInfo;

/**
 * Activity to display available solution methods for a selected problem
 */
public class MethodSelectionActivity extends AppCompatActivity {

    private RecyclerView methodRecyclerView;
    private ProblemInfo problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_method_selection);

            // Get problem from intent
            problem = getIntent().getParcelableExtra("PROBLEM");
            if (problem == null) {
                android.util.Log.e("MethodSelectionActivity", "Problem is null");
                Toast.makeText(this, "Error: Problem information not found", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Validate problem data
            if (problem.name == null || problem.name.isEmpty()) {
                android.util.Log.e("MethodSelectionActivity", "Problem name is null or empty");
                Toast.makeText(this, "Error: Invalid problem data", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (problem.methods == null || problem.methods.length == 0) {
                android.util.Log.e("MethodSelectionActivity", "Problem methods is null or empty for: " + problem.name);
                Toast.makeText(this, "Error: No methods available for this problem", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            initializeViews();
            setupToolbar();
            setupRecyclerView();
        } catch (Exception e) {
            android.util.Log.e("MethodSelectionActivity", "Error in onCreate", e);
            Toast.makeText(this, "Error loading methods: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void initializeViews() {
        methodRecyclerView = findViewById(R.id.methodRecyclerView);
        if (methodRecyclerView == null) {
            throw new RuntimeException("methodRecyclerView not found in layout");
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar == null) {
                android.util.Log.w("MethodSelectionActivity", "Toolbar not found in layout");
                return;
            }
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(problem.name != null ? problem.name : "Methods");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            android.util.Log.e("MethodSelectionActivity", "Error setting up toolbar", e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (problem.methods == null || problem.methods.length == 0) {
                android.util.Log.e("MethodSelectionActivity", "Methods array is null or empty");
                Toast.makeText(this, "No methods available", Toast.LENGTH_SHORT).show();
                return;
            }

            MethodAdapter adapter = new MethodAdapter(problem.methods, this::onMethodSelected);
            methodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            methodRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            android.util.Log.e("MethodSelectionActivity", "Error setting up RecyclerView", e);
            Toast.makeText(this, "Error loading methods: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void onMethodSelected(String methodName) {
        // Navigate to InputActivity
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra("PROBLEM", problem);
        intent.putExtra("METHOD", methodName);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

