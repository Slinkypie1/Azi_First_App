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
 * Displays a multiple-choice question with four buttons.
 * Handles clicks and navigates to either the correct answer screen
 * or the failure screen.
 */
public class Third extends BaseMenuActivity implements View.OnClickListener {

    // UI components
    private TextView TV1;       // Displays the question text
    private Button BtClick2;    // Answer option 1
    private Button BtClick3;    // Answer option 2 (CORRECT)
    private Button BtClick4;    // Answer option 3
    private Button BtClick5;    // Answer option 4

    private long startTime;     // Records the time when the question is displayed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Set the layout XML for this activity
        setContentView(R.layout.activity_third);

        // Start background music for Level 1 (Third screen)
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.third_music);
        startService(serviceIntent);

        // Record the start time to measure how long the user takes to answer
        startTime = System.currentTimeMillis();

        // Ensure views are initialized after layout is applied
        // This avoids issues where findViewById might fail
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            initViews();   // Connect TextView and Buttons to Java objects
            return insets; // Return insets unchanged
        });
    }

    /**
     * Initializes the UI components and sets click listeners
     */
    private void initViews() {
        // Connect TextView from XML
        TV1 = findViewById(R.id.TV1);

        // Connect buttons from XML
        BtClick2 = findViewById(R.id.BtClick2);
        BtClick3 = findViewById(R.id.BtClick3); // Correct answer
        BtClick4 = findViewById(R.id.BtClick4);
        BtClick5 = findViewById(R.id.BtClick5);

        // Set this activity as the click listener for all buttons
        BtClick2.setOnClickListener(this);
        BtClick3.setOnClickListener(this);
        BtClick4.setOnClickListener(this);
        BtClick5.setOnClickListener(this);
    }

    /**
     * Handles clicks for all four answer buttons
     */
    @Override
    public void onClick(View view) {
        // Check if the correct answer button (BtClick3) was clicked
        if (view == BtClick3) {
            // Correct answer selected
            long timeTaken = System.currentTimeMillis() - startTime; // Calculate time taken to answer

            // Prepare Intent to navigate to CorrectScreen1
            Intent intent = new Intent(this, CorrectScreen1.class);
            intent.putExtra("TIME_TAKEN", timeTaken);               // Pass completion time
            startActivity(intent);                                  // Navigate to success screen

        } else {
            // Wrong answer selected (any other button)
            Intent intent = new Intent(this, Failure.class);       // Prepare failure screen
            startActivity(intent);                                  // Navigate to failure/game-over screen
        }
    }
}
