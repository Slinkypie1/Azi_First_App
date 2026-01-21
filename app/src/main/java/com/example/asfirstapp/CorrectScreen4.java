package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to start another activity
import android.os.Bundle;      // Stores saved state of the activity
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

import androidx.activity.EdgeToEdge; // Enables edge-to-edge screen layout
import androidx.core.view.ViewCompat; // Provides backward-compatible view features

// Screen shown when the user completes Level 4 correctly
public class CorrectScreen4 extends BaseMenuActivity implements View.OnClickListener {

    Button BtCLick17; // Button that moves the user to Level 5

    // Called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        EdgeToEdge.enable(this);            // Enable full-screen layout
        setContentView(R.layout.activity_correct_screen4); // Load the layout

        // Apply window insets and initialize UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews(); // Find views and set click listeners
            return insets; // Return the insets unchanged
        });

        // Mark Level 4 as completed and unlock Level 5
        unlockNextLevel(4);
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
        BtCLick17 = findViewById(R.id.BtClick17); // Connect button to XML
        BtCLick17.setOnClickListener(this);      // Set this activity as click handler
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to start the Level 5 puzzle activity
        Intent intent = new Intent(this, Puzzle2.class);

        // Launch the next activity
        startActivity(intent);
    }
}
