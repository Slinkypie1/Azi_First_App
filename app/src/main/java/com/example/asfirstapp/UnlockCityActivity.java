package com.example.asfirstapp;

// Imports for Android UI, maps, location, and navigation
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UnlockCityActivity
 * ------------------
 * Map-based puzzle where the player "unlocks" cities by guessing their location.
 * The player moves the map to the correct position and submits a guess.
 * After a set number of correct guesses, the player advances to the success screen.
 * The player has 3 hearts; losing all results in failure.
 */
public class UnlockCityActivity extends BaseMenuActivity implements OnMapReadyCallback {

    private GoogleMap mMap;           // Google Map instance used for gameplay
    private TextView clueText;        // Displays the clue for the current city
    private TextView heartsText;      // Displays remaining hearts
    private Button submitGuessBtn;    // Button to submit user's map guess

    private List<City> cityList = new ArrayList<>();   // Master list of all available cities
    private List<City> shuffledCities;                // Randomized list used during gameplay
    private int currentIndex = 0;                     // Tracks position in shuffled list
    private City currentCity;                         // The current city the player must guess

    private int correctGuessCount = 0;               // Number of correctly guessed cities
    private int hearts = 3;                          // Player lives
    private static final int TOTAL_CORRECT_TO_FINISH = 6; // Required correct guesses to win
    private static final float ALLOWED_RADIUS_METERS = 500_000; // Acceptable error radius (500 km)

    private long startTime;                           // Tracks level start time
    private long pauseStartTime;                      // Tracks when instruction dialog was shown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load activity layout
        setContentView(R.layout.activity_unlock_city);

        // Start background music for Unlock City level
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.unlock_city_music);
        startService(serviceIntent);

        // Link UI components to layout views
        clueText = findViewById(R.id.clueText);
        heartsText = findViewById(R.id.heartsText);
        submitGuessBtn = findViewById(R.id.submitGuessBtn);
        submitGuessBtn.setEnabled(false); // Disabled until instructions are acknowledged

        // Show instructions before gameplay starts
        showInstructions();

        // Initialize hearts UI display
        updateHeartsUI();

        // Load and prepare city data
        setupCities();
        shuffleCities();

        // Initialize Google Maps fragment asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Triggers onMapReady when loaded
        }

        // Handle guess submission button click
        submitGuessBtn.setOnClickListener(v -> {
            if (mMap == null || currentCity == null) return; // Safety check

            // Use map center as the player's guess location
            LatLng guess = mMap.getCameraPosition().target;

            // Calculate distance between guess and actual city location
            float[] distanceResult = new float[1];
            Location.distanceBetween(
                    guess.latitude, guess.longitude,
                    currentCity.location.latitude, currentCity.location.longitude,
                    distanceResult);

            if (distanceResult[0] <= ALLOWED_RADIUS_METERS) {
                // Correct guess
                correctGuessCount++;
                Toast.makeText(this,
                        "✅ Correct! You unlocked " + currentCity.name + "!",
                        Toast.LENGTH_LONG).show();

                if (correctGuessCount >= TOTAL_CORRECT_TO_FINISH) {
                    // Award achievement for completion
                    ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_WORLD_TRAVELER);

                    // Finish level and go to success screen
                    long timeTaken = System.currentTimeMillis() - startTime;
                    Intent intent = new Intent(this, CorrectScreen9.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    startActivity(intent);
                    finish();
                } else {
                    // Load next city
                    pickRandomCity();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
                }
            } else {
                // Incorrect guess → lose a heart
                hearts--;
                updateHeartsUI();

                if (hearts <= 0) {
                    // Game over condition
                    Toast.makeText(this, "❌ No hearts left!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Failure.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Allow retry
                    Toast.makeText(this, "❌ Incorrect! " + hearts + " hearts left.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Updates heart display UI using emoji representation.
     */
    private void updateHeartsUI() {
        StringBuilder sb = new StringBuilder();

        // Build heart string based on remaining lives
        for (int i = 0; i < hearts; i++) {
            sb.append("❤️");
        }

        if (heartsText != null) {
            heartsText.setText(sb.toString());
        }
    }

    /**
     * Shows instructions before gameplay begins.
     * Timer and interaction are enabled only after confirmation.
     */
    private void showInstructions() {

        // Check game mode (casual or timed)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        if (mode.equals("timed")) {
            // Skip instructions in timed mode
            startTime = System.currentTimeMillis();
            submitGuessBtn.setEnabled(true);
            return;
        }

        pauseStartTime = System.currentTimeMillis();

        // Show instruction dialog
        new AlertDialog.Builder(this)
                .setTitle("Level 9: Unlock the Cities")
                .setMessage("A clue will appear for a famous city.\n\n" +
                        "1. Move the map to your guess.\n" +
                        "2. Tap 'Submit Guess'.\n" +
                        "3. Unlock " + TOTAL_CORRECT_TO_FINISH + " cities to win.\n\n" +
                        "You only have 3 hearts.")
                .setCancelable(false)
                .setPositiveButton("Start Guessing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Track paused time (for scoring accuracy)
                        long pausedDuration = System.currentTimeMillis() - pauseStartTime;
                        ProgressStorage.addPausedTime(UnlockCityActivity.this, pausedDuration);

                        // Start gameplay timer
                        startTime = System.currentTimeMillis();
                        submitGuessBtn.setEnabled(true);
                    }
                })
                .show();
    }

    /**
     * Initializes all cities used in the game.
     */
    private void setupCities() {
        cityList.add(new City("Paris", new LatLng(48.8566, 2.3522), "This city is home to the Eiffel Tower."));
        cityList.add(new City("New York", new LatLng(40.7128, -74.0060), "Known as the Big Apple."));
        cityList.add(new City("Tokyo", new LatLng(35.6895, 139.6917), "Famous for its cherry blossoms and technology."));
        cityList.add(new City("London", new LatLng(51.5074, -0.1278), "The home of Big Ben."));
        cityList.add(new City("Sydney", new LatLng(-33.8688, 151.2093), "Famous for its Opera House."));
        cityList.add(new City("Rio de Janeiro", new LatLng(-22.9068, -43.1729), "Known for Christ the Redeemer."));
        cityList.add(new City("Cairo", new LatLng(30.0444, 31.2357), "Near the Great Pyramids."));
        cityList.add(new City("Moscow", new LatLng(55.7558, 37.6173), "Famous for the Kremlin."));
        cityList.add(new City("Rome", new LatLng(41.9028, 12.4964), "Known for the Colosseum."));
        cityList.add(new City("Dubai", new LatLng(25.2048, 55.2708), "Home to the tallest building."));
        cityList.add(new City("Berlin", new LatLng(52.5200, 13.4050), "Famous for the Berlin Wall."));
        cityList.add(new City("San Francisco", new LatLng(37.7749, -122.4194), "Known for the Golden Gate Bridge."));
    }

    /**
     * Randomizes city order for gameplay.
     */
    private void shuffleCities() {
        shuffledCities = new ArrayList<>(cityList);
        Collections.shuffle(shuffledCities);
        currentIndex = 0;
    }

    /**
     * Selects the next city and updates clue text.
     */
    private void pickRandomCity() {
        if (shuffledCities == null || shuffledCities.isEmpty()) {
            shuffleCities();
        }

        if (currentIndex >= shuffledCities.size()) {
            shuffleCities();
        }

        currentCity = shuffledCities.get(currentIndex);
        currentIndex++;
        clueText.setText(currentCity.clue);
    }

    /**
     * Called when Google Maps is ready.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        pickRandomCity();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
    }
}