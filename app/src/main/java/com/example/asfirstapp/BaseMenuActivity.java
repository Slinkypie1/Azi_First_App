package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Load the menu XML (level_select.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.level_select, menu);
        return true;
    }

    // Update the menu each time it appears (icons + lock state)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int highest = ProgressStorage.getHighestUnlockedLevel(this);

        // Loop through all levels 1–9
        for (int level = 1; level <= 9; level++) {

            int resId = getResources().getIdentifier(
                    "level_" + level,
                    "id",
                    getPackageName()
            );

            MenuItem item = menu.findItem(resId);

            if (item != null) {

                // If level unlocked → enable + unlock icon
                if (level <= highest) {
                    item.setEnabled(true);
                    item.setIcon(R.drawable.ic_unlock);
                }
                // If level locked → disable + lock icon
                else {
                    item.setEnabled(false);
                    item.setIcon(R.drawable.ic_lock);
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }
    public void startLevel(int level) {
        // Example: start Level1 activity
        // You can adjust to different levels dynamically if you have more activities
        Intent intent = new Intent(this, Third.class);
        startActivity(intent);
    }


    // Handle click events for all menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Main menu button
        if (id == R.id.main_menu) {
            Intent i = new Intent(this, Second.class);
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

        return super.onOptionsItemSelected(item);
    }

    // Make sure menu updates when the user returns
    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu(); // Refresh icons + locked state
    }
}
