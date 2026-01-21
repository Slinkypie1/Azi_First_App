package com.example.asfirstapp;

// Package declaration – must match your app's structure

import android.content.Intent;      // Used to navigate between activities
import android.os.Bundle;           // Stores activity state if recreated
import android.view.View;           // Base class for UI widgets
import android.widget.Button;       // UI element for clickable buttons
import android.widget.TextView;     // UI element for displaying text

import androidx.activity.EdgeToEdge;           // Enables edge-to-edge fullscreen layout
import androidx.appcompat.app.AppCompatActivity; // Base AppCompat activity
import androidx.core.view.ViewCompat;          // Backwards-compatible view utilities

/**
 * ThirdQuestion Activity
 * ----------------------
 * Shows the third multiple-choice quiz question with four buttons.
 * Handles button clicks to navigate to correct or failure screens.
 */
public class ThirdQuestion extends BaseMenuActivity implements View.OnClickListener {

    private TextView TV3;       // Displays the question text
    private Button BtClick11;   // Answer option 1
    private Button BtClick12;   // Answer option 2
    private Button BtClick13;   // Answer option 3 (CORRECT)
    private Button BtClick14;   // Answer option 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Load layout XML for this activity
        setContentView(R.layout.activity_third_question);

        // Apply insets listener to handle status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();   // Initialize TextView and Buttons after layout
            return insets; // Return insets unchanged
        });
    }

    /**
     * Initialize UI components and attach click listeners
     */
    private void initViews() {
        // Connect views to layout XML
        TV3 = findViewById(R.id.TV3);
        BtClick11 = findViewById(R.id.BtClick11);
        BtClick12 = findViewById(R.id.BtClick12);
        BtClick13 = findViewById(R.id.BtClick13); // Correct answer
        BtClick14 = findViewById(R.id.BtClick14);

        // Set this activity as the click listener for all buttons
        BtClick11.setOnClickListener(this);
        BtClick12.setOnClickListener(this);
        BtClick13.setOnClickListener(this); // Correct answer
        BtClick14.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Handle button clicks
        if (view == BtClick13) {
            // Correct answer selected
            Intent intent = new Intent(this, CorrectScreen3.class);
            startActivity(intent); // Navigate to success screen
        } else {
            // Wrong answer selected
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent); // Navigate to failure screen
        }
    }
}
