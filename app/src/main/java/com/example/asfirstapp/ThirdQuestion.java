package com.example.asfirstapp;
// Defines the package namespace for this activity.

import android.content.Intent;    // Used to switch between activities.
import android.os.Bundle;         // Stores activity state information.
import android.view.View;         // Base class for UI components.
import android.widget.Button;     // UI element for clickable buttons.
import android.widget.TextView;   // UI element for displaying text.

import androidx.activity.EdgeToEdge;       // Enables modern fullscreen, edge-to-edge layouts.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.
import androidx.core.graphics.Insets;      // Represents system UI insets.
import androidx.core.view.ViewCompat;      // Helper for applying cross-version UI behaviors.
import androidx.core.view.WindowInsetsCompat; // Provides system window inset information.

public class ThirdQuestion extends AppCompatActivity implements View.OnClickListener {
    // Activity for the third quiz question.
    // Implements OnClickListener to handle answer button clicks.

    TextView TV3;       // Displays the question text.
    Button BtClick11;   // Answer option 1
    Button BtClick12;   // Answer option 2
    Button BtClick13;   // Answer option 3 (correct answer)
    Button BtClick14;   // Answer option 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent onCreate to initialize activity.

        EdgeToEdge.enable(this);
        // Enables content to display edge-to-edge (behind status/navigation bars).

        setContentView(R.layout.activity_third_question);
        // Sets layout to activity_third_question.xml.

        // Apply window insets for proper spacing around system bars.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();  // Initialize TextView and buttons after layout is ready.
            return insets; // Return insets unchanged.
        });
    }

    private void initViews() {
        TV3 = findViewById(R.id.TV3);           // Connect TextView from layout.
        BtClick11 = findViewById(R.id.BtClick11); // Connect buttons from layout.
        BtClick12 = findViewById(R.id.BtClick12);
        BtClick13 = findViewById(R.id.BtClick13);
        BtClick14 = findViewById(R.id.BtClick14);

        // Set this activity as click listener for all buttons.
        BtClick11.setOnClickListener(this);
        BtClick12.setOnClickListener(this);
        BtClick13.setOnClickListener(this);
        BtClick14.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Triggered when any answer button is clicked.

        if(view == BtClick13){
            // Correct answer button clicked.

            Intent intent = new Intent(this, CorrectScreen3.class);
            startActivity(intent);
            // Open CorrectScreen3 activity (success screen for question 3).
        }
        else{
            // Wrong answer clicked.

            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
            // Open Failure activity (game over screen).
        }
    }
}
