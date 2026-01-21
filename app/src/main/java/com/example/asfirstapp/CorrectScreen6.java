package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to start another activity
import android.os.Bundle;      // Stores saved state of the activity
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

// Screen shown when the user completes Level 6 correctly
public class CorrectScreen6 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick19; // Button that moves the user to Level 7

    // Called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        setContentView(R.layout.activity_correct_screen6); // Load the layout

        // Find the button in the layout
        BtClick19 = findViewById(R.id.BtClick19);

        // Set this activity as the button click handler
        BtClick19.setOnClickListener(this);

        // Mark Level 6 as completed and unlock Level 7
        unlockNextLevel(6);
    }

    // Unlocks the next level if this level is the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        // Check if the completed level is the highest unlocked one
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {

            // Save the next level as unlocked
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to start the Level 7 activity
        Intent intent = new Intent(this, FillTheBlanks.class);

        // Launch the next activity
        startActivity(intent);
    }
}
