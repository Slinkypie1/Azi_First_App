package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to move between activities
import android.os.Bundle;      // Holds activity state information
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

import androidx.activity.EdgeToEdge; // Enables edge-to-edge screen layout
import androidx.core.view.ViewCompat; // Provides backward-compatible view features

// Screen shown when the user answers Level 1 correctly
public class CorrectScreen1 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick10; // Button that moves the user to the next level

    // Called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        EdgeToEdge.enable(this);            // Enable edge-to-edge display
        setContentView(R.layout.activity_correct_screen1); // Load the layout

        // Apply window insets and initialize views
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews(); // Initialize UI elements
            return insets; // Return the insets unchanged
        });

        // Mark Level 1 as completed and unlock Level 2
        unlockNextLevel(1);
    }

    // Unlocks the next level if the current one is the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        // Check if the player just completed the highest unlocked level
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {

            // Unlock the next level by saving it
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Finds views and sets click listeners
    private void initViews() {
        BtClick10 = findViewById(R.id.BtClick10); // Link button to XML
        BtClick10.setOnClickListener(this);      // Set this activity as click handler
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to move to the next question screen
        Intent intent = new Intent(this, SecondQuestion.class);

        // Start the next activity
        startActivity(intent);
    }
}
