package com.example.asfirstapp;
// Defines the package namespace for this class.

import android.content.Intent;       // Used to navigate between activities.
import android.os.Bundle;            // Holds activity state information.
import android.view.View;            // Base class for UI elements.
import android.widget.Button;        // Represents a clickable button.

import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.

public class CorrectScreen5 extends BaseMenuActivity implements View.OnClickListener {
    // This activity shows the "correct answer" screen after Puzzle2 (compass puzzle)
    // Implements OnClickListener to handle button presses.

    Button BtClick18; // Button to proceed to the next puzzle (Puzzle3).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen5); // Set layout for this screen.
        initViews();  // Initialize button view directly (no insets handling needed).
        int level = getIntent().getIntExtra("LEVEL", 1);

        if (level == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, level + 1);
        }

    }

    private void initViews() {
        BtClick18 = findViewById(R.id.BtClick18);  // Find "Next" button by ID in XML.
        BtClick18.setOnClickListener(this);       // Set this activity as click listener.
    }

    @Override
    public void onClick(View view) {
        // Triggered when BtClick18 is clicked.
        Intent intent = new Intent(this, Puzzle3.class); // Create intent for Puzzle3 activity.
        startActivity(intent);                            // Start Puzzle3.
    }
}
