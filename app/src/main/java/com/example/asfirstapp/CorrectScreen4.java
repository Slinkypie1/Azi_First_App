package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

public class CorrectScreen4 extends BaseMenuActivity implements View.OnClickListener {

    Button BtCLick17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_screen4);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            return insets;
        });

        // Level 4 completed, unlock Level 5
        unlockNextLevel(4);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    private void initViews() {
        BtCLick17 = findViewById(R.id.BtClick17);
        BtCLick17.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Puzzle2.class);
        startActivity(intent);
    }
}
