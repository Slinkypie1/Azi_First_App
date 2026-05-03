package com.example.asfirstapp;
// Declares the package name where this class is located

import android.content.Intent;
// Allows this activity to start another activity (screen)

import android.os.Bundle;
// Used to pass and restore activity state data

import android.view.View;
// Base class for all UI components

import android.widget.Button;
// UI element that the user can tap

import android.widget.TextView;
// UI element used to display text on the screen

import androidx.activity.EdgeToEdge;
// Enables drawing the UI edge-to-edge on modern Android devices

import androidx.core.view.ViewCompat;
// Provides compatibility helpers for Views

import java.util.List;
// Used to store multiple leaderboard entries

import java.util.Map;
// Used to store key-value pairs for leaderboard data

// Activity shown when the user completes Level 8 successfully
public class CorrectScreen8 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick21;
    // Button that moves the user to Level 9

    TextView leaderboardText;
    // TextView used to display leaderboard results

    long timeTaken;
    // Stores how long the user took to complete Level 8

    // Called automatically when this activity starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Calls the parent activity's setup code

        EdgeToEdge.enable(this);
        // Enables full-screen layout handling

        setContentView(R.layout.activity_correct_screen8);
        // Loads the XML layout for this screen

        // Start background music for Correct Screens
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);
        // Retrieves the completion time passed from the previous activity

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            unlockNextLevel(8);
            saveAndLoadLeaderboard();
            return insets;
        });
    }

    // Saves the user's time and loads leaderboard data for Level 8
    private void saveAndLoadLeaderboard() {
        // Check game mode
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // Save completion (handles achievements even in casual mode)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 8, timeTaken);
        }

        // If in casual mode, hide the leaderboard
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // Requests leaderboard data for Level 8
        ProgressStorage.getLeaderboard(this, 8, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {
                // Called when leaderboard data is successfully loaded

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                // Used to build the leaderboard text

                int rank = 1;
                // Keeps track of the player's rank

                for (Map<String, Object> entry : entries) {
                    // Loops through each leaderboard entry

                    String name = (String) entry.get("userName");
                    // Gets the player's name

                    long time = (long) entry.get("timeTakenMillis");
                    // Gets the completion time in milliseconds

                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");
                    // Adds formatted entry to the leaderboard text

                    rank++;
                    // Move to the next rank
                }

                leaderboardText.setText(sb.toString());
                // Displays the leaderboard on screen
            }

            @Override
            public void onError(Exception e) {
                // Called if leaderboard loading fails
                leaderboardText.setText("Leaderboard unavailable");
            }
        });
    }

    // Finds UI elements and sets click listeners
    private void initViews() {

        BtClick21 = findViewById(R.id.BtClick21);
        // Connects the button to the XML layout

        BtClick21.setOnClickListener(this);
        // Sets this activity to handle button clicks

        leaderboardText = findViewById(R.id.leaderboardText);
        // Connects the leaderboard TextView to the XML
    }

    // Unlocks the next level only if this is the highest unlocked one
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            // Checks if the player just completed the furthest level

            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
            // Unlocks the next level
        }
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, UnlockCityActivity.class);
        // Creates an intent to open the Level 9 / city unlock screen

        startActivity(intent);
        // Starts the next activity
    }
}

