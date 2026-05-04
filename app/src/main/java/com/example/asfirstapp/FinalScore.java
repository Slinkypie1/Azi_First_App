package com.example.asfirstapp; // Package name for this class

import android.content.Intent; // Used to navigate between activities
import android.os.Bundle; // Holds activity state data
import android.view.View; // Base class for UI elements
import android.widget.Button; // Button UI component
import android.widget.TextView; // Displays text on screen

import androidx.activity.EdgeToEdge; // Enables edge-to-edge fullscreen layout
import androidx.appcompat.app.AppCompatActivity; // Base activity class (not strictly needed here since BaseMenuActivity extends it)
import androidx.core.graphics.Insets; // Represents system bar insets
import androidx.core.view.ViewCompat; // Handles backward-compatible view adjustments
import androidx.core.view.WindowInsetsCompat; // Provides window inset information

import java.util.Locale; // Used for formatting time strings

// Screen that shows final total time after completing the game
public class FinalScore extends BaseMenuActivity {

    private TextView tvTotalTime; // Displays total game time
    private Button btnBackToMenu; // Button to return to main menu
    private Button btnViewRanking; // Button to view leaderboard/ranking

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call parent setup
        EdgeToEdge.enable(this); // Enable fullscreen edge-to-edge layout

        setContentView(R.layout.activity_final_score); // Load UI layout

        // Start background music for final score screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.final_score_music);
        startService(serviceIntent);

        // Apply system bar padding (status/navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        // Connect UI elements
        tvTotalTime = findViewById(R.id.tvTotalTime);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnViewRanking = findViewById(R.id.btnViewRanking);

        displayAndSaveFinalTime(); // Calculate and show final time

        // Back to menu button logic
        btnBackToMenu.setOnClickListener(v -> {

            // Reset progress (locks all levels again)
            ProgressStorage.setHighestUnlockedLevel(FinalScore.this, 1);

            // Go back to main menu
            Intent intent = new Intent(FinalScore.this, MainActivity.class);

            // Clear back stack so user cannot return here
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });

        // Ranking button (only if exists in layout)
        if (btnViewRanking != null) {
            btnViewRanking.setOnClickListener(v -> {

                Intent intent = new Intent(FinalScore.this, Ranking.class);
                startActivity(intent);
            });
        }
    }

    // Calculates total play time, formats it, saves it, and displays it
    private void displayAndSaveFinalTime() {

        long startTime = ProgressStorage.getGameStartTime(this); // game start timestamp
        long pausedTime = ProgressStorage.getTotalPausedTime(this); // time spent paused

        // If no start time exists, show fallback
        if (startTime == 0) {
            tvTotalTime.setText("Total Time: N/A");
            return;
        }

        long endTime = System.currentTimeMillis(); // current time
        long rawDurationMillis = endTime - startTime; // total elapsed time

        // Remove paused/instruction time from total
        long finalDurationMillis = rawDurationMillis - pausedTime;

        // Prevent negative time values
        if (finalDurationMillis < 0) finalDurationMillis = 0;

        // Save final completion time (Firebase or storage)
        ProgressStorage.saveGameCompletion(this, finalDurationMillis);

        // Award "Ranked" achievement (timed completion)
        ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_RANKED);

        // Award "Perfectionist" achievement if no mistakes were made
        if (!ProgressStorage.wasWallHit()) {
            ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_PERFECTIONIST);
        }

        // Convert milliseconds into hours, minutes, seconds
        long seconds = (finalDurationMillis / 1000) % 60;
        long minutes = (finalDurationMillis / (1000 * 60)) % 60;
        long hours = (finalDurationMillis / (1000 * 60 * 60));

        String timeFormatted;

        // Format time depending on whether hours exist
        if (hours > 0) {
            timeFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        // Display final formatted time
        tvTotalTime.setText("Total Time: " + timeFormatted);
    }
}