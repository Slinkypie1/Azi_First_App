package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Ranking extends BaseMenuActivity {

    private TextView leaderboardText;
    private Button btnBackFromRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);

        // Start background music for Ranking Screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.ranking_music);
        startService(serviceIntent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        leaderboardText = findViewById(R.id.leaderboardText);
        btnBackFromRanking = findViewById(R.id.btnBackFromRanking);

        btnBackFromRanking.setOnClickListener(v -> finish());

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        ProgressStorage.getGameLeaderboard(this, new ProgressStorage.LeaderboardCallback() {
            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {
                StringBuilder sb = new StringBuilder();
                int rank = 1;
                for (Map<String, Object> entry : entries) {
                    String name = (String) entry.get("userName");
                    Long timeMillis = (Long) entry.get("totalTimeMillis");

                    if (name != null && timeMillis != null) {
                        String timeFormatted = formatTime(timeMillis);
                        sb.append(rank).append(". ").append(name)
                                .append(" - ").append(timeFormatted).append("\n\n");
                        rank++;
                    }
                }
                if (sb.length() == 0) {
                    leaderboardText.setText("No rankings yet. Be the first to finish!");
                } else {
                    leaderboardText.setText(sb.toString());
                }
            }

            @Override
            public void onError(Exception e) {
                leaderboardText.setText("Error loading leaderboard. Please try again later.");
            }
        });
    }

    private String formatTime(long durationMillis) {
        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long hours = (durationMillis / (1000 * 60 * 60));

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }
}
