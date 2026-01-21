package com.example.asfirstapp;
// Defines the package this class belongs to

import android.content.Intent;
// Needed to switch between activities (screens)

import android.os.Bundle;
// Needed for activity lifecycle methods like onCreate

import android.view.View;
// Needed to handle clicks and UI interactions

import android.widget.*;
// Imports Spinner, Button, and other widgets

import androidx.appcompat.app.AppCompatActivity;
// Base class for activities with AppCompat support

// Activity for the "Fill the Blanks" quiz
public class FillTheBlanks extends BaseMenuActivity {

    // Array of all possible choices for the blanks
    String[] choices = {"choose here", "jumping", "rising", "falling", "happiness", "glory", "walking"};

    // Correct answers for each blank, in order
    String[] correctAnswers = {"glory", "falling", "rising"};

    // Array to store references to the three Spinner widgets
    Spinner[] spinners = new Spinner[3];

    private long startTime;
    // Records when the quiz starts, used to calculate completion time

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls the parent activity's setup code

        setContentView(R.layout.activity_fill_the_blanks);
        // Loads the layout XML for this activity

        startTime = System.currentTimeMillis();
        // Records the start time for timing the quiz

        // Initialize spinner references by finding them in the layout
        spinners[0] = findViewById(R.id.spinner1); // First blank
        spinners[1] = findViewById(R.id.spinner2); // Second blank
        spinners[2] = findViewById(R.id.spinner3); // Third blank

        // Find the submit button in the layout
        Button submitButton = findViewById(R.id.submitButton);

        // Create an ArrayAdapter to populate the spinners with choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                choices
        );

        // Attach the adapter to each spinner to show the choices in a dropdown
        for (Spinner spinner : spinners) {
            spinner.setAdapter(adapter);
        }

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allCorrect = true;
                // Flag to track if all blanks are answered correctly

                // Check each spinner's selected value against the correct answers
                for (int i = 0; i < spinners.length; i++) {
                    String selectedAnswer = spinners[i].getSelectedItem().toString();
                    // Get the user's selected choice from spinner

                    if (!selectedAnswer.equals(correctAnswers[i])) {
                        // Compare the selected answer with the correct one
                        allCorrect = false; // Mark as incorrect if any answer is wrong
                        break; // Stop checking further
                    }
                }

                // If all answers are correct, go to CorrectScreen7
                if (allCorrect) {
                    long timeTaken = System.currentTimeMillis() - startTime;
                    // Calculate the total time taken for this quiz

                    Intent intent = new Intent(FillTheBlanks.this, CorrectScreen7.class);
                    // Prepare to start the correct answer screen

                    intent.putExtra("TIME_TAKEN", timeTaken);
                    // Pass the time taken to the next screen

                    startActivity(intent);
                    // Launch CorrectScreen7
                } else {
                    // If any answer is wrong, go to the Failure screen
                    Intent intent = new Intent(FillTheBlanks.this, Failure.class);
                    startActivity(intent);
                    // Launch the failure screen
                }
            }
        });
    }
}
