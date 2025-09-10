package com.example.asfirstapp;
// Defines the package namespace for this class.

import android.content.Intent;          // Used to navigate between activities.
import android.os.Bundle;               // Holds activity state information.
import android.view.View;               // Base class for UI elements.
import android.widget.Button;           // Represents a clickable button.

import androidx.activity.EdgeToEdge;    // Enables modern fullscreen edge-to-edge layouts.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.
import androidx.core.graphics.Insets;    // Represents system bar insets (status/nav bars).
import androidx.core.view.ViewCompat;    // Helps with cross-version UI behavior.
import androidx.core.view.WindowInsetsCompat; // Provides info about window insets.

public class CorrectScreen4 extends AppCompatActivity implements View.OnClickListener {
    // This activity shows the "correct answer" screen after Puzzle1 (light sensor puzzle)
    // Implements OnClickListener to handle button presses.

    Button BtCLick17; // Button to proceed to the next puzzle (Puzzle2).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable fullscreen edge-to-edge layout.
        setContentView(R.layout.activity_correct_screen4); // Set layout for this screen.

        // Handle system window insets (status/nav bar).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews(); // Initialize button view.
            return insets; // Return insets unchanged.
        });
    }

    private void initViews() {
        BtCLick17 = findViewById(R.id.BtClick17); // Find "Next" button by ID.
        BtCLick17.setOnClickListener(this);       // Set this activity as click listener.
    }

    @Override
    public void onClick(View view) {
        // Triggered when BtCLick17 is clicked.
        Intent intent = new Intent(this, Puzzle2.class); // Create intent for Puzzle2 activity.
        startActivity(intent);                            // Start Puzzle2.
    }
}
