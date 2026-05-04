package com.example.asfirstapp; // Package this class belongs to

import android.content.Intent; // Used to navigate between activities (screens)
import android.os.Bundle; // Used for activity lifecycle data
import android.view.View; // Base class for UI interactions
import android.widget.*; // Imports UI widgets like Spinner, Button, Toast

import androidx.appcompat.app.AppCompatActivity; // Base activity class (not directly used since BaseMenuActivity extends it)

// Activity for the "Fill the Blanks" quiz
public class FillTheBlanks extends BaseMenuActivity {

    // All possible choices shown in the dropdowns
    String[] choices = {"choose here", "jumping", "rising", "falling", "happiness", "glory", "walking"};

    // Correct answers in order for each blank
    String[] correctAnswers = {"glory", "falling", "rising"};

    // Array holding the 3 spinner UI elements
    Spinner[] spinners = new Spinner[3];

    private long startTime; // Stores when the quiz starts (for timing)
    private int attempts = 0; // Tracks incorrect attempts

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call parent setup
        setContentView(R.layout.activity_fill_the_blanks); // Load layout XML

        // Start background music for this level
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.fill_the_blanks_music);
        startService(serviceIntent);

        startTime = System.currentTimeMillis(); // Record start time

        // Connect spinner UI elements from XML
        spinners[0] = findViewById(R.id.spinner1);
        spinners[1] = findViewById(R.id.spinner2);
        spinners[2] = findViewById(R.id.spinner3);

        // Connect submit button
        Button submitButton = findViewById(R.id.submitButton);

        // Create adapter to show dropdown choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                choices
        );

        // Attach adapter to each spinner
        for (Spinner spinner : spinners) {
            spinner.setAdapter(adapter);
        }

        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean allCorrect = true; // Track if all answers are correct

                // Check each spinner answer
                for (int i = 0; i < spinners.length; i++) {

                    String selectedAnswer = spinners[i].getSelectedItem().toString();
                    // Get selected value from spinner

                    if (!selectedAnswer.equals(correctAnswers[i])) {
                        // If answer is wrong
                        allCorrect = false;
                        break; // stop checking early
                    }
                }

                // If all answers are correct → success
                if (allCorrect) {

                    long timeTaken = System.currentTimeMillis() - startTime;
                    // Calculate completion time

                    Intent intent = new Intent(FillTheBlanks.this, CorrectScreen7.class);
                    // Go to success screen

                    intent.putExtra("TIME_TAKEN", timeTaken);
                    // Pass time to next screen

                    startActivity(intent);
                    finish(); // close this activity
                }

                // If answers are wrong
                else {
                    attempts++; // increase fail count

                    if (attempts >= 2) {
                        // Too many attempts → fail screen
                        Intent intent = new Intent(FillTheBlanks.this, Failure.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // First mistake → show warning
                        Toast.makeText(FillTheBlanks.this,
                                "Incorrect! 1 try remaining.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}