package com.example.asfirstapp; // Package name for this class

import android.content.Intent; // Used to start another activity (screen)
import android.os.Bundle; // Contains saved state data for activity lifecycle
import android.view.View; // Base class for UI elements
import android.widget.Button; // Button UI component
import android.widget.TextView; // Text display UI component

import androidx.activity.EdgeToEdge; // Enables edge-to-edge fullscreen layout
import androidx.core.view.ViewCompat; // Handles window insets compatibility

import java.util.List; // List structure for leaderboard entries
import java.util.Map; // Key-value structure for leaderboard data

// Screen shown when the user correctly completes Level 2
public class CorrectScreen2 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick15; // Button to continue to next level
    TextView leaderboardText; // Displays leaderboard results
    long timeTaken; // Stores completion time for Level 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call parent setup logic
        EdgeToEdge.enable(this); // Enable edge-to-edge fullscreen UI

        setContentView(R.layout.activity_correct_screen2); // Load layout XML

        // Start background music for correct answer screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // Get time taken from previous activity (default = 0 if missing)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // Apply safe window insets + initialize UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // Setup UI elements
            unlockNextLevel(2); // Unlock next level if needed
            saveAndLoadLeaderboard(); // Save progress + load leaderboard

            return insets; // Return unchanged insets
        });
    }

    // Saves level completion and loads leaderboard data
    private void saveAndLoadLeaderboard() {

        // Get current game mode (casual or timed)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // Save completion time (also used for achievements tracking)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 2, timeTaken);
        }

        // Hide leaderboard in casual mode
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // Fetch leaderboard data for level 2
        ProgressStorage.getLeaderboard(this, 2, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                int rank = 1; // ranking counter

                // Loop through leaderboard entries
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName"); // player name
                    long time = (long) entry.get("timeTakenMillis"); // time in ms

                    // Format leaderboard line
                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");

                    rank++; // next rank
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

    // Unlocks next level if current level is highest unlocked
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Initializes UI components
    private void initViews() {

        BtClick15 = findViewById(R.id.BtClick15); // connect button from XML
        BtClick15.setOnClickListener(this); // set click listener

        leaderboardText = findViewById(R.id.leaderboardText); // connect text view
    }

    // Handles button click events
    @Override
    public void onClick(View view) {

        // Move to Level 3 question screen
        Intent intent = new Intent(this, ThirdQuestion.class);

        startActivity(intent); // open next activity
    }
}