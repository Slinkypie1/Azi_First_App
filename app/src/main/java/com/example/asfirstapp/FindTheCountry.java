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
        List<String> list = new ArrayList<>(Arrays.asList(allCountries));
        Collections.shuffle(list);
        String answer = list.get(0);
        questionText.setText("Find: " + answer);

        // Assuming you have drawable resources named like "outline_india", "outline_usa", etc.
        List<Region> options = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String country = list.get(i);
            // Map country name to outline drawable ID
            String outlineDrawableId = "outline_" + country.toLowerCase();  // assumes outlines are named like "outline_india"
            boolean isCorrect = country.equals(answer);

            options.add(new Region(country, outlineDrawableId, isCorrect));
        }

        recyclerView.setAdapter(new MapAdapter(this, options, isCorrect -> {
            if (isCorrect) {
                correctCount++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                if (correctCount == 5) {
                    Intent intent = new Intent(this, CorrectScreen8.class);
                    startActivity(intent);
                    finish();
                } else {
                    loadPuzzle();
                }
            } else {
                failed = true;
                Toast.makeText(this, "Wrong! You lose.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Failure.class));
                finish();
            }
        }));
    }

}
