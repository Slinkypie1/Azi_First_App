package com.example.asfirstapp;

// Package declaration – must match your app’s structure.

import android.content.Intent;    // Used to navigate between activities
import android.os.Bundle;         // Stores the activity state
import android.view.View;         // Base class for UI components
import android.widget.Button;     // Button UI element
import android.widget.TextView;   // TextView UI element

import androidx.activity.EdgeToEdge;           // Enables modern fullscreen layouts
import androidx.appcompat.app.AppCompatActivity; // Base class for AppCompat activities
import androidx.core.view.ViewCompat;          // Backwards-compatible view utilities

/**
 * SecondQuestion Activity
 * -----------------------
 * Displays the second multiple-choice question of the quiz.
 * Handles button clicks for all four answer options.
 */
public class SecondQuestion extends BaseMenuActivity implements View.OnClickListener {

    private TextView TV2;       // Displays the question text
    private Button BtClick6;    // Answer option 1
    private Button BtClick7;    // Answer option 2 (CORRECT)
    private Button BtClick8;    // Answer option 3
    private Button BtClick9;    // Answer option 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Load the layout XML for this screen
        setContentView(R.layout.activity_second_question);

        // Set up a listener to initialize views after layout is applied
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();   // Connect TextView and Buttons to Java objects
            return insets; // Return insets unchanged (optional padding adjustments could be added)
        });
    }

    /**
     * Initializes the UI components and sets click listeners
     */
    private void initViews() {
        // Link TextView to XML
        TV2 = findViewById(R.id.TV2);

        // Link buttons to XML
        BtClick6 = findViewById(R.id.BtClick6);
        BtClick7 = findViewById(R.id.BtClick7);
        BtClick8 = findViewById(R.id.BtClick8);
        BtClick9 = findViewById(R.id.BtClick9);

        // Set click listeners so this activity handles button presses
        BtClick6.setOnClickListener(this);
        BtClick7.setOnClickListener(this);
        BtClick8.setOnClickListener(this);
        BtClick9.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Handle clicks for all four answer buttons

        if(view == BtClick7){
            // Correct answer clicked
            Intent intent = new Intent(this, CorrectScreen2.class);
            startActivity(intent); // Navigate to the correct answer screen
        } else {
            // Any other button clicked (wrong answer)
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent); // Navigate to failure/game-over screen
        }
    }
}
