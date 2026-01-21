package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to start another activity
import android.os.Bundle;      // Stores saved state of the activity
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

// Screen shown when the user completes Level 9 correctly
public class CorrectScreen9 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick22; // Button that moves the user after finishing Level 9

    // Called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        setContentView(R.layout.activity_correct_screen9); // Load the layout

        // Find the button in the layout
        BtClick22 = findViewById(R.id.BtClick22);

        // Set this activity as the button click handler
        BtClick22.setOnClickListener(this);

        // Mark Level 9 as completed (and unlock next level if any)
        unlockNextLevel(9);
    }

    // Unlocks the next level if this level is the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        // Check if the completed level is the highest unlocked one
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {

            // Save the next level as unlocked (if more levels exist)
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to go back to the main menu or "game completed" screen
        Intent intent = new Intent(this, Second.class);

        // Launch the activity
        startActivity(intent);

        // Close this activity so it doesn't remain in the back stack
        finish();
    }
}
