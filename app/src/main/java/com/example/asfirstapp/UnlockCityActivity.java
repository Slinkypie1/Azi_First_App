package com.example.asfirstapp;

// Imports for Android UI, maps, location, and navigation
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
 */
public class UnlockCityActivity extends BaseMenuActivity implements OnMapReadyCallback {

    private GoogleMap mMap;           // Google Map instance
    private TextView clueText;        // Displays the clue for the current city
    private Button submitGuessBtn;    // Button to submit user's map guess

    private List<City> cityList = new ArrayList<>();   // Master list of cities
    private List<City> shuffledCities;                // Randomized order for gameplay
    private int currentIndex = 0;                     // Index for current city
    private City currentCity;                         // The city the player is currently guessing

    private int correctGuessCount = 0;               // Track how many correct guesses
    private static final int TOTAL_CORRECT_TO_FINISH = 6; // Number of correct guesses to finish
    private static final float ALLOWED_RADIUS_METERS = 500_000; // Allowed guess distance (500 km)

    private long startTime;                           // Track how long the player takes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_unlock_city); // Load layout XML

        startTime = System.currentTimeMillis(); // Record puzzle start time

        // Link UI elements
        clueText = findViewById(R.id.clueText);
        submitGuessBtn = findViewById(R.id.submitGuessBtn);

        // Initialize cities and shuffle order
        setupCities();
        shuffleCities();

        // Initialize Google Maps fragment asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Calls onMapReady when map is loaded
        }

        // Handle guess submission
        submitGuessBtn.setOnClickListener(v -> {
            if (mMap == null || currentCity == null) return; // Ensure map and city exist

            // Use the map center as player's guess
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
                    // Completed required number of cities
                    long timeTaken = System.currentTimeMillis() - startTime;
                    Intent intent = new Intent(this, CorrectScreen9.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    startActivity(intent);
                    finish(); // Close activity
                } else {
                    // Move to next city
                    pickRandomCity();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // Reset map
                }
            } else {
                // Guess too far
                Toast.makeText(this, "❌ Try again, not close enough.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initialize master list of cities with coordinates and clues.
     */
    private void setupCities() {
        cityList.add(new City("Paris", new LatLng(48.8566, 2.3522), "This city is home to the Eiffel Tower."));
        cityList.add(new City("New York", new LatLng(40.7128, -74.0060), "Known as the Big Apple."));
        cityList.add(new City("Tokyo", new LatLng(35.6895, 139.6917), "Famous for its cherry blossoms and technology."));
        cityList.add(new City("London", new LatLng(51.5074, -0.1278), "The home of Big Ben."));
        cityList.add(new City("Sydney", new LatLng(-33.8688, 151.2093), "Famous for its Opera House."));
        cityList.add(new City("Rio de Janeiro", new LatLng(-22.9068, -43.1729), "Known for the Christ the Redeemer statue."));
        cityList.add(new City("Cairo", new LatLng(30.0444, 31.2357), "Near the Great Pyramids."));
        cityList.add(new City("Moscow", new LatLng(55.7558, 37.6173), "Famous for the Kremlin."));
        cityList.add(new City("Rome", new LatLng(41.9028, 12.4964), "Known for the Colosseum."));
        cityList.add(new City("Dubai", new LatLng(25.2048, 55.2708), "Home to the tallest building in the world."));
        cityList.add(new City("Berlin", new LatLng(52.5200, 13.4050), "Famous for the Berlin Wall."));
        cityList.add(new City("San Francisco", new LatLng(37.7749, -122.4194), "Known for the Golden Gate Bridge."));
    }

    /**
     * Shuffle the city order for gameplay.
     */
    private void shuffleCities() {
        shuffledCities = new ArrayList<>(cityList); // Copy master list
        Collections.shuffle(shuffledCities);        // Randomize order
        currentIndex = 0;                           // Reset index
    }

    /**
     * Pick the next city in shuffled order and display its clue.
     */
    private void pickRandomCity() {
        if (shuffledCities == null || shuffledCities.isEmpty()) {
            shuffleCities(); // Ensure shuffled list exists
        }
        if (currentIndex >= shuffledCities.size()) {
            shuffleCities(); // Reshuffle if reached end
        }

        currentCity = shuffledCities.get(currentIndex); // Get current city
        currentIndex++;                                 // Increment index for next round
        clueText.setText(currentCity.clue);            // Update clue TextView
    }

    /**
     * Callback when Google Map is ready.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;        // Store map instance
        pickRandomCity();         // Show first city clue
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // World view zoom
    }
}
