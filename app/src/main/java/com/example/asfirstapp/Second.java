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
 * Activity that displays a welcome message to the user,
 * allows them to input text (like a name), and provides a button
 * to start Level 1 of the game.
 */
public class Second extends BaseMenuActivity implements View.OnClickListener {

    private TextView TV;      // Displays welcome message
    private EditText ET;      // Optional input field (e.g., player name)
    private Button BtClick1;  // Button to start level
    private Button BtSettings; // Button to go to settings
    private Button btnSwitchUser; // Button to logout/switch player

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
        BtSettings = findViewById(R.id.BtSettings);
        btnSwitchUser = findViewById(R.id.btnSwitchUser);

        // Display the last saved player name
        updateNameDisplay();

        // Set click listener for the button
        BtClick1.setOnClickListener(this);
        BtSettings.setOnClickListener(this);
        btnSwitchUser.setOnClickListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add Achievements (Trophy Room) item
        MenuItem trophyItem = menu.add(Menu.NONE, 1002, 0, "Achievements");
        trophyItem.setIcon(R.drawable.achievements);
        trophyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Add the Settings item specifically for this activity
        MenuItem settingsItem = menu.add(Menu.NONE, 1001, 1, "Settings");
        settingsItem.setIcon(R.drawable.light_dark_mode);
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Inflate the base menu (Home, Level Select) from BaseMenuActivity AFTER
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the Settings item click
        if (item.getItemId() == 1001) {
            Intent intent = new Intent(this, AppearanceSettings.class);
            startActivity(intent);
            return true;
        }
        // Handle Achievements (Trophy Room) click
        if (item.getItemId() == 1002) {
            Intent intent = new Intent(this, TrophyRoom.class);
            startActivity(intent);
            return true;
        }
        // Handle other menu items via BaseMenuActivity
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.BtClick1) {
            // Start tracking total game time when starting Level 1
            ProgressStorage.setGameStartTime(this, System.currentTimeMillis());

            // Reset perfectionist flag for new run
            ProgressStorage.resetPerfectionistFlag();

            // Start Level 1 when button is clicked
            startLevel(1);
        } else if (view.getId() == R.id.BtSettings) {
            Intent intent = new Intent(this, GameSettings.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btnSwitchUser) {
            handleLogout();
        }
    }

    /**
     * Clears user preferences and returns to the login screen.
     */
    private void handleLogout() {
        // Clear the saved name and email
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .remove("last_name")
                .remove("last_email")
                .apply();

        // Reset highest level for the next user (UI only, cloud is safe)
        ProgressStorage.setHighestUnlockedLevelOffline(this, 1);

        // Go back to Login
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
