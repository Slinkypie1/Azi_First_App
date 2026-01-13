package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

public class CorrectScreen3 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_screen3);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            return insets;
        });

        // Level 3 completed, unlock Level 4
        unlockNextLevel(3);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    private void initViews() {
        BtClick16 = findViewById(R.id.BtClick16);
        BtClick16.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Puzzle1.class);
        startActivity(intent);
    }
}
