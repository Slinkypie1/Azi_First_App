package com.example.asfirstapp; // Package name for this activity

import android.content.Intent; // Used to navigate between activities
import android.os.Bundle; // Holds saved instance state
import android.view.View; // Base class for UI elements
import android.widget.Button; // Button UI element

import androidx.activity.EdgeToEdge; // Enables edge-to-edge UI layout
import androidx.core.view.ViewCompat; // Used for window inset handling

// This screen is shown when the user finishes Casual mode
public class CasualFinish extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call parent setup logic

        EdgeToEdge.enable(this); // Enable edge-to-edge fullscreen layout

        setContentView(R.layout.activity_casual_finish); // Load layout XML

        // Check achievement: if player never hit a wall
        if (!ProgressStorage.wasWallHit()) {
            ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_PERFECTIONIST);
        }

        // Apply window inset handling (safe layout padding for system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // Initialize buttons and UI interactions

            return insets; // Return unchanged insets
        });
    }

    // Initializes all buttons and click behavior
    private void initViews() {

        // Find UI buttons from layout
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        Button btnTryTimedMode = findViewById(R.id.btnTryTimedMode);

        // Back to menu button click handler
        btnBackToMenu.setOnClickListener(v -> {

            // Create intent to go back to hub screen
            Intent intent = new Intent(CasualFinish.this, Second.class);

            startActivity(intent); // Launch activity
            finish(); // Close current screen
        });

        // Try timed mode button click handler
        btnTryTimedMode.setOnClickListener(v -> {

            // Save game mode as "timed" in local storage
            ProgressStorage.getAppPrefs(this)
                    .edit()
                    .putString("game_mode", "timed")
                    .apply();

            // Sync updated mode to Firebase cloud storage
            ProgressStorage.syncGameModeToFirebase(this, "timed");

            // Return to main hub screen
            Intent intent = new Intent(CasualFinish.this, Second.class);

            startActivity(intent); // Open hub
            finish(); // Close finish screen
        });
    }
}