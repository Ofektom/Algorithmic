package com.algorithm.android.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.algorithm.android.R;
import com.algorithm.android.service.AIFallbackService;

/**
 * Settings Activity for configuring AI API keys
 */
public class SettingsActivity extends AppCompatActivity {

    private EditText apiKeyEditText;
    private Spinner providerSpinner;
    private TextView providerInfoText;
    private Button saveButton;
    private Button testButton;
    private SharedPreferences prefs;
    private AIFallbackService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("ai_config", MODE_PRIVATE);
        aiService = new AIFallbackService(this);

        initializeViews();
        setupToolbar();
        loadSavedConfig();
        setupListeners();
    }

    private void initializeViews() {
        apiKeyEditText = findViewById(R.id.apiKeyEditText);
        providerSpinner = findViewById(R.id.providerSpinner);
        providerInfoText = findViewById(R.id.providerInfoText);
        saveButton = findViewById(R.id.saveButton);
        testButton = findViewById(R.id.testButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadSavedConfig() {
        // Setup spinner with provider options
        android.widget.ArrayAdapter<CharSequence> adapter = android.widget.ArrayAdapter.createFromResource(
            this,
            R.array.ai_providers,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        providerSpinner.setAdapter(adapter);
        
        // Check if there's a runtime override, otherwise show BuildConfig value
        String savedKey = prefs.getString("api_key", "");
        String savedProvider = prefs.getString("provider", "groq");
        
        // If no runtime key, show BuildConfig key (from gradle.properties)
        if (savedKey.isEmpty()) {
            savedKey = com.algorithm.android.BuildConfig.GROQ_API_KEY;
            if (savedKey != null && !savedKey.isEmpty()) {
                // Show that it's from BuildConfig
                apiKeyEditText.setHint("Using key from gradle.properties (configured)");
            }
        }
        
        apiKeyEditText.setText(savedKey);
        
        // Set spinner position based on saved provider
        String[] providers = {"groq", "huggingface", "together", "openai"};
        for (int i = 0; i < providers.length; i++) {
            if (providers[i].equals(savedProvider)) {
                providerSpinner.setSelection(i);
                break;
            }
        }
        
        updateProviderInfo();
    }

    private void setupListeners() {
        providerSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                updateProviderInfo();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        saveButton.setOnClickListener(v -> saveConfig());
        testButton.setOnClickListener(v -> testConnection());
    }

    private void updateProviderInfo() {
        int position = providerSpinner.getSelectedItemPosition();
        String[] info = {
            "Groq: Fast, free, no credit card needed.\nGet key: console.groq.com",
            "Hugging Face: Limited free tier (~$0.10/month).\nGet token: huggingface.co/settings/tokens",
            "Together AI: Free tier available.\nGet key: api.together.xyz",
            "OpenAI: Paid service.\nGet key: platform.openai.com/api-keys"
        };
        if (position >= 0 && position < info.length) {
            providerInfoText.setText(info[position]);
        }
    }

    private void saveConfig() {
        String apiKey = apiKeyEditText.getText().toString().trim();
        int position = providerSpinner.getSelectedItemPosition();
        String[] providers = {"groq", "huggingface", "together", "openai"};
        String provider = position >= 0 && position < providers.length ? providers[position] : "groq";
        
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "Please enter an API key", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Store in SharedPreferences
        prefs.edit()
            .putString("api_key", apiKey)
            .putString("provider", provider)
            .apply();
        
        // Update AIFallbackService
        String apiUrl = getApiUrl(provider);
        aiService.setApiConfig(apiKey, apiUrl);
        
        Toast.makeText(this, "API key saved!", Toast.LENGTH_SHORT).show();
    }

    private void testConnection() {
        String apiKey = apiKeyEditText.getText().toString().trim();
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "Please enter an API key first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int position = providerSpinner.getSelectedItemPosition();
        String[] providers = {"groq", "huggingface", "together", "openai"};
        String provider = position >= 0 && position < providers.length ? providers[position] : "groq";
        String apiUrl = getApiUrl(provider);
        
        // Temporarily set config for testing
        aiService.setApiConfig(apiKey, apiUrl);
        
        // Test with a simple request
        Toast.makeText(this, "Testing connection...", Toast.LENGTH_SHORT).show();
        aiService.getSolution("Test: What is 2+2?", "Java", new AIFallbackService.AIResponseListener() {
            @Override
            public void onSuccess(String solution, String explanation, String code) {
                runOnUiThread(() -> {
                    Toast.makeText(SettingsActivity.this, "Connection successful!", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(SettingsActivity.this, "Connection failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String getApiUrl(String provider) {
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
                return "https://api.groq.com/openai/v1/chat/completions";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

