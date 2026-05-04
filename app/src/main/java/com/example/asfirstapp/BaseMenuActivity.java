package com.example.asfirstapp; // Defines the package name for this class

import android.content.Intent; // Used to start new activities
import android.os.Bundle;      // Holds activity state data
import android.view.Menu;      // Represents the options menu
import android.view.MenuInflater; // Converts menu XML into menu objects
import android.view.MenuItem;  // Represents a single menu item
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable; // Allows null values safely
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with menu support

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

// Abstract base activity that provides a shared menu system for all levels
public abstract class BaseMenuActivity extends AppCompatActivity {

    // Called when the activity is first created
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls parent activity setup

        // Hide the title from the Action Bar (removes the app name/activity name from the top)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle the back button with a confirmation dialog
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        });
    }

    /**
     * Shows a dialog to confirm if the user wants to leave the current screen.
     */
    private void showExitConfirmationDialog() {
        String message;
        if (this instanceof MainActivity) {
            message = "Are you sure you want to exit the app?";
        } else if (this instanceof Second) {
            message = "Are you sure you want to go back to the login screen?";
        } else if (this instanceof GameSettings || this instanceof AppearanceSettings) {
            message = "Are you sure you want to exit the settings? Your changes may not change";
        } else {
            message = "Are you sure you want to quit this level? Your current progress will be lost.";
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (this instanceof MainActivity) {
                        finishAffinity(); // Close the app entirely
                    } else if (this instanceof Second) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else if (this instanceof GameSettings || this instanceof AppearanceSettings) {
                        finish(); // Just go back to the previous screen (usually the Hub)
                    } else {
                        // Go back to the Hub (Second activity)
                        startActivity(new Intent(this, Second.class));
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Creates the options menu from XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // Gets menu inflater
        inflater.inflate(R.menu.level_select, menu); // Loads level_select.xml into the menu
        return true; // Tells Android to show the menu
    }

    // Called every time the menu is shown (used to update lock/unlock state)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Handle Music Toggle Icon
        MenuItem musicItem = menu.findItem(R.id.music_toggle);
        if (musicItem != null) {
            boolean isMuted = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .getBoolean("music_muted", false);
            musicItem.setIcon(isMuted ? R.drawable.music_off : R.drawable.music_on);
        }

        // Hide the entire menu if we are on the login screen (MainActivity)
        if (this instanceof MainActivity) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
            return true;
        }

        // Get the highest level the user has unlocked
        int highest = ProgressStorage.getHighestUnlockedLevel(this);

        // Check if level selection should be shown
        // 1. Must be on the Second activity
        // 2. Must NOT be in "Timed" mode
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String mode = prefs.getString("game_mode", "casual");
        boolean isTimedMode = mode.equals("timed");
        boolean isSecondActivity = this instanceof Second;
        boolean showLevels = isSecondActivity && !isTimedMode;

        // Loop through levels 1 to 9
        for (int level = 1; level <= 9; level++) {

            // Dynamically find the menu item ID (level_1, level_2, etc.)
            int resId = getResources().getIdentifier(
                    "level_" + level, // Menu item name
                    "id",              // Resource type
                    getPackageName()   // App package name
            );

            // Get the actual menu item
            MenuItem item = menu.findItem(resId);

            // Make sure the item exists
            if (item != null) {
                // Hide the level selection entirely if conditions aren't met
                if (!showLevels) {
                    item.setVisible(false);
                    continue;
                }

                // If the level is unlocked
                if (level <= highest) {
                    item.setEnabled(true);                // Allow clicking
                    item.setIcon(R.drawable.ic_unlock);   // Show unlocked icon
                }
                // If the level is locked
                else {
                    item.setEnabled(false);               // Disable clicking
                    item.setIcon(R.drawable.ic_lock);     // Show locked icon
                }
            }
        }

        return super.onPrepareOptionsMenu(menu); // Finish menu preparation
    }

    // Starts a level based on its number (currently example only)
    public void startLevel(int level) {
        // Creates an intent to start the level activity
        Intent intent = new Intent(this, Third.class);
        startActivity(intent); // Launch the activity
    }

    // Handles clicks on menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId(); // Get the clicked menu item's ID

        // Main menu button
        if (id == R.id.main_menu) {
            Intent i = new Intent(this, MainActivity.class); // Go to main menu screen
            startActivity(i);
            return true;
        }

        // Music Toggle
        if (id == R.id.music_toggle) {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean isMuted = prefs.getBoolean("music_muted", false);
            boolean newMuted = !isMuted;

            // Save locally
            prefs.edit().putBoolean("music_muted", newMuted).apply();

            // Sync to Firebase
            ProgressStorage.syncMusicToFirebase(this, newMuted);

            // Update Music Service
            Intent serviceIntent = new Intent(this, MusicService.class);
            if (newMuted) {
                stopService(serviceIntent);
            } else {
                // Try to restart music. Note: individual activities usually start specific tracks.
                // We'll just call startService which triggers onStartCommand check.
                startService(serviceIntent);
            }

            // Refresh the icon
            invalidateOptionsMenu();
            return true;
        }

        // Level 1
        if (id == R.id.level_1) {
            startActivity(new Intent(this, Third.class));
            return true;
        }

        // Level 2
        if (id == R.id.level_2) {
            startActivity(new Intent(this, SecondQuestion.class));
            return true;
        }

        // Level 3
        if (id == R.id.level_3) {
            startActivity(new Intent(this, ThirdQuestion.class));
            return true;
        }

        // Level 4
        if (id == R.id.level_4) {
            startActivity(new Intent(this, Puzzle1.class));
            return true;
        }

        // Level 5
        if (id == R.id.level_5) {
            startActivity(new Intent(this, Puzzle2.class));
            return true;
        }

        // Level 6
        if (id == R.id.level_6) {
            startActivity(new Intent(this, Puzzle3.class));
            return true;
        }

        // Level 7
        if (id == R.id.level_7) {
            startActivity(new Intent(this, FillTheBlanks.class));
            return true;
        }

        // Level 8
        if (id == R.id.level_8) {
            startActivity(new Intent(this, FindTheCountry.class));
            return true;
        }

        // Level 9
        if (id == R.id.level_9) {
            startActivity(new Intent(this, UnlockCityActivity.class));
            return true;
        }

        // If the menu item wasn't handled here, let the parent handle it
        return super.onOptionsItemSelected(item);
    }

    // Called when the activity comes back into view
    @Override
    protected void onResume() {
        super.onResume();         // Resume normal activity behavior
        invalidateOptionsMenu(); // Forces the menu to refresh lock/unlock states
        applyAppearance();       // Apply background and text color settings

        // If this is a success/correct screen, show confetti automatically
        if (this.getClass().getSimpleName().contains("Correct") || 
            this.getClass().getSimpleName().contains("Finish") ||
            this instanceof FinalScore) {
            triggerConfetti();
        }
    }

    /**
     * Programmatically adds a KonfettiView and triggers a confetti explosion.
     */
    public void triggerConfetti() {
        View rootView = findViewById(android.R.id.content);
        if (!(rootView instanceof ViewGroup)) return;
        ViewGroup rootGroup = (ViewGroup) rootView;

        // Create or find KonfettiView
        KonfettiView konfettiView = null;
        for (int i = 0; i < rootGroup.getChildCount(); i++) {
            if (rootGroup.getChildAt(i) instanceof KonfettiView) {
                konfettiView = (KonfettiView) rootGroup.getChildAt(i);
                break;
            }
        }

        if (konfettiView == null) {
            konfettiView = new KonfettiView(this);
            konfettiView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootGroup.addView(konfettiView);
        }

        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(30);
        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .shapes(new Shape.Rectangle(0.2f), Shape.Circle.INSTANCE)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0) // Top of screen
                .build();

        konfettiView.start(party);
    }

    /**
     * Applies background and text color based on SharedPreferences
     */
    private void applyAppearance() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String bgColor = prefs.getString("bg_color", "white");

        int backgroundColor = bgColor.equals("black") ? Color.BLACK : Color.WHITE;
        int textColor = bgColor.equals("black") ? Color.WHITE : Color.BLACK;

        // Set window background
        getWindow().getDecorView().setBackgroundColor(backgroundColor);

        // Find root view and apply recursively
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            applyColorsRecursively(rootView, backgroundColor, textColor, true);
        }
    }

    private void applyColorsRecursively(View view, int bgColor, int textColor, boolean isRoot) {
        if (isRoot) {
            view.setBackgroundColor(bgColor);
        } else if (view instanceof ViewGroup && !(view instanceof android.widget.AdapterView)) {
            // Make inner layouts transparent so the root background shows through,
            // but avoid AdapterView (ListView/Spinner) as it might break their look.
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            // Only change text color if it's not a Button (buttons have their own style)
            // or if the background is black (to ensure readability)
            if (!(view instanceof android.widget.Button) || bgColor == Color.BLACK) {
                tv.setTextColor(textColor);
            }
            
            if (view instanceof RadioButton) {
                ((RadioButton) view).setButtonTintList(android.content.res.ColorStateList.valueOf(textColor));
            }
        }

        if (view instanceof android.widget.ProgressBar) {
            ((android.widget.ProgressBar) view).setIndeterminateTintList(android.content.res.ColorStateList.valueOf(textColor));
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyColorsRecursively(group.getChildAt(i), bgColor, textColor, false);
            }
        }
    }
}
