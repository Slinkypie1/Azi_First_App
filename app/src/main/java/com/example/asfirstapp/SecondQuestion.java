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
 * Displays the second multiple-choice question of the quiz.
 * Handles clicks on four answer buttons and navigates to either the
 * correct-answer screen or failure screen.
 */
public class SecondQuestion extends BaseMenuActivity implements View.OnClickListener {

    // UI components
    private TextView TV2;       // Displays the question text
    private Button BtClick6;    // Answer option 1
    private Button BtClick7;    // Answer option 2 (CORRECT)
    private Button BtClick8;    // Answer option 3
    private Button BtClick9;    // Answer option 4

    private long startTime;     // Records when the question screen was displayed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Set the layout XML for this activity
        setContentView(R.layout.activity_second_question);

        // Start background music for Level 2 (SecondQuestion screen)
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.second_question_music);
        startService(serviceIntent);

        // Record the time the question started
        startTime = System.currentTimeMillis();

        // Ensure views are initialized after layout is applied
        // This prevents findViewById from failing due to view hierarchy not ready
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();   // Connect TextView and Buttons to Java objects
            return insets; // Return insets unchanged
        });
    }

    /**
     * Initializes the UI components and sets click listeners
     */
    private void initViews() {
        // Connect TextView from XML
        TV2 = findViewById(R.id.TV2);

        // Connect Buttons from XML
        BtClick6 = findViewById(R.id.BtClick6);
        BtClick7 = findViewById(R.id.BtClick7);
        BtClick8 = findViewById(R.id.BtClick8);
        BtClick9 = findViewById(R.id.BtClick9);

        // Set this activity as the click listener for all buttons
        BtClick6.setOnClickListener(this);
        BtClick7.setOnClickListener(this);
        BtClick8.setOnClickListener(this);
        BtClick9.setOnClickListener(this);
    }

    /**
     * Handles clicks for all four answer buttons
     */
    @Override
    public void onClick(View view) {
        // Check if the correct answer button (BtClick7) was clicked
        if(view == BtClick7){
            // Correct answer clicked
            long timeTaken = System.currentTimeMillis() - startTime; // Calculate time taken to answer

            // Prepare Intent to navigate to CorrectScreen2
            Intent intent = new Intent(this, CorrectScreen2.class);
            intent.putExtra("TIME_TAKEN", timeTaken);               // Pass completion time
            startActivity(intent);                                  // Navigate to success screen

        } else {
            // Wrong answer clicked (any other button)
            Intent intent = new Intent(this, Failure.class);       // Prepare failure screen
            startActivity(intent);                                  // Navigate to failure/game-over screen
        }
    }
}
