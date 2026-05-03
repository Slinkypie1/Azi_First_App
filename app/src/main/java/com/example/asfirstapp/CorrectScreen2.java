package com.example.asfirstapp;
// Specifies the package where this class is located

import android.content.Intent;
// Allows this activity to start another activity (screen)

import android.os.Bundle;
// Used to receive saved state data when the activity starts

import android.view.View;
// Base class for UI components like buttons and layouts

import android.widget.Button;
// Button UI component

import android.widget.TextView;
// TextView UI component for displaying text

import androidx.activity.EdgeToEdge;
// Enables drawing the UI edge-to-edge on the screen

import androidx.core.view.ViewCompat;
// Provides backward-compatible view features

import java.util.List;
// Used to store multiple leaderboard entries

import java.util.Map;
// Used to store key-value pairs for each leaderboard entry

// Screen that appears when the user answers Level 2 correctly
public class CorrectScreen2 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick15;
    // Button that moves the user to the next level

    TextView leaderboardText;
    // TextView that displays the leaderboard

    long timeTaken;
    // Stores how long the user took to complete Level 2

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Calls the parent activity's onCreate method

        EdgeToEdge.enable(this);
        // Enables full-screen edge-to-edge layout

        setContentView(R.layout.activity_correct_screen2);
        // Loads the XML layout for this screen

        // Start background music for Correct Screens
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);
        // Gets the time taken from the previous activity (default is 0)

        // Applies system window insets and initializes views
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            unlockNextLevel(2);
            saveAndLoadLeaderboard();
            return insets;
        });
    }

    // Saves the level completion time and loads leaderboard data
    private void saveAndLoadLeaderboard() {
        // Check game mode
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // Save completion (handles achievements even in casual mode)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 2, timeTaken);
        }

        // If in casual mode, hide the leaderboard
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // Loads the leaderboard for Level 2
        ProgressStorage.getLeaderboard(this, 2, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {
                // Called when leaderboard data is successfully retrieved

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                // Used to build the leaderboard text

                int rank = 1;
                // Keeps track of player ranking

                for (Map<String, Object> entry : entries) {
                    // Loop through each leaderboard entry

                    String name = (String) entry.get("userName");
                    // Gets the player's name

                    long time = (long) entry.get("timeTakenMillis");
                    // Gets the player's completion time in milliseconds

                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");
                    // Adds formatted leaderboard entry

                    rank++;
                    // Move to the next rank
                }

                leaderboardText.setText(sb.toString());
                // Displays the leaderboard on screen
            }

            @Override
            public void onError(Exception e) {
                // Called if loading the leaderboard fails

                leaderboardText.setText("Leaderboard unavailable");
                // Displays an error message
            }
        });
    }

    // Unlocks the next level if this one was the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            // Check if the player finished the latest unlocked level

            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
            // Unlock the next level
        }
    }

    // Finds UI elements and sets click listeners
    private void initViews() {

        BtClick15 = findViewById(R.id.BtClick15);
        // Connects the button from XML to Java

        BtClick15.setOnClickListener(this);
        // Sets this activity to handle button clicks

        leaderboardText = findViewById(R.id.leaderboardText);
        // Connects the leaderboard TextView from XML
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, ThirdQuestion.class);
        // Creates an intent to move to the Level 3 question screen

        startActivity(intent);
        // Starts the next activity
    }
}

