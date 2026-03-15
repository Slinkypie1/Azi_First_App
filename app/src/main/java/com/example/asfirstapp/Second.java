package com.example.asfirstapp;

import android.content.Intent; // Used if you want to navigate to another activity
import android.os.Bundle;     // Holds activity state information
import android.view.View;     // Base class for all UI widgets
import android.widget.Button; // Button UI element
import android.widget.EditText; // Editable text input
import android.widget.TextView; // Text display

import androidx.activity.EdgeToEdge; // Enables edge-to-edge layout support
import androidx.core.view.ViewCompat; // For applying window insets

/**
 * Second Activity
 * ----------------
 * Activity that displays a welcome message to the user,
 * allows them to input text (like a name), and provides a button
 * to start Level 1 of the game.
 */
public class Second extends BaseMenuActivity implements View.OnClickListener {

    private TextView TV;      // Displays welcome message
    private EditText ET;      // Optional input field (e.g., player name)
    private Button BtClick1;  // Button to start level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge layouts
        EdgeToEdge.enable(this);

        // Set the layout for this activity
        setContentView(R.layout.activity_second);

        // Start background music for Second Screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.second_music);
        startService(serviceIntent);

        // Initialize UI components
        initViews();

        // Apply insets if using edge-to-edge (optional here)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> insets);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the welcome message each time the activity resumes
        updateNameDisplay();
    }

    /**
     * Initializes all UI elements and sets click listeners
     */
    private void initViews() {
        // Link views from layout
        TV = findViewById(R.id.TV);
        ET = findViewById(R.id.ET);
        BtClick1 = findViewById(R.id.BtClick1);

        // Display the last saved player name
        updateNameDisplay();

        // Set click listener for the button
        BtClick1.setOnClickListener(this);
    }

    /**
     * Updates the welcome message with the last entered name from SharedPreferences
     */
    private void updateNameDisplay() {
        String lastName = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("last_name", "Player"); // Default to "Player" if none saved

        TV.setText("Ready " + lastName + "?"); // Display welcome message
    }

    @Override
    public void onClick(View view) {
        // Start tracking total game time when starting Level 1
        ProgressStorage.setGameStartTime(this, System.currentTimeMillis());

        // Start Level 1 when button is clicked
        startLevel(1);
    }
}
