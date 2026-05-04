package com.example.asfirstapp; // Package namespace for the app

import android.content.Intent; // Used to navigate between activities
import android.os.Bundle; // Stores activity state information
import android.view.View; // Base class for UI components
import android.widget.Button; // Button UI element

import androidx.activity.EdgeToEdge; // Enables edge-to-edge fullscreen layout
import androidx.appcompat.app.AppCompatActivity; // Base activity class (not directly used here since BaseMenuActivity extends it)
import androidx.core.graphics.Insets; // Represents system bar insets
import androidx.core.view.ViewCompat; // Handles compatibility for view changes
import androidx.core.view.WindowInsetsCompat; // Provides window inset data

// Activity shown when the player fails a level
public class Failure extends BaseMenuActivity implements View.OnClickListener {

    Button BtClickLose; // Button that returns player to main menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // Call parent setup logic

        EdgeToEdge.enable(this); // Enable full-screen edge-to-edge layout

        setContentView(R.layout.activity_failure_screen); // Load failure screen layout

        // Record that the player failed (used for achievements like "Perfectionist")
        ProgressStorage.recordWallHit();

        // Start failure background music
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.failure_music);
        startService(serviceIntent);

        // Handle system UI insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {

            initViews(); // Initialize UI components

            return insets; // Keep system insets unchanged
        });
    }

    // Initializes UI components and sets click listeners
    private void initViews() {

        BtClickLose = findViewById(R.id.BtClickLose); // Find button in layout
        BtClickLose.setOnClickListener(this); // Set click handler
    }

    // Handles button click events
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, Second.class);
        // Create intent to return to main hub/menu screen

        startActivity(intent); // Launch main screen
    }
}