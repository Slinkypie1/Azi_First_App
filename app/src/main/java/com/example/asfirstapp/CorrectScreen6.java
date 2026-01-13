package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CorrectScreen6 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen6);

        BtClick19 = findViewById(R.id.BtClick19);
        BtClick19.setOnClickListener(this);

        // Level 6 completed, unlock Level 7
        unlockNextLevel(6);
    }

    private void unlockNextLevel(int currentLevel) {
        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, FillTheBlanks.class);
        startActivity(intent);
    }
}
