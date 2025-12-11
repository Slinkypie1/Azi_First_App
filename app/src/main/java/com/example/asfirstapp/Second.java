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

    private TextView TV;
    private EditText ET;
    private Button BtClick1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        // Initialize views immediately after layout is set
        initViews();

        // Keep edge-to-edge padding if needed but DO NOT initialize views here
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload latest name when activity returns
        updateNameDisplay();
    }

    // Initialize all views and listeners
    private void initViews() {
        TV = findViewById(R.id.TV);
        ET = findViewById(R.id.ET);
        BtClick1 = findViewById(R.id.BtClick1);

        updateNameDisplay();

        BtClick1.setOnClickListener(this);
    }

    // Update welcome message using SharedPreferences
    private void updateNameDisplay() {
        String lastName = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("last_name", "Player");

        TV.setText("Ready " + lastName + "?");
    }

    @Override
    public void onClick(View view) {
        // Always start Level 1
         startLevel(1);
    }
}
