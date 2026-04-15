package com.example.asfirstapp;
// Defines the package namespace of the app.

import android.content.Intent;    // Used to start activities.
import android.os.Bundle;         // Stores state info for activity recreation.
import android.view.View;         // Base class for UI widgets.
import android.widget.Button;     // Represents a clickable button in the UI.

import androidx.activity.EdgeToEdge;       // Enables fullscreen edge-to-edge layouts.
import androidx.appcompat.app.AppCompatActivity; // Base class for modern activities.
import androidx.core.graphics.Insets;      // Represents system bar insets (status/nav bar).
import androidx.core.view.ViewCompat;      // Helps apply UI changes across Android versions.
import androidx.core.view.WindowInsetsCompat; // Provides info about window insets.

// Activity shown when the player answers incorrectly
public class Failure extends BaseMenuActivity implements View.OnClickListener {
    // Implements OnClickListener so it can handle button clicks.

    Button BtClickLose; // Button to return to the main menu.

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent onCreate to set up the activity.

        EdgeToEdge.enable(this);
        // Enables modern fullscreen (edge-to-edge) layout.

        setContentView(R.layout.activity_failure_screen);
        // Sets this activity’s layout to activity_failure_screen.xml.

        // Start background music for Failure Screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.failure_music);
        startService(serviceIntent);

        // Handle system window insets (status/nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            initViews();  // Initialize button view
            return insets; // Return insets unchanged
        });
    }

    // Initialize views and set click listeners
    private void initViews() {
        BtClickLose = findViewById(R.id.BtClickLose); // Find the "Try Again" button by ID
        BtClickLose.setOnClickListener(this);         // Set this activity as its click handler
    }

    // Called when the "Lose" button is clicked
    @Override
    public void onClick(View view) {
        Intent intent  = new Intent(this, Second.class);
        // Create intent to restart the game by opening MainActivity

        startActivity(intent);
        // Launch MainActivity (back to the beginning)
    }
}
