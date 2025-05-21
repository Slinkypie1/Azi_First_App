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

        // Add the newly chosen answer to usedCountries to avoid repeats in next rounds
        usedCountries.add(answer);

        // Prepare the options list: always show first 6 countries (for example) with images,
        // regardless of usage, so all pictures are visible
        List<Region> options = new ArrayList<>();
        List<String> optionsForDisplay = new ArrayList<>(Arrays.asList(allCountries)); // all countries

        Collections.shuffle(optionsForDisplay);

        for (int i = 0; i < 6; i++) {
            String country = optionsForDisplay.get(i);
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
                    Intent intent = new Intent(this, CorrectScreen8.class);
                    startActivity(intent);
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
