package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to start another activity
import android.os.Bundle;      // Stores saved state of the activity
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

import androidx.activity.EdgeToEdge; // Enables edge-to-edge screen layout
import androidx.core.view.ViewCompat; // Provides backward-compatible view features

// Screen shown when the user answers Level 3 correctly
public class CorrectScreen3 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick16; // Button that moves the user to Level 4

    // Called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        EdgeToEdge.enable(this);            // Enable full-screen layout
        setContentView(R.layout.activity_correct_screen3); // Load the layout

        // Apply window insets and initialize UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews(); // Find views and set click listeners
            return insets; // Return the insets unchanged
        });

        // Mark Level 3 as completed and unlock Level 4
        unlockNextLevel(3);
    }

    // Unlocks the next level if this level is the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        // Check if the completed level is the highest unlocked one
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {

            // Save the next level as unlocked
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Finds the button in the layout and sets its click listener
    private void initViews() {
        BtClick16 = findViewById(R.id.BtClick16); // Connect button to XML
        BtClick16.setOnClickListener(this);      // Set this activity as click handler
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to start the Level 4 puzzle activity
        Intent intent = new Intent(this, Puzzle1.class);

        // Launch the next activity
        startActivity(intent);
    }
}
