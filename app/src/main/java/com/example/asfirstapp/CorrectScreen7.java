package com.example.asfirstapp;

import android.content.Intent; // Needed to switch activities
import android.os.Bundle; // Needed for activity lifecycle
import android.view.View; // Needed for click handling
import android.widget.Button; // Needed for Button UI element
import androidx.appcompat.app.AppCompatActivity; // Base class for activities

public class CorrectScreen7 extends AppCompatActivity implements View.OnClickListener {

    Button BtClick20; // Button that the user will click to continue

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call the parent onCreate method
        setContentView(R.layout.activity_correct_screen7); // Load the correct screen layout

        BtClick20 = findViewById(R.id.BtClick20); // Find the button in the layout
        BtClick20.setOnClickListener(this); // Set the click listener to this activity
    }

    @Override
    public void onClick(View view) {
        // When the button is clicked, start the FindTheCountry activity
        Intent intent = new Intent(this, FindTheCountry.class);
        startActivity(intent); // Launch the next activity
    }
}
