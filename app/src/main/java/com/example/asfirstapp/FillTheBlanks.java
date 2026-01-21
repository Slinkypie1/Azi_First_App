package com.example.asfirstapp;

import android.content.Intent; // Needed to switch activities
import android.os.Bundle;      // Needed for activity lifecycle
import android.view.View;      // Needed for click handling
import android.widget.*;       // Needed for Spinner, Button, etc.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities

// Activity for the "Fill the Blanks" quiz
public class FillTheBlanks extends BaseMenuActivity {

    // Array of all possible choices for the spinners
    String[] choices = {"choose here", "jumping", "rising", "falling", "happiness", "glory", "walking"};

    // Correct answers for the blanks, in order
    String[] correctAnswers = {"glory", "falling", "rising"};

    // Array to store references to the three Spinner widgets
    Spinner[] spinners = new Spinner[3];

    // Called when activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call parent setup
        setContentView(R.layout.activity_fill_the_blanks); // Load layout XML

        // Initialize spinner references by finding them in the layout
        spinners[0] = findViewById(R.id.spinner1); // First blank
        spinners[1] = findViewById(R.id.spinner2); // Second blank
        spinners[2] = findViewById(R.id.spinner3); // Third blank

        // Find the submit button in the layout
        Button submitButton = findViewById(R.id.submitButton);

        // Create an ArrayAdapter to populate the spinners with choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);

        // Attach the adapter to each spinner
        for (Spinner spinner : spinners) {
            spinner.setAdapter(adapter); // Show all choices in the dropdown
        }

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allCorrect = true; // Flag to track if all answers are correct

                // Check each spinner's selected value against the correct answers
                for (int i = 0; i < spinners.length; i++) {
                    String selectedAnswer = spinners[i].getSelectedItem().toString(); // Get selected choice
                    if (!selectedAnswer.equals(correctAnswers[i])) { // Compare with correct answer
                        allCorrect = false; // Mark as wrong if any answer is incorrect
                        break; // Stop checking further
                    }
                }

                // If all answers are correct, go to CorrectScreen7
                if (allCorrect) {
                    Intent intent = new Intent(FillTheBlanks.this, CorrectScreen7.class);
                    startActivity(intent); // Start correct answer screen
                } else {
                    // If any answer is wrong, go to Failure screen
                    Intent intent = new Intent(FillTheBlanks.this, Failure.class);
                    startActivity(intent); // Start failure screen
                }
            }
        });
    }
}
