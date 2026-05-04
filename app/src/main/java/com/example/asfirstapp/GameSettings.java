package com.example.asfirstapp;
// Defines the package this class belongs to

import android.content.SharedPreferences;
// Used to store simple key-value data locally on the device

import android.os.Bundle;
// Contains saved state information for activity lifecycle

import android.widget.Button;
// UI element for clickable buttons

import android.widget.RadioButton;
// UI element for selecting one option in a group

import android.widget.RadioGroup;
// Container that groups radio buttons together

import android.widget.Toast;
// Used to show short popup messages

import androidx.activity.EdgeToEdge;
// Enables edge-to-edge fullscreen layout support

import androidx.appcompat.app.AppCompatActivity;
// Base class for modern Android activities

import androidx.core.graphics.Insets;
// Represents system bar insets (status/nav bar spacing)

import androidx.core.view.ViewCompat;
// Provides compatibility utilities for views

import androidx.core.view.WindowInsetsCompat;
// Handles system window insets for modern UI layouts

public class GameSettings extends BaseMenuActivity {

    private RadioGroup rgGameMode;
    // Group that holds game mode radio buttons

    private RadioButton rbCasual, rbTimed;
    // Radio buttons for selecting casual or timed mode

    private Button btnSaveSettings;
    // Button used to save selected settings

    private SharedPreferences sharedPreferences;
    // Stores persistent user settings locally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent activity setup

        EdgeToEdge.enable(this);
        // Enables fullscreen edge-to-edge layout

        setContentView(R.layout.activity_game_settings);
        // Loads the UI layout for this screen

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Gets system bar spacing (status + navigation bar)

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Applies padding so UI does not overlap system bars

            return insets;
        });

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        // Opens local storage for app preferences

        rgGameMode = findViewById(R.id.rgGameMode);
        // Connects radio group from XML

        rbCasual = findViewById(R.id.rbCasual);
        // Connects casual mode radio button

        rbTimed = findViewById(R.id.rbTimed);
        // Connects timed mode radio button

        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        // Connects save button

        // Load existing setting
        String mode = sharedPreferences.getString("game_mode", "casual");
        // Reads saved game mode (defaults to casual)

        if (mode.equals("timed")) {
            rbTimed.setChecked(true);
            // Select timed mode if previously saved
        } else {
            rbCasual.setChecked(true);
            // Otherwise default to casual mode
        }

        btnSaveSettings.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Creates editor to modify stored preferences

            String chosenMode;
            // Stores selected mode

            if (rbTimed.isChecked()) {
                chosenMode = "timed";
                // User selected timed mode
            } else {
                chosenMode = "casual";
                // Default to casual mode
            }

            editor.putString("game_mode", chosenMode);
            // Saves selected mode locally

            editor.apply();
            // Applies changes asynchronously

            // Sync to Firebase
            ProgressStorage.syncGameModeToFirebase(this, chosenMode);
            // Sends selected mode to cloud storage

            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
            // Shows confirmation message

            finish();
            // Closes settings screen and returns to previous screen
        });
    }
}