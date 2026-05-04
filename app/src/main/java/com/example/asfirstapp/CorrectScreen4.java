package com.example.asfirstapp; // Package where this class belongs

import android.content.Intent; // Used to navigate between activities (screens)
import android.os.Bundle; // Holds saved state data for activity lifecycle
import android.view.View; // Base class for all UI components
import android.widget.Button; // Button UI component
import android.widget.TextView; // Displays text on screen

import androidx.activity.EdgeToEdge; // Enables edge-to-edge fullscreen layout
import androidx.core.view.ViewCompat; // Provides backward-compatible view utilities

import java.util.List; // List structure for leaderboard entries
import java.util.Map; // Key-value structure for leaderboard data

// Screen shown when the user completes Level 4 correctly
public class CorrectScreen4 extends BaseMenuActivity implements View.OnClickListener {

    Button BtCLick17; // Button that continues to Level 5
    TextView leaderboardText; // Displays leaderboard results
    long timeTaken; // Stores completion time for Level 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call parent setup logic
        EdgeToEdge.enable(this); // Enable edge-to-edge fullscreen UI

        setContentView(R.layout.activity_correct_screen4); // Load layout XML

        // Start background music for correct answer screens
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // Get time taken from previous activity (default = 0 if missing)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // Apply window insets and initialize UI components
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // Setup buttons and text views
            unlockNextLevel(4); // Unlock next level if needed
            saveAndLoadLeaderboard(); // Save progress and load leaderboard

            return insets; // Return unchanged insets
        });
    }

    // Saves completion time and loads leaderboard for Level 4
    private void saveAndLoadLeaderboard() {

        // Get current game mode (casual or timed)
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // Save level completion time (also used for achievements)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 4, timeTaken);
        }

        // Hide leaderboard in casual mode
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // Fetch leaderboard data for Level 4
        ProgressStorage.getLeaderboard(this, 4, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n");
                int rank = 1; // ranking counter

                // Loop through leaderboard entries
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName"); // player name
                    long time = (long) entry.get("timeTakenMillis"); // completion time

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

    // Unlocks next level if this is the highest unlocked level
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Initializes UI components
    private void initViews() {

        BtCLick17 = findViewById(R.id.BtClick17); // connect button from XML
        BtCLick17.setOnClickListener(this); // set click listener

        leaderboardText = findViewById(R.id.leaderboardText); // connect text view
    }

    // Handles button click events
    @Override
    public void onClick(View view) {

        // Move to Level 5 puzzle screen
        Intent intent = new Intent(this, Puzzle2.class);

        startActivity(intent); // open next activity
    }
}