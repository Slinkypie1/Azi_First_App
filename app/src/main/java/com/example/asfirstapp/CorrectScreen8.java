package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CorrectScreen8 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen8);

        BtClick21 = findViewById(R.id.BtClick21);
        BtClick21.setOnClickListener(this);

        // Level 8 completed, unlock Level 9
        unlockNextLevel(8);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, UnlockCityActivity.class);
        startActivity(intent);
    }
}
