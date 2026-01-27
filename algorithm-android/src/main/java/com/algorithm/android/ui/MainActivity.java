package com.algorithm.android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.algorithm.android.R;
import com.algorithm.android.service.ProblemRegistry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Main Activity for Algorithm Solver Android App
 * This is the entry point and displays the category selection screen
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;
    
    private RecyclerView categoryRecyclerView;
    private FloatingActionButton voiceButton;
    private ProblemRegistry problemRegistry;
    private VoiceInputHandler voiceInputHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ProblemRegistry
        problemRegistry = ProblemRegistry.getInstance();
        
        // Initialize VoiceInputHandler
        voiceInputHandler = new VoiceInputHandler(this, new VoiceInputHandler.VoiceNavigationListener() {
            @Override
            public void onCategorySelected(String category) {
                navigateToCategory(category);
            }
            
            @Override
            public void onProblemSelected(com.algorithm.android.data.ProblemInfo problem) {
                try {
                    if (problem == null) {
                        android.util.Log.e("MainActivity", "Voice selected problem is null");
                        Toast.makeText(MainActivity.this, "Error: Problem not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate problem data before navigating
                    if (problem.name == null || problem.name.isEmpty()) {
                        android.util.Log.e("MainActivity", "Problem name is null or empty");
                        Toast.makeText(MainActivity.this, "Error: Invalid problem data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (problem.methods == null || problem.methods.length == 0) {
                        android.util.Log.w("MainActivity", "Problem has no methods: " + problem.name);
                        Toast.makeText(MainActivity.this, "No methods available for: " + problem.name, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Navigate directly to MethodSelectionActivity with the selected problem
                    Intent intent = new Intent(MainActivity.this, MethodSelectionActivity.class);
                    intent.putExtra("PROBLEM", problem);
                    startActivity(intent);
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error navigating to problem", e);
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onMethodSelected(com.algorithm.android.data.ProblemInfo problem, String methodName) {
                // Not used in MainActivity
            }
            
            @Override
            public void onInputProvided(com.algorithm.android.data.ProblemInfo problem, String methodName, String input) {
                // Not used in MainActivity
            }
        });

        initializeViews();
        setupRecyclerView();
        setupVoiceButton();
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
            } else {
                Toast.makeText(this, "Voice input requires microphone permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void navigateToCategory(String category) {
        String[] categories = problemRegistry.getCategories();
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                onCategorySelected(i);
                break;
            }
        }
    }

    private void initializeViews() {
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        voiceButton = findViewById(R.id.voiceButton);
    }

    private void setupRecyclerView() {
        String[] categories = problemRegistry.getCategories();
        CategoryAdapter adapter = new CategoryAdapter(categories, this::onCategorySelected);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(adapter);
    }

    private void setupVoiceButton() {
        voiceButton.setOnClickListener(v -> {
            if (voiceInputHandler != null && voiceInputHandler.isAvailable()) {
                voiceInputHandler.startListening();
            } else {
                Toast.makeText(this, "Voice input not available. Please check microphone permission.", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voiceInputHandler != null) {
            voiceInputHandler.destroy();
        }
    }

    private void onCategorySelected(int position) {
        String[] categories = problemRegistry.getCategories();
        if (position >= 0 && position < categories.length) {
            String category = categories[position];
            
            // Navigate to ProblemListActivity
            Intent intent = new Intent(this, ProblemListActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        }
    }
}

