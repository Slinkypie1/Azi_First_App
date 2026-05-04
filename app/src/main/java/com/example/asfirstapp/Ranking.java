package com.example.asfirstapp;

// Imports for navigation, UI components, and Android lifecycle handling
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

/**
 * Ranking Activity
 * ----------------
 * Displays the game leaderboard by fetching data from Firebase via ProgressStorage.
 * Shows players ranked by total completion time.
 */
public class Ranking extends BaseMenuActivity {

    // UI elements
    private TextView leaderboardText;          // Displays leaderboard results
    private Button btnBackFromRanking;        // Button to return to previous screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge UI layout
        EdgeToEdge.enable(this);

        // Set layout for this activity
        setContentView(R.layout.activity_ranking);

        // Start background music for ranking screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.ranking_music);
        startService(serviceIntent);

        // Handle system UI insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link UI elements
        leaderboardText = findViewById(R.id.leaderboardText);
        btnBackFromRanking = findViewById(R.id.btnBackFromRanking);

        // Back button closes activity
        btnBackFromRanking.setOnClickListener(v -> finish());

        // Load leaderboard data from Firebase/local storage
        loadLeaderboard();
    }

    /**
     * Fetches leaderboard data from ProgressStorage and displays it.
     */
    private void loadLeaderboard() {

        ProgressStorage.getGameLeaderboard(this, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder();
                int rank = 1;

                // Build leaderboard display text
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName");              // Player name
                    Long timeMillis = (Long) entry.get("totalTimeMillis");     // Completion time

                    if (name != null && timeMillis != null) {

                        // Convert milliseconds into readable format
                        String timeFormatted = formatTime(timeMillis);

                        // Append formatted ranking line
                        sb.append(rank).append(". ")
                                .append(name)
                                .append(" - ")
                                .append(timeFormatted)
                                .append("\n\n");

                        rank++;
                    }
                }

                // If no entries exist, show placeholder message
                if (sb.length() == 0) {
                    leaderboardText.setText("No rankings yet. Be the first to finish!");
                } else {
                    leaderboardText.setText(sb.toString());
                }
            }

            @Override
            public void onError(Exception e) {
                // Show error message if Firebase request fails
                leaderboardText.setText("Error loading leaderboard. Please try again later.");
            }
        });
    }

    /**
     * Converts milliseconds into a readable time format.
     * Example:
     * - 90,000 ms → 01:30
     * - 3,660,000 ms → 01:01:00
     */
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
