package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CorrectScreen7 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen7);

        BtClick20 = findViewById(R.id.BtClick20);
        BtClick20.setOnClickListener(this);

        // Level 7 completed, unlock Level 8
        unlockNextLevel(7);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, FindTheCountry.class);
        startActivity(intent);
    }
}
