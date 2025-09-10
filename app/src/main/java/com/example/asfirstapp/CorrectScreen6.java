package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CorrectScreen6 extends AppCompatActivity implements View.OnClickListener {

    Button BtClick19;  // Button to proceed to next puzzle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen6); // Load the correct screen layout

        // Initialize button from layout and set click listener
        BtClick19 = findViewById(R.id.BtClick19);
        BtClick19.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // When the button is clicked, navigate to the next puzzle/activity
        Intent intent = new Intent(this, FillTheBlanks.class);
        startActivity(intent);
    }
}
