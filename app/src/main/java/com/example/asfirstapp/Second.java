package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

public class Second extends BaseMenuActivity implements View.OnClickListener {

    TextView TV;
    EditText ET;
    Button BtCLick1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        // Initialize views after layout insets are applied
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            initViews();
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the latest name whenever activity resumes

    }

    // Initialize views
    private void initViews() {
        TV = findViewById(R.id.TV);
        ET = findViewById(R.id.ET);
        BtCLick1 = findViewById(R.id.BtClick1);

        updateNameDisplay(); // Show latest name
        BtCLick1.setOnClickListener(this);
    }

    // Update TextView with last typed name
    private void updateNameDisplay() {
        String lastName = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("last_name", "Player");
        TV.setText("Ready " + lastName + "?");
    }

    @Override
    public void onClick(View view) {
        // Button click always starts level 1
        startLevel(1);
    }
}
