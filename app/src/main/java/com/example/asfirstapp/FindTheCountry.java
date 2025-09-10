package com.example.asfirstapp;

import android.content.Intent; // Needed to switch activities
import android.os.Bundle; // Needed for activity lifecycle
import android.widget.TextView; // For displaying the question and feedback
import androidx.appcompat.app.AppCompatActivity; // Base class for activities
import androidx.recyclerview.widget.GridLayoutManager; // Layout manager for RecyclerView
import androidx.recyclerview.widget.RecyclerView; // RecyclerView to display country options

import java.util.*; // For List, ArrayList, Arrays, Collections

public class FindTheCountry extends AppCompatActivity {

    // List of all countries available for the puzzle
    String[] allCountries = {"India", "USA", "France", "Germany", "Japan", "Brazil", "Canada", "Italy", "China"};

    int correctCount = 0; // Number of correct selections made
    boolean failed = false; // Tracks if the user has made a wrong choice

    TextView questionText; // Displays the current "Find: Country" question
    RecyclerView recyclerView; // Shows the selectable countries as a grid

    // Keeps track of countries that have already been used in this session
    List<String> usedCountries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call parent method
        setContentView(R.layout.activity_find_the_country); // Load the layout

        questionText = findViewById(R.id.questionText); // Find TextView in layout
        recyclerView = findViewById(R.id.recyclerView); // Find RecyclerView in layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns grid layout

        loadPuzzle(); // Load the first puzzle question
    }

    // Method to load a new country puzzle
    void loadPuzzle() {
        // Start with all countries and remove ones already used
        List<String> availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        availableCountries.removeAll(usedCountries);

        // Reset used countries if all have been used
        if (availableCountries.isEmpty()) {
            usedCountries.clear();
            availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        }

        Collections.shuffle(availableCountries); // Shuffle to randomize the next country
        String answer = availableCountries.get(0); // Pick the first country as the correct answer
        questionText.setText("Find: " + answer); // Show question
        usedCountries.add(answer); // Mark this country as used

        // Prepare options for display
        List<String> optionsForDisplay = new ArrayList<>(Arrays.asList(allCountries));
        optionsForDisplay.remove(answer); // Remove the correct answer
        Collections.shuffle(optionsForDisplay); // Shuffle the remaining options

        List<String> finalOptions = new ArrayList<>();
        finalOptions.add(answer); // Add the correct answer
        for (int i = 0; i < 5; i++) { // Add 5 random incorrect options
            finalOptions.add(optionsForDisplay.get(i));
        }

        Collections.shuffle(finalOptions); // Shuffle so the correct answer is in a random position

        // Create Region objects for RecyclerView adapter
        List<Region> options = new ArrayList<>();
        for (String country : finalOptions) {
            String outlineDrawableId = "outline_" + country.toLowerCase(); // Name of drawable for country outline
            boolean isCorrect = country.equals(answer); // True if this option is the correct country
            options.add(new Region(country, outlineDrawableId, isCorrect));
        }

        // Set the RecyclerView adapter with the options and a click listener
        recyclerView.setAdapter(new MapAdapter(this, options, isCorrect -> {
            if (isCorrect) {
                correctCount++; // Increment correct answer count
                TextView textView = findViewById(R.id.winOrLose);
                textView.setText("Correct"); // Display feedback

                if (correctCount == 5) { // If 5 correct answers, puzzle complete
                    startActivity(new Intent(this, CorrectScreen8.class)); // Go to next correct screen
                    finish(); // Finish current activity
                } else {
                    loadPuzzle(); // Load next country puzzle
                }
            } else {
                failed = true; // Mark failure
                startActivity(new Intent(this, Failure.class)); // Go to failure screen
                finish(); // Finish current activity
            }
        }));
    }
}
