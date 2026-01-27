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
import com.algorithm.android.service.ProblemRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display list of problems in a selected category
 */
public class ProblemListActivity extends AppCompatActivity {

    private RecyclerView problemRecyclerView;
    private ProblemRegistry problemRegistry;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_problem_list);

            // Get category from intent
            category = getIntent().getStringExtra("CATEGORY");
            if (category == null) {
                android.util.Log.e("ProblemListActivity", "Category is null");
                finish();
                return;
            }

            // Initialize ProblemRegistry
            problemRegistry = ProblemRegistry.getInstance();
            if (problemRegistry == null) {
                android.util.Log.e("ProblemListActivity", "ProblemRegistry is null");
                Toast.makeText(this, "Error: Problem registry not initialized", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            initializeViews();
            setupToolbar();
            setupRecyclerView();
        } catch (Exception e) {
            android.util.Log.e("ProblemListActivity", "Error in onCreate", e);
            Toast.makeText(this, "Error loading problems: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void initializeViews() {
        problemRecyclerView = findViewById(R.id.problemRecyclerView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        try {
            List<ProblemInfo> problems = problemRegistry.getProblemsForCategory(category);
            
            if (problems == null) {
                android.util.Log.w("ProblemListActivity", "Problems list is null for category: " + category);
                problems = new ArrayList<>();
            }
            
            if (problems.isEmpty()) {
                android.util.Log.w("ProblemListActivity", "No problems found for category: " + category);
                Toast.makeText(this, "No problems found for: " + category, Toast.LENGTH_SHORT).show();
            }
            
            ProblemAdapter adapter = new ProblemAdapter(problems, this::onProblemSelected);
            problemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            problemRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            android.util.Log.e("ProblemListActivity", "Error setting up RecyclerView", e);
            Toast.makeText(this, "Error loading problem list: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void onProblemSelected(ProblemInfo problem) {
        try {
            if (problem == null) {
                android.util.Log.e("ProblemListActivity", "Selected problem is null");
                Toast.makeText(this, "Error: Problem information is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate problem data before navigating
            if (problem.name == null || problem.name.isEmpty()) {
                android.util.Log.e("ProblemListActivity", "Problem name is null or empty");
                Toast.makeText(this, "Error: Invalid problem data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (problem.methods == null || problem.methods.length == 0) {
                android.util.Log.w("ProblemListActivity", "Problem has no methods: " + problem.name);
                Toast.makeText(this, "No methods available for: " + problem.name, Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to MethodSelectionActivity
            Intent intent = new Intent(this, MethodSelectionActivity.class);
            intent.putExtra("PROBLEM", problem);
            startActivity(intent);
        } catch (Exception e) {
            android.util.Log.e("ProblemListActivity", "Error navigating to method selection", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

