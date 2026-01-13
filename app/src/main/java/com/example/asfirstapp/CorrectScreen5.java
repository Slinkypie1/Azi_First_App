package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CorrectScreen5 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen5);
        initViews();

        // Level 5 completed, unlock Level 6
        unlockNextLevel(5);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    private void initViews() {
        BtClick18 = findViewById(R.id.BtClick18);
        BtClick18.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Puzzle3.class);
        startActivity(intent);
    }
}
