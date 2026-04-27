package com.example.asfirstapp;
// Specifies the package where this class is located

import android.content.Intent;
// Used to start another activity (screen)

import android.os.Bundle;
// Holds saved state information for the activity

import android.view.View;
// Base class for all UI elements

import android.widget.Button;
// Button UI component

import android.widget.TextView;
// TextView UI component for displaying text

import androidx.activity.EdgeToEdge;
// Enables edge-to-edge full-screen layout

import androidx.core.view.ViewCompat;
// Provides backward-compatible view features

import java.util.List;
// Used to store leaderboard entries

import java.util.Map;
// Used to store key-value pairs for leaderboard data

// Screen shown when the user completes Level 7 correctly
public class CorrectScreen7 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick20;
    // Button that moves the user to Level 8

    TextView leaderboardText;
    // TextView that displays the leaderboard

    long timeTaken;
    // Stores how long the user took to complete Level 7

    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Calls the parent activity setup code

        EdgeToEdge.enable(this);
        // Enables full-screen edge-to-edge layout

        setContentView(R.layout.activity_correct_screen7);
        // Loads the XML layout for this screen

        // Start background music for Correct Screens
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);
        // Retrieves the completion time passed from the previous activity

        // Applies window insets and initializes UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            unlockNextLevel(7);
            saveAndLoadLeaderboard();
            return insets;
        });
    }

    // Saves the completion time and loads the leaderboard for Level 7
    private void saveAndLoadLeaderboard() {
        // Check game mode
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // If in casual mode, hide the leaderboard and don't save time
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        if (timeTaken > 0) {
            // Only save if a valid time exists

            ProgressStorage.saveLevelCompletion(this, 7, timeTaken);
            // Saves completion time for Level 7
        }

        // Requests leaderboard data for Level 7
        ProgressStorage.getLeaderboard(7, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {
                // Called when leaderboard data is successfully loaded

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                // Used to build the leaderboard display text

                int rank = 1;
                // Ranking counter

                for (Map<String, Object> entry : entries) {
                    // Loop through each leaderboard entry

                    String name = (String) entry.get("userName");
                    // Get the player's name

                    long time = (long) entry.get("timeTakenMillis");
                    // Get the completion time in milliseconds

                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");
                    // Add formatted entry to the leaderboard

                    rank++;
                    // Move to the next rank
                }

                leaderboardText.setText(sb.toString());
                // Display the leaderboard on screen
            }

            @Override
            public void onError(Exception e) {
                // Called if leaderboard loading fails

                leaderboardText.setText("Leaderboard unavailable");
                // Show an error message
            }
        });
    }

    // Finds views in the layout and sets click listeners
    private void initViews() {

        BtClick20 = findViewById(R.id.BtClick20);
        // Connects the button from XML to Java

        BtClick20.setOnClickListener(this);
        // Sets this activity as the click handler

        leaderboardText = findViewById(R.id.leaderboardText);
        // Connects the leaderboard TextView from XML
    }

    // Unlocks the next level if this level was the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            // Check if Level 7 was the latest unlocked level

            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
            // Unlock Level 8
        }
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, FindTheCountry.class);
        // Creates an intent to start the Level 8 activity

        startActivity(intent);
        // Launches the next activity
    }
}
