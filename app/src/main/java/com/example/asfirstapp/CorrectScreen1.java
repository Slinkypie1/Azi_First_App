package com.example.asfirstapp;
// Defines the package namespace of the app.

import android.content.Intent;    // Used to navigate between activities.
import android.os.Bundle;         // Stores activity state info.
import android.view.View;         // Base class for UI widgets.
import android.widget.Button;     // UI element for a clickable button.
import android.widget.TextView;   // (Imported but not used in this file.)

import androidx.activity.EdgeToEdge;       // Enables edge-to-edge fullscreen layouts.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with modern UI support.
import androidx.core.graphics.Insets;      // Represents system UI insets (status/nav bars).
import androidx.core.view.ViewCompat;      // Utility for cross-version UI adjustments.
import androidx.core.view.WindowInsetsCompat; // Provides details about window insets.

public class CorrectScreen1 extends AppCompatActivity implements View.OnClickListener {
    // This activity is shown when the player answers correctly (for Question 1).
    // Implements OnClickListener so it can handle button clicks.

    Button BtClick10; // Button that moves the player to the next question.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent onCreate to initialize the activity.

        EdgeToEdge.enable(this);
        // Enables fullscreen edge-to-edge layout (modern Android style).

        setContentView(R.layout.activity_correct_screen1);
        // Sets the UI layout for this screen (activity_correct_screen1.xml).

        // Apply system window insets (status bar, navigation bar).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();  // Initialize button after layout is ready.
            return insets; // Pass the insets back unchanged.
        });
    }

    private void initViews() {
        BtClick10 = findViewById(R.id.BtClick10); // Finds the "Next" button by its ID.
        BtClick10.setOnClickListener(this);       // Sets this activity as the button's click listener.
    }

    @Override
    public void onClick(View view) {
        // Called when BtClick10 is pressed.

        Intent intent = new Intent(this, SecondQuestion.class);
        // Create an intent to open the next activity: SecondQuestion.

        startActivity(intent);
        // Launch SecondQuestion (progress to the next puzzle).
    }
}
