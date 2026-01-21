package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to start another activity
import android.os.Bundle;      // Stores saved state of the activity
import android.view.View;      // Base class for UI elements
import android.widget.Button; // Button UI component

// Screen shown when the user completes Level 5 correctly
public class CorrectScreen5 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick18; // Button that moves the user to Level 6

    // Called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Run base activity setup
        setContentView(R.layout.activity_correct_screen5); // Load the layout
        initViews(); // Find views and set click listeners

        // Mark Level 5 as completed and unlock Level 6
        unlockNextLevel(5);
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
        BtClick18 = findViewById(R.id.BtClick18); // Connect button to XML
        BtClick18.setOnClickListener(this);      // Set this activity as click handler
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        // Create intent to start the Level 6 puzzle activity
        Intent intent = new Intent(this, Puzzle3.class);

        // Launch the next activity
        startActivity(intent);
    }
}
