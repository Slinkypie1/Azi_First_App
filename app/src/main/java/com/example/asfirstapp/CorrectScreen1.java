package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

public class CorrectScreen1 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_screen1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            return insets;
        });

        // Level 1 completed, unlock Level 2
        unlockNextLevel(1);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    private void initViews() {
        BtClick10 = findViewById(R.id.BtClick10);
        BtClick10.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, SecondQuestion.class);
        startActivity(intent);
    }
}
