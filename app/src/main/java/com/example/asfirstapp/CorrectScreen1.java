package com.example.asfirstapp;
// Defines the package name where this class is located

import android.content.Intent;
// Allows this activity to start another activity (screen)

import android.os.Bundle;
// Used to pass saved state data when the activity is created

import android.view.View;
// Base class for all UI elements like buttons and text

import android.widget.Button;
// Button UI element

import android.widget.TextView;
// TextView UI element used to display text

import android.widget.Toast;
// Used to show short popup messages (not used yet)

import androidx.activity.EdgeToEdge;
// Enables drawing the UI edge-to-edge on the screen

import androidx.core.view.ViewCompat;
// Provides compatibility helpers for views across Android versions

import java.util.List;
// Represents a list of items

import java.util.Map;
// Represents key-value pairs

// This screen appears when the user answers Level 1 correctly
public class CorrectScreen1 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick10;
    // Button that sends the player to the next level

    TextView leaderboardText;
    // TextView that displays the leaderboard

    long timeTaken;
    // Stores how long the player took to finish the level

    // Runs when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Calls the parent activity's setup code

        EdgeToEdge.enable(this);
        // Enables edge-to-edge screen layout

        setContentView(R.layout.activity_correct_screen1);
        // Loads the XML layout for this screen

        // Start background music for Correct Screens
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);
        // Gets the time taken from the previous activity (default is 0)

        // Applies window insets (safe areas) and initializes UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews();
            // Finds UI elements and sets listeners

            return insets;
            // Returns the insets unchanged
        });

        unlockNextLevel(1);
        // Marks Level 1 as completed and unlocks Level 2 if allowed

        saveAndLoadLeaderboard();
        // Saves the player's time and loads the leaderboard
    }

    // Saves the player's score and loads leaderboard data
    private void saveAndLoadLeaderboard() {

        if (timeTaken > 0) {
            // Only save if a valid time exists

            ProgressStorage.saveLevelCompletion(this, 1, timeTaken);
            // Saves the completion time for level 1
        }

        // Loads leaderboard data asynchronously
        ProgressStorage.getLeaderboard(1, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {
                // Called when leaderboard data is successfully loaded

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                // Builds a formatted leaderboard string

                int rank = 1;
                // Ranking counter

                for (Map<String, Object> entry : entries) {
                    // Loop through each leaderboard entry

                    String name = (String) entry.get("userName");
                    // Get the player's name

                    long time = (long) entry.get("timeTakenMillis");
                    // Get the time taken in milliseconds

                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");
                    // Add formatted entry to the leaderboard text

                    rank++;
                    // Move to the next rank
                }

                leaderboardText.setText(sb.toString());
                // Display the leaderboard on screen
            }

            @Override
            public void onError(Exception e) {
                // Called if loading the leaderboard fails

                leaderboardText.setText("Leaderboard unavailable");
                // Show an error message
            }
        });
    }

    // Unlocks the next level if the current level is the highest unlocked
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            // Check if the player finished the highest unlocked level

            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
            // Unlock the next level
        }
    }

    // Finds UI elements and sets click listeners
    private void initViews() {

        BtClick10 = findViewById(R.id.BtClick10);
        // Connects the button from XML to Java

        BtClick10.setOnClickListener(this);
        // Sets this activity to handle button clicks

        leaderboardText = findViewById(R.id.leaderboardText);
        // Connects the leaderboard TextView from XML
    }

    // Runs when the button is clicked
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, SecondQuestion.class);
        // Creates an intent to move to the second question screen

        startActivity(intent);
        // Starts the next activity
    }
}
