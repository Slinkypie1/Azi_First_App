package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class FindTheCountry extends AppCompatActivity {

    String[] allCountries = {"India", "USA", "France", "Germany", "Japan", "Brazil", "Canada", "Italy", "China"};
    int correctCount = 0;
    boolean failed = false;

    TextView questionText;
    RecyclerView recyclerView;

    // Track used countries globally
    List<String> usedCountries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_the_country);

        questionText = findViewById(R.id.questionText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        loadPuzzle();
    }

    void loadPuzzle() {
        List<String> availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        availableCountries.removeAll(usedCountries);

        // If all countries used, reset to start over
        if (availableCountries.isEmpty()) {
            usedCountries.clear();
            availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        }

        Collections.shuffle(availableCountries);
        String answer = availableCountries.get(0);
        questionText.setText("Find: " + answer);
        usedCountries.add(answer);

        // Create options list and ensure the correct answer is included
        List<String> optionsForDisplay = new ArrayList<>(Arrays.asList(allCountries));
        optionsForDisplay.remove(answer);
        Collections.shuffle(optionsForDisplay);

        List<String> finalOptions = new ArrayList<>();
        finalOptions.add(answer); // Ensure the answer is in the list

        for (int i = 0; i < 5; i++) {
            finalOptions.add(optionsForDisplay.get(i));
        }

        Collections.shuffle(finalOptions); // Shuffle to randomize position

        List<Region> options = new ArrayList<>();
        for (String country : finalOptions) {
            String outlineDrawableId = "outline_" + country.toLowerCase();
            boolean isCorrect = country.equals(answer);
            options.add(new Region(country, outlineDrawableId, isCorrect));
        }

        recyclerView.setAdapter(new MapAdapter(this, options, isCorrect -> {
            if (isCorrect) {
                correctCount++;
                TextView textView = findViewById(R.id.winOrLose);
                textView.setText("Correct");
                if (correctCount == 5) {
                    startActivity(new Intent(this, CorrectScreen8.class));
                    finish();
                } else {
                    loadPuzzle();
                }
            } else {
                failed = true;
                startActivity(new Intent(this, Failure.class));
                finish();
            }
        }));
    }
}
