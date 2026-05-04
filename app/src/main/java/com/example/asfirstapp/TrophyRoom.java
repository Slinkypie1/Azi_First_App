package com.example.asfirstapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

import java.util.Set;

/**
 * TrophyRoom Activity
 * --------------------
 * Displays all player achievements (trophies).
 * Each trophy is shown unlocked (fully visible) or locked (faded)
 * depending on what is stored in ProgressStorage.
 */
public class TrophyRoom extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Set layout for this screen
        setContentView(R.layout.activity_trophy_room);

        // Initialize UI elements and logic
        initViews();

        // Handle system window insets (status bar / navigation bar spacing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // BaseMenuActivity handles appearance updates (theme, colors, etc.)
        // No additional logic needed here
    }

    /**
     * Initializes UI elements and updates trophy visibility
     * based on unlocked achievements.
     */
    private void initViews() {

        // Back button returns to previous screen
        Button btnBack = findViewById(R.id.btnBackFromTrophy);
        btnBack.setOnClickListener(v -> finish());

        // Get all earned achievements from storage
        Set<String> earned = ProgressStorage.getEarnedAchievements(this);
        Log.d("TrophyRoom", "Earned Achievements: " + earned.toString());

        // Speed Demon trophy
        if (earned.contains(ProgressStorage.ACHIEV_SPEED_DEMON)) {
            findViewById(R.id.layoutSpeedDemon).setAlpha(1.0f);
        }

        // Perfectionist trophy
        if (earned.contains(ProgressStorage.ACHIEV_PERFECTIONIST)) {
            findViewById(R.id.layoutPerfectionist).setAlpha(1.0f);
        }

        // World Traveler trophy
        if (earned.contains(ProgressStorage.ACHIEV_WORLD_TRAVELER)) {
            findViewById(R.id.layoutWorldTraveler).setAlpha(1.0f);
        }

        // Ranked trophy
        if (earned.contains(ProgressStorage.ACHIEV_RANKED)) {
            findViewById(R.id.layoutRanked).setAlpha(1.0f);
        }

        // Top 10 leaderboard trophy
        if (earned.contains(ProgressStorage.ACHIEV_TOP_10)) {
            findViewById(R.id.layoutTop10).setAlpha(1.0f);
        }
    }
}