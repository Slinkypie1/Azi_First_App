package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CorrectScreen9 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen9);

        BtClick22 = findViewById(R.id.BtClick22);
        BtClick22.setOnClickListener(this);

        // Level 9 completed. (If there's a Level 10, unlock it here)
        unlockNextLevel(9);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    @Override
    public void onClick(View view) {
        // Typically, this might go to a "game completed" screen or back to menu
        Intent intent = new Intent(this, Second.class);
        startActivity(intent);
        finish();
    }
}
