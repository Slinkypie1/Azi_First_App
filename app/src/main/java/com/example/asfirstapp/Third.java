package com.example.asfirstapp;
// Defines the package name (namespace) of the app.

import android.content.Intent;    // Used to switch between activities.
import android.os.Bundle;         // Holds activity state if recreated.
import android.view.View;         // Base class for UI widgets.
import android.widget.Button;     // Represents clickable buttons in the UI.
import android.widget.TextView;   // Represents text display elements.

import androidx.activity.EdgeToEdge;       // Allows fullscreen, edge-to-edge layouts.
import androidx.appcompat.app.AppCompatActivity; // Base activity class with modern features.
import androidx.core.graphics.Insets;      // Represents system bar insets (status/nav bar).
import androidx.core.view.ViewCompat;      // Helps apply UI changes across Android versions.
import androidx.core.view.WindowInsetsCompat; // Provides info about window insets.

public class Third extends AppCompatActivity implements View.OnClickListener {
    // This activity shows a multiple-choice style screen with buttons.
    // Implements OnClickListener so this class can handle button clicks.

    TextView TV1;        // A TextView from the layout (question text).
    Button BtClick2;     // First answer button.
    Button BtClick3;     // Second answer button.
    Button BtClick4;     // Third answer button.
    Button BtClick5;     // Fourth answer button.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent onCreate to set up the activity.

        EdgeToEdge.enable(this);
        // Enables edge-to-edge fullscreen layout.

        setContentView(R.layout.activity_third);
        // Sets the UI layout to activity_third.xml.

        // Handle system UI insets (status/nav bar).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            initViews();  // Initialize all views (TextView and buttons).
            return insets; // Pass insets back unchanged.
        });
    }

    private void initViews() {
        TV1 = findViewById(R.id.TV1);           // Finds TextView by ID.
        BtClick2 = findViewById(R.id.BtClick2); // Finds button 1 by ID.
        BtClick3 = findViewById(R.id.BtClick3); // Finds button 2 by ID.
        BtClick4 = findViewById(R.id.BtClick4); // Finds button 3 by ID.
        BtClick5 = findViewById(R.id.BtClick5); // Finds button 4 by ID.

        // Attach this class (Third) as click handler for each button.
        BtClick2.setOnClickListener(this);
        BtClick4.setOnClickListener(this);
        BtClick5.setOnClickListener(this);
        BtClick3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Called when any of the buttons are clicked.

        if(view == BtClick3){
            // If the correct answer button (BtClick3) is clicked...

            Intent intent = new Intent(this, CorrectScreen1.class);
            startActivity(intent);
            // Open CorrectScreen1 activity (success screen).
        }
        else {
            // For any other button clicked (wrong answer)...
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
            // Open Failure activity (failure screen).
        }
    }
}
