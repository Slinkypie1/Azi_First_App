package com.example.asfirstapp;

// Imports for Android UI, activity management, and modern layouts
import android.content.Intent;           // Used to navigate to other screens
import android.os.Bundle;                // Stores activity state
import android.view.View;                // Base class for UI elements and click handling
import android.widget.Button;            // Represents clickable buttons
import android.widget.TextView;          // Displays text

import androidx.activity.EdgeToEdge;           // Enables modern edge-to-edge fullscreen layouts
import androidx.appcompat.app.AppCompatActivity; // Base class for backward-compatible activities
import androidx.core.view.ViewCompat;          // Utilities for view insets and padding

/**
 * SecondQuestion Activity
 * -----------------------
 * This is a multiple-choice quiz screen (Question 2).
 * The user selects one of four answers.
 * One is correct and leads to a success screen,
 * others lead to a failure screen.
 */
public class SecondQuestion extends BaseMenuActivity implements View.OnClickListener {

    // UI elements
    private TextView TV2;       // Displays the question text on screen
    private Button BtClick6;    // Answer option 1
    private Button BtClick7;    // Answer option 2 (correct answer)
    private Button BtClick8;    // Answer option 3
    private Button BtClick9;    // Answer option 4

    // Timing variable to measure how fast the user answered
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout (content extends behind system bars)
        EdgeToEdge.enable(this);

        // Load layout XML for this screen
        setContentView(R.layout.activity_second_question);

        // Start background music for this level
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.second_question_music);
        startService(serviceIntent);

        // Record when this question started (used for score/time tracking)
        startTime = System.currentTimeMillis();

        // Attach UI initialization after view layout is ready
        // This ensures findViewById works correctly with full layout hierarchy
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            // Initialize all buttons and text views
            initViews();

            // Return system insets unchanged
            return insets;
        });
    }

    /**
     * Connects XML UI elements to Java variables
     * and sets click listeners for all answer buttons
     */
    private void initViews() {

        // Connect question text view
        TV2 = findViewById(R.id.TV2);

        // Connect answer buttons
        BtClick6 = findViewById(R.id.BtClick6);
        BtClick7 = findViewById(R.id.BtClick7);
        BtClick8 = findViewById(R.id.BtClick8);
        BtClick9 = findViewById(R.id.BtClick9);

        // Set click listener for all buttons
        BtClick6.setOnClickListener(this);
        BtClick7.setOnClickListener(this);
        BtClick8.setOnClickListener(this);
        BtClick9.setOnClickListener(this);
    }

    /**
     * Handles button clicks for all answer choices
     */
    @Override
    public void onClick(View view) {

        // If correct answer is selected
        if(view == BtClick7){

            // Calculate time taken to answer the question
            long timeTaken = System.currentTimeMillis() - startTime;

            // Go to success screen
            Intent intent = new Intent(this, CorrectScreen2.class);

            // Pass time taken to next screen
            intent.putExtra("TIME_TAKEN", timeTaken);

            // Start success activity
            startActivity(intent);

        } else {
            // Wrong answer selected → go to failure screen
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
        }
    }
}