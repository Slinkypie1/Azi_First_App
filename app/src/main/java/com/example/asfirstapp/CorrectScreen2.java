package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to start another activity
import android.os.Bundle;      // Stores saved state of the activity
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

import androidx.activity.EdgeToEdge; // Enables edge-to-edge layout
import androidx.core.view.ViewCompat; // Provides compatibility features for views

// Screen shown when the user answers Level 2 correctly
public class CorrectScreen2 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick15; // Button that moves the user to the next level

    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        EdgeToEdge.enable(this);            // Enable full-screen layout
        setContentView(R.layout.activity_correct_screen2); // Load layout XML

        // Apply window insets and initialize views
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews(); // Find UI elements and set listeners
            return insets; // Return insets unchanged
        });

        // Mark Level 2 as completed and unlock Level 3
        unlockNextLevel(2);
    }

    // Unlocks the next level if this level is the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        // Check if the player just finished the latest unlocked level
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {

            // Save the next level as unlocked
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Finds the button and sets its click listener
    private void initViews() {
        BtClick15 = findViewById(R.id.BtClick15); // Connect button to XML
        BtClick15.setOnClickListener(this);      // Set click handler
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to move to the Level 3 question screen
        Intent intent = new Intent(this, ThirdQuestion.class);

        // Start the next activity
        startActivity(intent);
    }
}
