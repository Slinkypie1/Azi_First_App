package com.example.asfirstapp;

import android.content.Intent; // Needed to switch activities
import android.os.Bundle; // Needed for activity lifecycle
import android.view.View; // Needed for click handling
import android.widget.*; // Needed for Spinner, Button, etc.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities

public class FillTheBlanks extends BaseMenuActivity {

    // Array of all choices for the spinners
    String[] choices = {"choose here", "jumping", "rising", "falling", "happiness", "glory", "walking"};

    // Correct answers for the blanks, order matters
    String[] correctAnswers = {"glory", "falling", "rising"};

    // Array of spinner references for easier iteration
    Spinner[] spinners = new Spinner[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call parent onCreate
        setContentView(R.layout.activity_fill_the_blanks); // Load the layout

        // Initialize spinner references by finding them in the layout
        spinners[0] = findViewById(R.id.spinner1); // First blank
        spinners[1] = findViewById(R.id.spinner2); // Second blank
        spinners[2] = findViewById(R.id.spinner3); // Third blank

        Button submitButton = findViewById(R.id.submitButton); // Find the submit button

        // Create an ArrayAdapter to populate the spinners with choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);

        // Set the adapter for each spinner
        for (Spinner spinner : spinners) {
            spinner.setAdapter(adapter); // Attach the choices to the spinner
        }

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allCorrect = true; // Flag to check if all answers are correct

                // Loop through each spinner to check answers
                for (int i = 0; i < spinners.length; i++) {
                    String selectedAnswer = spinners[i].getSelectedItem().toString(); // Get selected item
                    if (!selectedAnswer.equals(correctAnswers[i])) { // Compare with correct answer
                        allCorrect = false; // If any wrong, set flag to false
                        break; // No need to check further if one is wrong
                    }
                }

                // If all answers are correct, navigate to CorrectScreen7
                if (allCorrect) {
                    Intent intent = new Intent(FillTheBlanks.this, CorrectScreen7.class);
                    startActivity(intent); // Start the correct screen
                } else {
                    // If any answer is wrong, navigate to Failure screen
                    Intent intent = new Intent(FillTheBlanks.this, Failure.class);
                    startActivity(intent); // Start the failure screen
                }
            }
        });
    }
}
