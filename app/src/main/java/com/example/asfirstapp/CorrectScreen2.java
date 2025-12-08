package com.example.asfirstapp;
// Defines the package where this activity resides.

import android.content.Intent;    // Used to start other activities.
import android.os.Bundle;         // Holds saved state info for the activity.
import android.view.View;         // Base class for UI elements.
import android.widget.Button;     // Represents a clickable button.

import androidx.activity.EdgeToEdge;       // Enables modern edge-to-edge fullscreen layouts.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.
import androidx.core.graphics.Insets;      // Represents system bar insets (status/nav bars).
import androidx.core.view.ViewCompat;      // Helper for backward-compatible UI behavior.
import androidx.core.view.WindowInsetsCompat; // Provides info about system window insets.

public class CorrectScreen2 extends BaseMenuActivity implements View.OnClickListener {
    // This activity shows the "correct answer" screen for Question 2.
    // Implements View.OnClickListener to handle button clicks.

    Button BtClick15; // Button to move to the next question.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent onCreate to initialize the activity.

        EdgeToEdge.enable(this);
        // Enables fullscreen, edge-to-edge layout for modern Android UI.

        setContentView(R.layout.activity_correct_screen2);
        // Sets the UI layout for this activity.

        // Apply window insets (for proper padding with system bars).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();  // Initialize button after layout is ready.
            return insets; // Return insets unchanged.
        });
        int level = getIntent().getIntExtra("LEVEL", 1);

        if (level == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, level + 1);
        }

    }

    private void initViews() {
        BtClick15 = findViewById(R.id.BtClick15); // Find the "Next" button by ID.
        BtClick15.setOnClickListener(this);       // Set this activity as the click listener.
    }

    @Override
    public void onClick(View view) {
        // Called when BtClick15 is clicked.

        Intent intent = new Intent(this, ThirdQuestion.class);
        // Create an intent to open the next activity: ThirdQuestion.

        startActivity(intent);
        // Launch ThirdQuestion activity to continue the quiz.
    }
}
