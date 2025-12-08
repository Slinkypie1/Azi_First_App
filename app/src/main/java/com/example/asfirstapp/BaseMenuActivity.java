package com.example.asfirstapp;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseMenuActivity extends AppCompatActivity {



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.level_select, menu);

        int highest = ProgressStorage.getHighestUnlockedLevel(this);

        for (int i = 1; i <= 9; i++) {
            MenuItem item = menu.findItem(getResources().getIdentifier("level_" + i, "id", getPackageName()));
            if (i > highest) {
                // Locked → show lock icon
                item.setIcon(R.drawable.ic_lock);
            } else {
                // Unlocked → no icon
                item.setIcon(null);
            }
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int highest = ProgressStorage.getHighestUnlockedLevel(this);

        switch (item.getItemId()) {
            case R.id.main_menu:
                startActivity(new Intent(this, Second.class));
                return true;

            case R.id.level_1:
            case R.id.level_2:
            case R.id.level_3:
            case R.id.level_4:
            case R.id.level_5:
            case R.id.level_6:
            case R.id.level_7:
            case R.id.level_8:
            case R.id.level_9:
                int level = Integer.parseInt(item.getTitle().toString().split(" ")[1]);
                if (level <= highest) {
                    startActivity(LevelIntentFactory.getIntent(this, level));
                } else {
                    Toast.makeText(this, "This level is locked!", Toast.LENGTH_SHORT).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void startLevel(int level) {

        int highest = ProgressStorage.getHighestUnlockedLevel(this);
        if (level > highest) return;

        Intent intent = LevelIntentFactory.getIntent(this, level);
        intent.putExtra("LEVEL", level);
        startActivity(intent);
    }
}
