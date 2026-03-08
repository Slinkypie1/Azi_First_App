package com.example.asfirstapp;
// Defines the package this class belongs to

import android.content.Intent;
// Needed to switch activities

import android.os.Bundle;
// Needed for activity lifecycle methods like onCreate

import android.widget.TextView;
// Used to display the current question and feedback
import android.widget.Toast;
// Needed to show feedback for incorrect attempts

import androidx.appcompat.app.AppCompatActivity;
// Base class for activities with AppCompat support

import androidx.recyclerview.widget.GridLayoutManager;
// Layout manager for RecyclerView to arrange items in a grid

import androidx.recyclerview.widget.RecyclerView;
// RecyclerView to display country options as selectable items

import java.util.*;
// Imports List, ArrayList, Arrays, Collections utilities

// Activity for the "Find the Country" puzzle
public class FindTheCountry extends BaseMenuActivity {

    // List of all countries available for the puzzle
    String[] allCountries = {"India", "USA", "France", "Germany", "Japan", "Brazil", "Canada", "Italy", "China"};

    int correctCount = 0; // Number of correct selections the user has made
    int wrongCount = 0;   // Number of incorrect selections the user has made

    TextView questionText; // Displays the "Find: Country" question
    RecyclerView recyclerView; // Shows selectable countries in a grid

    List<String> usedCountries = new ArrayList<>();
    // Keeps track of countries already used in the current session

    private long startTime;
    // Records when the puzzle starts to measure completion time

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Call parent setup

        setContentView(R.layout.activity_find_the_country);
        // Load the layout XML for this activity

        startTime = System.currentTimeMillis();
        // Record start time

        questionText = findViewById(R.id.questionText);
        // Find TextView in layout

        recyclerView = findViewById(R.id.recyclerView);
        // Find RecyclerView in layout

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        // Set RecyclerView to display items in a 3-column grid

        loadPuzzle();
        // Load the first puzzle question
    }

    // Method to load a new country puzzle
    void loadPuzzle() {
        // Start with all countries and remove already used ones
        List<String> availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        availableCountries.removeAll(usedCountries);

        // Reset used countries if all countries have been used
        if (availableCountries.isEmpty()) {
            usedCountries.clear();
            availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        }

        Collections.shuffle(availableCountries);
        // Randomize the order of countries

        String answer = availableCountries.get(0);
        // Pick the first country as the correct answer

        questionText.setText("Find: " + answer);
        // Display the question

        usedCountries.add(answer);
        // Mark this country as used

        // Prepare options to show in RecyclerView
        List<String> optionsForDisplay = new ArrayList<>(Arrays.asList(allCountries));
        optionsForDisplay.remove(answer);
        // Remove the correct answer from the incorrect options

        Collections.shuffle(optionsForDisplay);
        // Shuffle the incorrect options

        List<String> finalOptions = new ArrayList<>();
        finalOptions.add(answer);
        // Add the correct answer first

        for (int i = 0; i < 5; i++) {
            finalOptions.add(optionsForDisplay.get(i));
            // Add 5 random incorrect options
        }

        Collections.shuffle(finalOptions);
        // Shuffle so the correct answer appears in a random position

        // Create Region objects for RecyclerView adapter
        List<Region> options = new ArrayList<>();
        for (String country : finalOptions) {
            String outlineDrawableId = "outline_" + country.toLowerCase();
            // Name of drawable resource for country outline

            boolean isCorrect = country.equals(answer);
            // True if this is the correct answer

            options.add(new Region(country, outlineDrawableId, isCorrect));
        }

        // Set RecyclerView adapter with the options and a click listener
        recyclerView.setAdapter(new MapAdapter(this, options, isCorrect -> {
            if (isCorrect) {
                // User clicked the correct country
                correctCount++;
                // Increment correct answer count

                TextView textView = findViewById(R.id.winOrLose);
                if (textView != null) textView.setText("Correct");
                // Display positive feedback

                if (correctCount == 5) {
                    // If 5 correct answers, puzzle is complete
                    long timeTaken = System.currentTimeMillis() - startTime;

                    Intent intent = new Intent(this, CorrectScreen8.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    startActivity(intent);
                    // Go to the next level

                    finish();
                    // Close current activity
                } else {
                    loadPuzzle();
                    // Load the next country puzzle
                }
            } else {
                // User clicked a wrong country
                wrongCount++;

                if (wrongCount >= 3) {
                    // 3 strikes and you're out
                    startActivity(new Intent(this, Failure.class));
                    finish();
                } else {
                    // Show how many tries are left
                    int remaining = 3 - wrongCount;
                    Toast.makeText(this, "Incorrect! " + remaining + " tries remaining.", Toast.LENGTH_SHORT).show();
                    
                    // Optional: reload the puzzle to provide a new challenge after a mistake
                    loadPuzzle();
                }
            }
        }));
    }
}
