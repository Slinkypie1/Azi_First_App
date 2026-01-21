package com.example.asfirstapp; // Defines the package name for this class

import android.content.Intent; // Used to start new activities
import android.os.Bundle;      // Holds activity state data
import android.view.Menu;      // Represents the options menu
import android.view.MenuInflater; // Converts menu XML into menu objects
import android.view.MenuItem;  // Represents a single menu item

import androidx.annotation.Nullable; // Allows null values safely
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with menu support

// Abstract base activity that provides a shared menu system for all levels
public abstract class BaseMenuActivity extends AppCompatActivity {

    // Called when the activity is first created
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls parent activity setup
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

        // Get the highest level the user has unlocked
        int highest = ProgressStorage.getHighestUnlockedLevel(this);

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
            Intent i = new Intent(this, Second.class); // Go to main menu screen
            startActivity(i);
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
    }
}
