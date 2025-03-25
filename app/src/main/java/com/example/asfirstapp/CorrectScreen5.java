package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CorrectScreen5 extends AppCompatActivity implements View.OnClickListener {
    Button BtClick18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen5); // Make sure this layout exists
        initViews();  // Directly call initViews to avoid unnecessary insets handling
    }

    private void initViews() {
        BtClick18 = findViewById(R.id.BtClick18);  // Ensure this ID is correct in XML
        BtClick18.setOnClickListener(this); // Set up the click listener
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Puzzle3.class);
        startActivity(intent); // Start the next activity
    }
}
