package com.example.asfirstapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;

import java.util.Set;

public class TrophyRoom extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trophy_room);

        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The BaseMenuActivity's onResume calls applyAppearance() 
        // which will handle background/text colors for us.
    }

    private void initViews() {
        Button btnBack = findViewById(R.id.btnBackFromTrophy);
        btnBack.setOnClickListener(v -> finish());

        Set<String> earned = ProgressStorage.getEarnedAchievements(this);
        Log.d("TrophyRoom", "Earned Achievements: " + earned.toString());

        if (earned.contains(ProgressStorage.ACHIEV_SPEED_DEMON)) {
            findViewById(R.id.layoutSpeedDemon).setAlpha(1.0f);
        }
        if (earned.contains(ProgressStorage.ACHIEV_PERFECTIONIST)) {
            findViewById(R.id.layoutPerfectionist).setAlpha(1.0f);
        }
        if (earned.contains(ProgressStorage.ACHIEV_WORLD_TRAVELER)) {
            findViewById(R.id.layoutWorldTraveler).setAlpha(1.0f);
        }
        if (earned.contains(ProgressStorage.ACHIEV_RANKED)) {
            findViewById(R.id.layoutRanked).setAlpha(1.0f);
        }
        if (earned.contains(ProgressStorage.ACHIEV_TOP_10)) {
            findViewById(R.id.layoutTop10).setAlpha(1.0f);
        }
    }
}
