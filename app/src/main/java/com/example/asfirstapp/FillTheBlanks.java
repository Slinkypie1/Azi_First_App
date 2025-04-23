package com.example.asfirstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class FillTheBlanks extends AppCompatActivity {

    String[] choices = {"glory", "falling", "rising", "happiness", "jumping", "walking"};

    String[] correctAnswers = {"glory", "falling", "rising"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner1 = findViewById(R.id.spinner1);
        Spinner spinner2 = findViewById(R.id.spinner2);
        Spinner spinner3 = findViewById(R.id.spinner3);
        Button submitButton = findViewById(R.id.submitButton);
        TextView resultText = findViewById(R.id.resultText);

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

                resultText.setText("You got " + correct + " out of 3 correct.");
            }
        });
    }
}
