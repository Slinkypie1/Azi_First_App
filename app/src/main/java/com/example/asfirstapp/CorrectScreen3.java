package com.example.asfirstapp;
// Defines the package namespace for this activity.

import android.content.Intent;    // Used to navigate between activities.
import android.os.Bundle;         // Holds activity state info.
import android.view.View;         // Base class for UI elements.
import android.widget.Button;     // Represents a clickable button.

import androidx.activity.EdgeToEdge;       // Enables modern fullscreen edge-to-edge layouts.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.
import androidx.core.graphics.Insets;      // Represents system bar insets (status/nav bars).
import androidx.core.view.ViewCompat;      // Helps with cross-version UI behavior.
import androidx.core.view.WindowInsetsCompat; // Provides info about window insets.

public class CorrectScreen3 extends AppCompatActivity implements View.OnClickListener {
    // This activity shows the "correct answer" screen for Question 3.
    // Implements View.OnClickListener to handle button clicks.

    Button BtClick16; // Button to proceed to the first puzzle.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent onCreate to initialize the activity.

        EdgeToEdge.enable(this);
        // Enables fullscreen edge-to-edge layout.

        setContentView(R.layout.activity_correct_screen3);
        // Sets the UI layout for this screen.

        // Handle system window insets (status/nav bar).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();  // Initialize button view.
            return insets; // Return insets unchanged.
        });
    }

    private void initViews() {
        BtClick16 = findViewById(R.id.BtClick16); // Find "Next" button by ID.
        BtClick16.setOnClickListener(this);       // Set this activity as the click listener.
    }

    @Override
    public void onClick(View view) {
        // Triggered when BtClick16 is clicked.

        Intent intent = new Intent(this, Puzzle1.class);
        // Create an intent to open Puzzle1 activity (first puzzle in the game).

        startActivity(intent);
        // Launch Puzzle1 activity.
    }
}
