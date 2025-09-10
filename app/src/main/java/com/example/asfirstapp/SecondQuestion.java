package com.example.asfirstapp;
// Defines the package where this activity belongs.

import android.content.Intent;    // Lets you navigate between activities.
import android.os.Bundle;         // Stores data about the activity's state.
import android.view.View;         // Base class for UI components.
import android.widget.Button;     // UI widget for clickable buttons.
import android.widget.TextView;   // UI widget to display text.

import androidx.activity.EdgeToEdge;       // Enables fullscreen layouts (modern Android look).
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.
import androidx.core.graphics.Insets;      // Represents system bars insets (status/navigation).
import androidx.core.view.ViewCompat;      // Utility for backward-compatible view handling.
import androidx.core.view.WindowInsetsCompat; // Provides details about system UI insets.

public class SecondQuestion extends AppCompatActivity implements View.OnClickListener {
    // This activity shows the second quiz question.
    // Implements View.OnClickListener so this class can handle button clicks.

    TextView TV2;        // Displays the question text.
    Button BtClick6;     // Answer option 1
    Button BtClick7;     // Answer option 2 (this is the correct one)
    Button BtClick8;     // Answer option 3
    Button BtClick9;     // Answer option 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls the parent method to initialize the activity.

        EdgeToEdge.enable(this);
        // Enables drawing behind system bars (modern UI style).

        setContentView(R.layout.activity_second_question);
        // Loads the UI layout file for this screen.

        // Attach a listener to apply window insets (for proper fullscreen layout).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews(); // Initialize text and buttons once layout is ready.
            return insets; // Return the insets unchanged.
        });
    }

    private void initViews() {
        // Connects Java objects to the XML UI elements by their IDs.
        TV2 = findViewById(R.id.TV2);

        BtClick6 = findViewById(R.id.BtClick6);
        BtClick7 = findViewById(R.id.BtClick7);
        BtClick8 = findViewById(R.id.BtClick8);
        BtClick9 = findViewById(R.id.BtClick9);

        // Set up click listeners so this activity handles button clicks.
        BtClick6.setOnClickListener(this);
        BtClick7.setOnClickListener(this);
        BtClick8.setOnClickListener(this);
        BtClick9.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Called whenever one of the answer buttons is clicked.

        if(view == BtClick7){
            // If the correct button was clicked (BtClick7)...

            Intent intent = new Intent(this, CorrectScreen2.class);
            // Create intent to open CorrectScreen2 (success page for question 2).

            startActivity(intent);
            // Launch CorrectScreen2 activity.
        }
        else{
            // If any other button was clicked (wrong answer)...

            Intent intent = new Intent(this, Failure.class);
            // Create intent to open Failure screen.

            startActivity(intent);
            // Launch Failure activity (game over screen).
        }
    }
}
