package com.example.asfirstapp;

// Package declaration – must match your app’s structure

import android.content.Intent;      // Used to navigate between activities
import android.os.Bundle;           // Stores activity state if recreated
import android.view.View;           // Base class for UI widgets
import android.widget.Button;       // UI element for clickable buttons
import android.widget.TextView;     // UI element for displaying text

import androidx.activity.EdgeToEdge;           // Enables edge-to-edge fullscreen layout
import androidx.appcompat.app.AppCompatActivity; // Base AppCompat activity
import androidx.core.view.ViewCompat;          // Backwards-compatible view utilities

/**
 * Third Activity
 * --------------
 * Shows a multiple-choice question with four buttons.
 * Handles clicks and navigates to correct/failure screens.
 */
public class Third extends BaseMenuActivity implements View.OnClickListener {

    private TextView TV1;       // Displays the question text
    private Button BtClick2;    // Answer option 1
    private Button BtClick3;    // Answer option 2 (CORRECT)
    private Button BtClick4;    // Answer option 3
    private Button BtClick5;    // Answer option 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Load layout XML for this activity
        setContentView(R.layout.activity_third);

        // Apply insets listener to handle status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            initViews();   // Initialize TextView and Buttons after layout
            return insets; // Return insets unchanged
        });
    }

    /**
     * Initialize UI components and attach click listeners
     */
    private void initViews() {
        // Connect views to layout XML
        TV1 = findViewById(R.id.TV1);
        BtClick2 = findViewById(R.id.BtClick2);
        BtClick3 = findViewById(R.id.BtClick3);
        BtClick4 = findViewById(R.id.BtClick4);
        BtClick5 = findViewById(R.id.BtClick5);

        // Set this activity as the click listener for all buttons
        BtClick2.setOnClickListener(this);
        BtClick3.setOnClickListener(this); // Correct answer
        BtClick4.setOnClickListener(this);
        BtClick5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Handle button clicks

        if (view == BtClick3) {
            // Correct answer selected
            Intent intent = new Intent(this, CorrectScreen1.class);
            startActivity(intent); // Navigate to success screen
        } else {
            // Wrong answer selected
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent); // Navigate to failure screen
        }
    }
}
