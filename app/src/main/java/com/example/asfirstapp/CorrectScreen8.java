package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Screen shown when the player completes the "Find the Country" puzzle successfully
public class CorrectScreen8 extends AppCompatActivity implements View.OnClickListener {

    Button BtClick21; // Button to proceed to the next puzzle/activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen8); // Load the layout for this screen

        BtClick21 = findViewById(R.id.BtClick21); // Get reference to the button from layout
        BtClick21.setOnClickListener(this);       // Set this class as the click listener
    }

    @Override
    public void onClick(View view) {
        // When button is clicked, move to the UnlockCityActivity
        Intent intent = new Intent(this, UnlockCityActivity.class);
        startActivity(intent); // Start the next activity
    }
}
