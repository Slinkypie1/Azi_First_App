package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Intent; // Used to navigate between activities
import android.os.Bundle;      // Stores activity state information
import android.view.Menu;      // Represents the options menu
import android.view.MenuInflater; // Converts XML menu into Java objects
import android.view.MenuItem;  // Represents a single item in the menu
import android.view.View;      // Base class for UI components
import android.view.ViewGroup; // Container for multiple views
import android.widget.RadioButton; // Radio button UI element
import android.widget.TextView; // Text display UI element
import android.graphics.Color;  // Used for color values
import android.content.SharedPreferences; // Stores simple key-value data
import android.widget.FrameLayout; // Layout container

import androidx.activity.OnBackPressedCallback; // Handles back button behavior
import androidx.annotation.Nullable; // Allows null-safe annotations
import androidx.appcompat.app.AppCompatActivity; // Base activity with ActionBar support

import java.util.concurrent.TimeUnit; // Used for time-based operations

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

// Abstract base class shared by all menu-based activities
public abstract class BaseMenuActivity extends AppCompatActivity {

    // Called when activity is first created
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls parent setup logic

        // Hide the default title bar text
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Override back button behavior
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog(); // Show confirmation instead of exiting immediately
            }
        });
    }

    /**
     * Shows confirmation dialog before exiting screen
     */
    private void showExitConfirmationDialog() {
        String message;

        // Customize message depending on current activity
        if (this instanceof MainActivity) {
            message = "Are you sure you want to exit the app?";
        } else if (this instanceof Second) {
            message = "Are you sure you want to go back to the login screen?";
        } else if (this instanceof GameSettings || this instanceof AppearanceSettings) {
            message = "Are you sure you want to exit the settings? Your changes may not change";
        } else {
            message = "Are you sure you want to quit this level? Your current progress will be lost.";
        }

        // Build and show alert dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage(message)

                // YES button behavior
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (this instanceof MainActivity) {
                        finishAffinity(); // Close entire app
                    } else if (this instanceof Second) {
                        startActivity(new Intent(this, MainActivity.class)); // Go to main screen
                        finish();
                    } else if (this instanceof GameSettings || this instanceof AppearanceSettings) {
                        finish(); // Just go back
                    } else {
                        startActivity(new Intent(this, Second.class)); // Return to hub
                        finish();
                    }
                })

                // NO button does nothing (dialog closes)
                .setNegativeButton("No", null)
                .show();
    }

    // Creates top-right options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // Get menu inflater
        inflater.inflate(R.menu.level_select, menu); // Load XML menu layout
        return true; // Show menu
    }

    // Called every time menu is shown (refresh state)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Get music toggle button
        MenuItem musicItem = menu.findItem(R.id.music_toggle);

        if (musicItem != null) {
            // Check saved mute state
            boolean isMuted = ProgressStorage.getAppPrefs(this)
                    .getBoolean("music_muted", false);

            // Update icon depending on state
            musicItem.setIcon(isMuted ? R.drawable.music_off : R.drawable.music_on);
        }

        // Hide menu entirely on login screen
        if (this instanceof MainActivity) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
            return true;
        }

        // Get highest unlocked level
        int highest = ProgressStorage.getHighestUnlockedLevel(this);

        SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
        String mode = prefs.getString("game_mode", "casual");

        boolean isTimedMode = mode.equals("timed"); // Check if timed mode
        boolean isSecondActivity = this instanceof Second; // Check hub screen
        boolean showLevels = isSecondActivity && !isTimedMode; // Only show levels if allowed

        // Loop through all levels
        for (int level = 1; level <= 9; level++) {

            // Convert level number into menu ID (level_1, level_2, etc.)
            int resId = getResources().getIdentifier(
                    "level_" + level,
                    "id",
                    getPackageName()
            );

            MenuItem item = menu.findItem(resId); // Get menu item

            if (item != null) {

                // Hide levels if not allowed
                if (!showLevels) {
                    item.setVisible(false);
                    continue;
                }

                // Unlock logic
                if (level <= highest) {
                    item.setEnabled(true); // clickable
                    item.setIcon(R.drawable.ic_unlock); // unlocked icon
                } else {
                    item.setEnabled(false); // locked
                    item.setIcon(R.drawable.ic_lock); // lock icon
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // Start a level (placeholder function)
    public void startLevel(int level) {
        Intent intent = new Intent(this, Third.class); // Example level screen
        startActivity(intent);
    }

    // Handle menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId(); // Get clicked item ID

        // Main menu
        if (id == R.id.main_menu) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        // Music toggle
        if (id == R.id.music_toggle) {

            SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
            boolean isMuted = prefs.getBoolean("music_muted", false);
            boolean newMuted = !isMuted;

            prefs.edit().putBoolean("music_muted", newMuted).apply(); // Save state

            ProgressStorage.syncMusicToFirebase(this, newMuted); // Sync cloud

            Intent serviceIntent = new Intent(this, MusicService.class);

            if (newMuted) {
                stopService(serviceIntent); // stop music
            } else {
                startService(serviceIntent); // start music
            }

            invalidateOptionsMenu(); // refresh icon
            return true;
        }

        // Level navigation
        if (id == R.id.level_1) { startActivity(new Intent(this, Third.class)); return true; }
        if (id == R.id.level_2) { startActivity(new Intent(this, SecondQuestion.class)); return true; }
        if (id == R.id.level_3) { startActivity(new Intent(this, ThirdQuestion.class)); return true; }
        if (id == R.id.level_4) { startActivity(new Intent(this, Puzzle1.class)); return true; }
        if (id == R.id.level_5) { startActivity(new Intent(this, Puzzle2.class)); return true; }
        if (id == R.id.level_6) { startActivity(new Intent(this, Puzzle3.class)); return true; }
        if (id == R.id.level_7) { startActivity(new Intent(this, FillTheBlanks.class)); return true; }
        if (id == R.id.level_8) { startActivity(new Intent(this, FindTheCountry.class)); return true; }
        if (id == R.id.level_9) { startActivity(new Intent(this, UnlockCityActivity.class)); return true; }

        return super.onOptionsItemSelected(item);
    }

    // When activity returns to foreground
    @Override
    protected void onResume() {
        super.onResume(); // resume lifecycle

        invalidateOptionsMenu(); // refresh menu state
        applyAppearance(); // apply theme/colors

        // Auto confetti on success screens
        if (this.getClass().getSimpleName().contains("Correct") ||
                this.getClass().getSimpleName().contains("Finish") ||
                this instanceof FinalScore) {
            triggerConfetti();
        }
    }

    // Shows confetti animation
    public void triggerConfetti() {

        View rootView = findViewById(android.R.id.content);
        if (!(rootView instanceof ViewGroup)) return;

        ViewGroup rootGroup = (ViewGroup) rootView;

        KonfettiView konfettiView = null;

        // Find existing confetti view
        for (int i = 0; i < rootGroup.getChildCount(); i++) {
            if (rootGroup.getChildAt(i) instanceof KonfettiView) {
                konfettiView = (KonfettiView) rootGroup.getChildAt(i);
                break;
            }
        }

        // Create if missing
        if (konfettiView == null) {
            konfettiView = new KonfettiView(this);
            konfettiView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootGroup.addView(konfettiView);
        }

        // Confetti settings
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(30);

        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .shapes(new Shape.Rectangle(0.2f), Shape.Circle.INSTANCE)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0)
                .build();

        konfettiView.start(party);
    }

    // Applies theme colors
    private void applyAppearance() {

        SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
        String bgColor = prefs.getString("bg_color", "white");

        int backgroundColor = bgColor.equals("black") ? Color.BLACK : Color.WHITE;
        int textColor = bgColor.equals("black") ? Color.WHITE : Color.BLACK;

        getWindow().getDecorView().setBackgroundColor(backgroundColor);

        View rootView = findViewById(android.R.id.content);

        if (rootView != null) {
            applyColorsRecursively(rootView, backgroundColor, textColor, true);
        }
    }

    // Recursively applies colors to all views
    private void applyColorsRecursively(View view, int bgColor, int textColor, boolean isRoot) {

        if (isRoot) {
            view.setBackgroundColor(bgColor);
        } else if (view instanceof ViewGroup && !(view instanceof android.widget.AdapterView)) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        if (view instanceof TextView) {
            TextView tv = (TextView) view;

            if (!(view instanceof android.widget.Button) || bgColor == Color.BLACK) {
                tv.setTextColor(textColor);
            }

            if (view instanceof RadioButton) {
                ((RadioButton) view).setButtonTintList(
                        android.content.res.ColorStateList.valueOf(textColor));
            }
        }

        if (view instanceof android.widget.ProgressBar) {
            ((android.widget.ProgressBar) view)
                    .setIndeterminateTintList(
                            android.content.res.ColorStateList.valueOf(textColor));
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                applyColorsRecursively(group.getChildAt(i), bgColor, textColor, false);
            }
        }
    }
}