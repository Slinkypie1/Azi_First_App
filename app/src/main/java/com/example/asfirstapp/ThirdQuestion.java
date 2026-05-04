package com.example.asfirstapp;

// Imports for activity management, UI, and modern layouts
import android.content.Intent;           // Navigate between screens
import android.os.Bundle;                // Stores activity state
import android.view.View;                // Base class for UI elements
import android.widget.Button;            // Represents clickable buttons
import android.widget.TextView;          // Displays text

import androidx.activity.EdgeToEdge;           // Enables edge-to-edge fullscreen layout
import androidx.appcompat.app.AppCompatActivity; // Base class for backward-compatible activities
import androidx.core.view.ViewCompat;          // Utilities for view insets

/**
 * ThirdQuestion Activity
 * ----------------------
 * Displays the third multiple-choice quiz question with four answer buttons.
 * Handles button clicks to navigate to either the correct answer screen or the failure screen.
 */
public class ThirdQuestion extends BaseMenuActivity implements View.OnClickListener {

    // UI components
    private TextView TV3;       // Displays the question text
    private Button BtClick11;   // Answer option 1
    private Button BtClick12;   // Answer option 2
    private Button BtClick13;   // Answer option 3 (CORRECT)
    private Button BtClick14;   // Answer option 4

    private long startTime;     // Records the time when the question is displayed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Load layout XML for this activity
        setContentView(R.layout.activity_third_question);

        // Start background music for Level 3 (ThirdQuestion screen)
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.third_question_music);
        startService(serviceIntent);

        // Record start time for timing how long user takes to answer
        startTime = System.currentTimeMillis();

        // Attach window inset listener to ensure UI is properly initialized after layout is drawn
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();   // Initialize UI components
            return insets; // Keep system insets unchanged
        });
    }

    /**
     * Initializes UI components and sets click listeners for all buttons
     */
    private void initViews() {
        // Link TextView from XML layout
        TV3 = findViewById(R.id.TV3);

        // Link answer buttons from XML layout
        BtClick11 = findViewById(R.id.BtClick11);
        BtClick12 = findViewById(R.id.BtClick12);
        BtClick13 = findViewById(R.id.BtClick13); // Correct answer
        BtClick14 = findViewById(R.id.BtClick14);

        // Set click listener for all buttons
        BtClick11.setOnClickListener(this);
        BtClick12.setOnClickListener(this);
        BtClick13.setOnClickListener(this);
        BtClick14.setOnClickListener(this);
    }

    /**
     * Handles button click events for all answer options
     */
    @Override
    public void onClick(View view) {
        if (view == BtClick13) {
            // Correct answer selected

            // Calculate time taken to answer question
            long timeTaken = System.currentTimeMillis() - startTime;

            // Navigate to success screen and pass time
            Intent intent = new Intent(this, CorrectScreen3.class);
            intent.putExtra("TIME_TAKEN", timeTaken);
            startActivity(intent);

        } else {
            // Wrong answer selected

            // Navigate to failure screen
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
        }
    }
}