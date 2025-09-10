package com.example.asfirstapp;
// Defines the package for this class. It should match your app’s package structure.

import android.content.Intent;
// Imports the Intent class to start new activities.

import android.os.Bundle;
// Imports Bundle class to handle the activity’s saved state.

import android.view.View;
// Imports View class for UI elements and click handling.

import android.widget.Button;
// Imports Button class for creating clickable buttons.

import androidx.appcompat.app.AppCompatActivity;
// Imports the AppCompatActivity class which provides support for modern Android features.

public class CorrectScreen9 extends AppCompatActivity implements View.OnClickListener {
    // Defines the activity class named CorrectScreen9.
    // Implements OnClickListener so we can handle button clicks.

    Button BtClick22;
    // Declares a Button variable that will represent the button in the layout.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls the superclass method to initialize the activity.

        setContentView(R.layout.activity_correct_screen9);
        // Sets the layout file to use for this activity.

        BtClick22 = findViewById(R.id.BtClick22);
        // Finds the button in the layout by its ID and assigns it to the variable.

        BtClick22.setOnClickListener(this);
        // Sets this activity as the listener for button clicks.
    }

    @Override
    public void onClick(View view) {
        // This method is called whenever a view with this listener is clicked.

        Intent intent = new Intent(this, UnlockCityActivity.class);
        // Creates a new Intent to start UnlockCityActivity.

        startActivity(intent);
        // Starts the activity specified in the intent.
    }
}
