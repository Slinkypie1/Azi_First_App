package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class FillTheBlanks extends AppCompatActivity {

    String[] choices = {"choose here", "jumping", "rising", "falling", "happiness", "glory", "walking"};

    String[] correctAnswers = {"glory", "falling", "rising"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_the_blanks);

        Spinner spinner1 = findViewById(R.id.spinner1);
        Spinner spinner2 = findViewById(R.id.spinner2);
        Spinner spinner3 = findViewById(R.id.spinner3);
        Button submitButton = findViewById(R.id.submitButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer1 = spinner1.getSelectedItem().toString();
                String answer2 = spinner2.getSelectedItem().toString();
                String answer3 = spinner3.getSelectedItem().toString();

                int correct = 0;
                if (answer1.equals(correctAnswers[0])) correct++;
                if (answer2.equals(correctAnswers[1])) correct++;
                if (answer3.equals(correctAnswers[2])) correct++;
                if (correct == 3) {
                    Intent intent = new Intent(FillTheBlanks.this, CorrectScreen7.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FillTheBlanks.this, Failure.class);
                    startActivity(intent);

            }
        }
    });
}}
