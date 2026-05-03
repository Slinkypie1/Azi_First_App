package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

public class CasualFinish extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_casual_finish);

        // Achievement: Perfectionist (No walls hit)
        if (!ProgressStorage.wasWallHit()) {
            ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_PERFECTIONIST);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            return insets;
        });
    }

    private void initViews() {
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        Button btnTryTimedMode = findViewById(R.id.btnTryTimedMode);

        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(CasualFinish.this, Second.class);
            startActivity(intent);
            finish();
        });

        btnTryTimedMode.setOnClickListener(v -> {
            // Set game mode to timed
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("game_mode", "timed")
                    .apply();

            // Sync to Firebase
            ProgressStorage.syncGameModeToFirebase(this, "timed");

            // Go back to the menu
            Intent intent = new Intent(CasualFinish.this, Second.class);
            startActivity(intent);
            finish();
        });
    }
}
