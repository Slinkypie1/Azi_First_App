package com.example.asfirstapp; // Defines the app’s package (namespace)

import android.content.Intent; // Used to switch between screens (activities)
import android.content.SharedPreferences; // Used to save small persistent data locally
import android.graphics.Color; // Allows working with colors (not used in this file currently)
import android.os.Bundle; // Used to pass data into onCreate()
import android.view.View; // Base class for all UI elements
import android.widget.Button; // Button UI element
import android.widget.RadioButton; // Single selectable option button
import android.widget.RadioGroup; // Group of radio buttons (only one selectable)
import android.widget.TextView; // Text display element (not used here)
import android.widget.Toast; // Small popup message

import androidx.activity.EdgeToEdge; // Enables edge-to-edge layout rendering
import androidx.core.graphics.Insets; // Handles system window insets (status/navigation bars)
import androidx.core.view.ViewCompat; // Helps apply compatibility features to views
import androidx.core.view.WindowInsetsCompat; // Provides system bar inset information

public class AppearanceSettings extends BaseMenuActivity {
    // This screen inherits from BaseMenuActivity (shared menu functionality)

    private RadioGroup rgBgColor; // Group for background color options
    private RadioButton rbWhite, rbBlack; // Two selectable color options
    private Button btnSaveAppearance; // Button to save selected appearance
    private SharedPreferences sharedPreferences;
    // Storage for saving user preferences locally on device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Called when this screen is created

        super.onCreate(savedInstanceState); // Calls parent setup logic
        EdgeToEdge.enable(this); // Enables full-screen edge-to-edge UI
        setContentView(R.layout.activity_appearance_settings);
        // Links this Java file to its XML layout

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Adjust layout when system bars (status/nav) overlap UI

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Gets size of system bars

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Applies padding so UI doesn't overlap system bars

            return insets; // Returns modified insets
        });

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        // Creates/opens a private storage file named "app_prefs"

        rgBgColor = findViewById(R.id.rgBgColor); // Connects radio group from XML
        rbWhite = findViewById(R.id.rbWhite); // Connects "white" option
        rbBlack = findViewById(R.id.rbBlack); // Connects "black" option
        btnSaveAppearance = findViewById(R.id.btnSaveAppearance); // Save button
        Button btnBackToSecond = findViewById(R.id.btnBackToSecond);
        // Button to go back to previous screen

        // Load existing setting
        String bgColor = sharedPreferences.getString("bg_color", "white");
        // Reads saved background color (default = white)

        if (bgColor.equals("black")) {
            // If saved value is black
            rbBlack.setChecked(true); // Select black option
        } else {
            rbWhite.setChecked(true); // Otherwise select white option
        }

        btnSaveAppearance.setOnClickListener(v -> {
            // Runs when user clicks save button

            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Opens editor to modify stored preferences

            String chosenColor;
            // Variable to store selected color

            if (rbBlack.isChecked()) {
                chosenColor = "black"; // User selected black
            } else {
                chosenColor = "white"; // Otherwise white
            }

            editor.putString("bg_color", chosenColor);
            // Saves selected color under key "bg_color"

            editor.apply();
            // Commits changes asynchronously

            // Sync to Firebase
            ProgressStorage.syncAppearanceToFirebase(this, chosenColor);
            // Sends selected appearance to Firebase database (cloud sync)

            Toast.makeText(this, "Appearance Saved", Toast.LENGTH_SHORT).show();
            // Shows confirmation popup

            recreate();
            // Restarts activity to apply changes immediately
        });

        btnBackToSecond.setOnClickListener(v -> {
            // Runs when back button is clicked

            Intent intent = new Intent(AppearanceSettings.this, Second.class);
            // Creates intent to move to Second screen

            startActivity(intent); // Opens Second activity

            finish(); // Closes current screen so user can’t go back to it
        });
    }
}