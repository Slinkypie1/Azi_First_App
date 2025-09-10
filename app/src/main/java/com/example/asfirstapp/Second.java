package com.example.asfirstapp;
// Defines the package name of the app.

import android.annotation.SuppressLint;     // Used to suppress lint warnings (not directly used here).
import android.content.Intent;              // Allows starting new activities or services.
import android.os.Bundle;                   // Holds saved state when activity is recreated.
import android.view.Menu;                   // Represents the options menu.
import android.view.MenuInflater;           // Converts a menu XML file into actual menu items.
import android.view.MenuItem;               // Represents a single item in the menu.
import android.view.View;                   // Basic building block for UI components.
import android.widget.Button;               // Represents a button in the UI.
import android.widget.EditText;             // Represents an editable text field.
import android.widget.TextView;             // Represents a text display element.

import androidx.activity.EdgeToEdge;        // Provides modern fullscreen (edge-to-edge) layout support.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities with support features.
import androidx.core.graphics.Insets;       // Represents padding/margins from system UI.
import androidx.core.view.ViewCompat;       // Helper for applying UI changes across different Android versions.
import androidx.core.view.WindowInsetsCompat; // Provides info about system UI insets.

public class Second extends AppCompatActivity implements View.OnClickListener {
    // Second activity (screen) of the app.
    // Implements OnClickListener to handle button clicks.

    TextView TV;     // A TextView from the layout.
    EditText ET;     // An EditText input field.
    Button BtCLick1; // A Button in the layout.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent method to set up the activity.

        EdgeToEdge.enable(this);
        // Enables edge-to-edge display (content goes under system bars).

        setContentView(R.layout.activity_second);
        // Sets the screen layout to activity_second.xml.

        // Listens for window insets (status bar, navigation bar).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            initViews();  // Initialize views (TextView, EditText, Button).
            return insets; // Return insets unchanged.
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        // Creates a MenuInflater object.

        inflater.inflate(R.menu.level_select, menu);
        // Loads (inflates) the menu items from res/menu/level_select.xml.

        return true;
        // Return true to display the menu.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handles what happens when a menu item is clicked.

        switch (item.getItemId()){ // Get the ID of the clicked item.

            case R.id.level_1:
                Intent levelOne = new Intent(this, Third.class);
                startActivity(levelOne); // Open Third activity.
                return true;

            case R.id.level_2:
                Intent levelTwo = new Intent(this, SecondQuestion.class);
                startActivity(levelTwo); // Open SecondQuestion activity.
                return true;

            case R.id.level_3:
                Intent levelThree = new Intent(this, ThirdQuestion.class);
                startActivity(levelThree); // Open ThirdQuestion activity.
                return true;

            case R.id.level_4:
                Intent levelFour = new Intent(this, Puzzle1.class);
                startActivity(levelFour); // Open Puzzle1 activity.
                return true;

            case R.id.level_5:
                Intent levelFive = new Intent(this, Puzzle2.class);
                startActivity(levelFive); // Open Puzzle2 activity.
                return true;

            case R.id.level_6:
                Intent levelSix = new Intent(this, Puzzle3.class);
                startActivity(levelSix); // Open Puzzle3 activity.
                return true;

            case R.id.level_7:
                Intent levelSeven = new Intent(this, FillTheBlanks.class);
                startActivity(levelSeven); // Open FillTheBlanks activity.
                return true;

            case R.id.level_8:
                Intent levelEight = new Intent(this, FindTheCountry.class);
                startActivity(levelEight); // Open FindTheCountry activity.
                return true;

            case R.id.level_9:
                Intent levelNine = new Intent(this, UnlockCityActivity.class);
                startActivity(levelNine); // Open UnlockCityActivity activity.
                return true;

            default:
                return super.onOptionsItemSelected(item);
            // If none matched, use default handling.
        }
    }

    private void initViews() {
        TV = findViewById(R.id.TV);   // Find TextView in layout by its ID.
        ET = findViewById(R.id.ET);   // Find EditText in layout by its ID.

        TV.setText("Ready " + getIntent().getStringExtra("name")+"?");
        // Set TextView to "Ready <name>?" using the value passed from MainActivity.

        BtCLick1 = findViewById(R.id.BtClick1); // Find button in layout.
        BtCLick1.setOnClickListener(this);      // Set this activity as button click listener.
    }

    @Override
    public void onClick(View view){
        // Called when BtCLick1 is clicked.

        Intent intent = new Intent(this, Third.class);
        startActivity(intent); // Start Third activity.
    }
}
