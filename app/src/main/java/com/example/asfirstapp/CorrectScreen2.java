package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

public class CorrectScreen2 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_screen2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            return insets;
        });

        // Level 2 completed, unlock Level 3
        unlockNextLevel(2);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    private void initViews() {
        BtClick15 = findViewById(R.id.BtClick15);
        BtClick15.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, ThirdQuestion.class);
        startActivity(intent);
    }
}
