package com.example.asfirstapp;

import android.content.Intent; // Used if you want to navigate to another activity
import android.os.Bundle;     // Holds activity state information
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;     // Base class for all UI widgets
import android.widget.Button; // Button UI element
import android.widget.EditText; // Editable text input
import android.widget.TextView; // Text display

import androidx.activity.EdgeToEdge; // Enables edge-to-edge layout support
import androidx.core.view.ViewCompat; // For applying window insets

/**
 * Second Activity
 * ----------------
 * This is the main hub screen after login.
 * It shows a welcome message, allows the user to start the game,
 * open settings, or switch user accounts.
 */
public class Second extends BaseMenuActivity implements View.OnClickListener {

    // UI elements
    private TextView TV;          // Displays welcome message to the player
    private EditText ET;          // Input field for player name (currently optional)
    private Button BtClick1;      // Button to start Level 1
    private Button BtSettings;    // Button to open settings screen
    private Button btnSwitchUser; // Button to log out and switch user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge layout (content extends behind system bars)
        EdgeToEdge.enable(this);

        // Set the layout XML for this activity
        setContentView(R.layout.activity_second);

        // Start background music for this screen
        Intent serviceIntent = new Intent(this, MusicService.class);

        // Pass which music track should be played
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.second_music);

        // Start the music service
        startService(serviceIntent);

        // Initialize all UI components and listeners
        initViews();

        // Apply window insets (safe area handling for system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> insets);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh welcome text every time the activity comes back into view
        updateNameDisplay();
    }

    /**
     * Initializes UI components and sets click listeners
     */
    private void initViews() {

        // Link XML views to Java variables
        TV = findViewById(R.id.TV);
        ET = findViewById(R.id.ET);
        BtClick1 = findViewById(R.id.BtClick1);
        BtSettings = findViewById(R.id.BtSettings);
        btnSwitchUser = findViewById(R.id.btnSwitchUser);

        // Show stored player name in welcome text
        updateNameDisplay();

        // Set click listeners for all buttons
        BtClick1.setOnClickListener(this);
        BtSettings.setOnClickListener(this);
        btnSwitchUser.setOnClickListener(this);
    }

    /**
     * Updates welcome text using saved player name from SharedPreferences
     */
    private void updateNameDisplay() {

        // Retrieve last saved player name
        String lastName = ProgressStorage.getAppPrefs(this)
                .getString("last_name", "Player"); // Default value if none exists

        // Update TextView with greeting
        TV.setText("Ready " + lastName + "?");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Add Achievements button to top menu
        MenuItem trophyItem = menu.add(Menu.NONE, 1002, 0, "Achievements");
        trophyItem.setIcon(R.drawable.achievements);
        trophyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Add Settings button to top menu
        MenuItem settingsItem = menu.add(Menu.NONE, 1001, 1, "Settings");
        settingsItem.setIcon(R.drawable.light_dark_mode);
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Load base menu items from parent class (Home, Level Select, etc.)
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Open Appearance Settings screen
        if (item.getItemId() == 1001) {
            Intent intent = new Intent(this, AppearanceSettings.class);
            startActivity(intent);
            return true;
        }

        // Open Trophy Room (Achievements screen)
        if (item.getItemId() == 1002) {
            Intent intent = new Intent(this, TrophyRoom.class);
            startActivity(intent);
            return true;
        }

        // Let BaseMenuActivity handle other menu actions
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        // Start game when main button is pressed
        if (view.getId() == R.id.BtClick1) {

            // Record game start time for tracking total playtime
            ProgressStorage.setGameStartTime(this, System.currentTimeMillis());

            // Reset "perfect run" tracking flag
            ProgressStorage.resetPerfectionistFlag();

            // Start Level 1
            startLevel(1);

        }
        // Open game settings screen
        else if (view.getId() == R.id.BtSettings) {
            Intent intent = new Intent(this, GameSettings.class);
            startActivity(intent);

        }
        // Switch user / log out
        else if (view.getId() == R.id.btnSwitchUser) {
            handleLogout();
        }
    }

    private void handleLogout() {

        // Log out from Firebase Authentication
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

        // Remove stored user data from global prefs (optional, keep email for next login)
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .remove("last_name")
                .remove("last_email")
                .apply();

        // Return to login screen and clear activity stack
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}