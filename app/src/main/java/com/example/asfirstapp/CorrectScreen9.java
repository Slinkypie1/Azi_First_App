package com.example.asfirstapp;
// Declares the package where this class is stored

import android.content.Intent;
// Allows this activity to start another activity (screen)

import android.os.Bundle;
// Holds saved state data when the activity is recreated

import android.view.View;
// Base class for all UI components

import android.widget.Button;
// Button UI element

import android.widget.TextView;
// UI element used to display text

import androidx.activity.EdgeToEdge;
// Enables edge-to-edge screen layout support

import androidx.core.view.ViewCompat;
// Provides compatibility helpers for Views

import java.util.List;
// Used to store multiple leaderboard entries

import java.util.Map;
// Used to store key-value pairs for leaderboard data

// Activity shown when the user completes Level 9 successfully
public class CorrectScreen9 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick22;
    // Button that moves the user after finishing Level 9

    TextView leaderboardText;
    // TextView that displays the leaderboard

    long timeTaken;
    // Stores how long the user took to complete Level 9

    // Called automatically when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Calls setup logic from BaseMenuActivity

        EdgeToEdge.enable(this);
        // Enables full-screen layout behavior

        setContentView(R.layout.activity_correct_screen9);
        // Loads the XML layout for this screen

        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);
        // Retrieves the completion time sent from the previous activity

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();
            // Finds UI elements and sets listeners once layout is ready
            return insets;
        });

        // Marks Level 9 as completed and unlocks the next level if applicable
        unlockNextLevel(9);

        // Saves the player's score and loads the leaderboard
        saveAndLoadLeaderboard();
    }

    // Saves the level completion time and loads leaderboard data
    private void saveAndLoadLeaderboard() {

        if (timeTaken > 0) {
            // Only save if a valid completion time exists
            ProgressStorage.saveLevelCompletion(this, 9, timeTaken);
        }

        // Requests leaderboard data for Level 9
        ProgressStorage.getLeaderboard(9, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {
                // Called when leaderboard data is successfully loaded

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                // Builds the leaderboard text efficiently

                int rank = 1;
                // Keeps track of ranking order

                for (Map<String, Object> entry : entries) {
                    // Loops through each leaderboard entry

                    String name = (String) entry.get("userName");
                    // Extracts the player's name

                    long time = (long) entry.get("timeTakenMillis");
                    // Extracts the completion time in milliseconds

                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");
                    // Adds formatted leaderboard entry

                    rank++;
                    // Move to the next rank
                }

                leaderboardText.setText(sb.toString());
                // Displays the leaderboard on the screen
            }

            @Override
            public void onError(Exception e) {
                // Called if leaderboard loading fails
                leaderboardText.setText("Leaderboard unavailable");
            }
        });
    }

    // Finds UI elements and assigns click listeners
    private void initViews() {

        BtClick22 = findViewById(R.id.BtClick22);
        // Links the button from XML to Java

        BtClick22.setOnClickListener(this);
        // Sets this activity to handle button clicks

        leaderboardText = findViewById(R.id.leaderboardText);
        // Links the TextView from XML to Java
    }

    // Unlocks the next level only if this is the highest unlocked one
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            // Checks if the user just completed the furthest level

            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
            // Unlocks the next level (if one exists)
        }
    }

    // Called when the button is clicked
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, Second.class);
        // Creates an intent to return to the main menu or game-complete screen

        startActivity(intent);
        // Starts the target activity

        finish();
        // Closes this activity so it is removed from the back stack
    }
}
