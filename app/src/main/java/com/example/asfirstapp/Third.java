package com.example.asfirstapp;

// Imports for activity management, UI, and modern layouts
import android.content.Intent;           // Used to navigate between screens
import android.os.Bundle;                // Stores activity state
import android.view.View;                // Base class for UI elements and click handling
import android.widget.Button;            // Represents clickable buttons
import android.widget.TextView;          // Displays text

import androidx.activity.EdgeToEdge;           // Enables modern edge-to-edge fullscreen layouts
import androidx.appcompat.app.AppCompatActivity; // Base class for backward-compatible activities
import androidx.core.view.ViewCompat;          // Utilities for view insets and padding

/**
 * Third Activity
 * --------------
 * This is a multiple-choice quiz screen (Question 3).
 * The user selects one of four possible answers.
 * One answer is correct and leads to a success screen,
 * while all others lead to a failure screen.
 */
public class Third extends BaseMenuActivity implements View.OnClickListener {

    // UI components
    private TextView TV1;       // Displays the question text
    private Button BtClick2;    // Answer option 1
    private Button BtClick3;    // Answer option 2 (correct answer)
    private Button BtClick4;    // Answer option 3
    private Button BtClick5;    // Answer option 4

    // Timer used to track how long the user takes to answer
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern full-screen edge-to-edge layout
        EdgeToEdge.enable(this);

        // Set the layout XML file for this screen
        setContentView(R.layout.activity_third);

        // Start background music for this level/screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.third_music);
        startService(serviceIntent);

        // Record the time when this question screen is shown
        startTime = System.currentTimeMillis();

        // Attach UI initialization after layout is fully applied
        // Ensures all views exist before findViewById is called
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {

            // Initialize all buttons and text views
            initViews();

            // Return system window insets unchanged
            return insets;
        });
    }

    /**
     * Links XML UI elements to Java variables and sets click listeners
     */
    private void initViews() {

        // Connect question text view
        TV1 = findViewById(R.id.TV1);

        // Connect answer buttons
        BtClick2 = findViewById(R.id.BtClick2);
        BtClick3 = findViewById(R.id.BtClick3); // Correct answer
        BtClick4 = findViewById(R.id.BtClick4);
        BtClick5 = findViewById(R.id.BtClick5);

        // Set click listener for all buttons
        BtClick2.setOnClickListener(this);
        BtClick3.setOnClickListener(this);
        BtClick4.setOnClickListener(this);
        BtClick5.setOnClickListener(this);
    }

    /**
     * Handles user button clicks for all answer choices
     */
    @Override
    public void onClick(View view) {

        // If correct answer is selected
        if (view == BtClick3) {

            // Calculate time taken to answer correctly
            long timeTaken = System.currentTimeMillis() - startTime;

            // Navigate to success screen
            Intent intent = new Intent(this, CorrectScreen1.class);

            // Pass time taken to next activity
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