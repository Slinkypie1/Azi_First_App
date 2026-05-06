package com.example.asfirstapp; // Package name for this activity

import android.content.Intent; // Used to navigate between screens
import android.os.Bundle; // Contains saved state data for activity lifecycle
import android.view.View; // Base class for all UI elements
import android.widget.Button; // Button UI element
import android.widget.TextView; // Displays text on screen
import android.widget.Toast; // Shows small popup messages (not used here)

import androidx.activity.EdgeToEdge; // Enables edge-to-edge UI layout
import androidx.core.view.ViewCompat; // Helps handle window insets safely

import java.util.List; // List data structure for leaderboard entries
import java.util.Map; // Key-value pairs for leaderboard data

// Screen shown when the player answers Level 1 correctly
public class CorrectScreen1 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick10; // Button that continues to next question
    TextView leaderboardText; // Displays leaderboard results
    long timeTaken; // Stores how long the player took to finish level

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call parent setup
        EdgeToEdge.enable(this); // Enable fullscreen edge-to-edge UI

        setContentView(R.layout.activity_correct_screen1); // Load layout XML

        // Start background music for correct answer screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // Get time taken from previous screen (default = 0 if missing)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // Apply safe window insets and initialize UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // Setup buttons and text views
            unlockNextLevel(1); // Unlock next level if needed
            saveAndLoadLeaderboard(); // Save score + load leaderboard

            return insets; // Return unchanged insets
        });
    }

    // Saves score and loads leaderboard from storage/cloud
    private void saveAndLoadLeaderboard() {

        // Get current game mode (casual or timed)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // Save level completion time (also triggers achievements logic)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 1, timeTaken);
        }

        // If casual mode → hide leaderboard entirely
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // Load leaderboard data asynchronously from storage/backend
        ProgressStorage.getLeaderboard(this, 1, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                int rank = 1; // ranking counter

                // Loop through leaderboard entries
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName"); // player name
                    long time = (long) entry.get("timeTakenMillis"); // completion time

                    // Format and append entry
                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");

                    rank++; // increase rank
                }

                leaderboardText.setText(sb.toString()); // show leaderboard
            }

            @Override
            public void onError(Exception e) {
                // If leaderboard fails to load
                leaderboardText.setText("Leaderboard unavailable");
            }
        });
    }

    // Unlocks next level if player completed current highest level
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Finds UI elements and attaches listeners
    private void initViews() {

        BtClick10 = findViewById(R.id.BtClick10); // connect button from XML
        BtClick10.setOnClickListener(this); // set click listener

        leaderboardText = findViewById(R.id.leaderboardText); // connect text view
    }

    // Handles button click events
    @Override
    public void onClick(View view) {

        // Move to next question screen
        Intent intent = new Intent(this, SecondQuestion.class);

        startActivity(intent); // open next activity
    }
}