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

// Screen shown when the user completes Level 9 successfully
public class CorrectScreen9 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick22; // Button that proceeds after finishing Level 9
    TextView leaderboardText; // Displays leaderboard results
    long timeTaken; // Stores completion time for Level 9

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call base setup logic
        EdgeToEdge.enable(this); // Enable full-screen edge-to-edge UI

        setContentView(R.layout.activity_correct_screen9); // Load XML layout

        // Start background music for correct screens
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // Get completion time from previous screen (default = 0)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // Apply system insets and initialize UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // Initialize buttons and text views

            unlockNextLevel(9); // Unlock next level if applicable

            saveAndLoadLeaderboard(); // Save progress + load leaderboard

            return insets; // Return unchanged layout insets
        });
    }

    // Saves completion time and loads leaderboard for Level 9
    private void saveAndLoadLeaderboard() {

        // Get current game mode (casual or timed)
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // Save completion time (used for progress + achievements)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 9, timeTaken);
        }

        // Hide leaderboard in casual mode
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // Fetch leaderboard data for Level 9
        ProgressStorage.getLeaderboard(this, 9, new ProgressStorage.LeaderboardCallback() {

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

                leaderboardText.setText(sb.toString()); // display leaderboard
            }

            @Override
            public void onError(Exception e) {
                // If leaderboard fails
                leaderboardText.setText("Leaderboard unavailable");
            }
        });
    }

    // Initializes UI components
    private void initViews() {

        BtClick22 = findViewById(R.id.BtClick22); // connect button from XML
        BtClick22.setOnClickListener(this); // set click listener

        leaderboardText = findViewById(R.id.leaderboardText); // connect text view
    }

    // Unlocks next level if this is the highest unlocked level
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // Handles button click (final navigation logic)
    @Override
    public void onClick(View view) {

        // Check game mode
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        Intent intent;

        // If timed mode → show final score screen
        if (mode.equals("timed")) {
            intent = new Intent(this, FinalScore.class);
        }
        // If casual mode → show casual finish screen
        else {
            intent = new Intent(this, CasualFinish.class);
        }

        startActivity(intent); // start next screen
        finish(); // close current activity
    }
}