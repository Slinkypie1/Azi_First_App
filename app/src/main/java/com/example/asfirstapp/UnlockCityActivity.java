package com.example.asfirstapp;

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

// Activity where the user tries to "unlock" cities by navigating on the map
public class UnlockCityActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Google Map instance
    private TextView clueText; // TextView to display the clue for the current city
    private Button submitGuessBtn; // Button to submit the user's guess

    private List<City> cityList = new ArrayList<>(); // All possible cities
    private List<City> shuffledCities; // Shuffled order of cities to present
    private int currentIndex = 0; // Current city index
    private City currentCity; // Currently active city

    private int correctGuessCount = 0; // Track how many correct guesses
    private static final int TOTAL_CORRECT_TO_FINISH = 6; // Finish after 6 correct
    private static final float ALLOWED_RADIUS_METERS = 500_000; // How close the guess must be (500 km)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_city); // Load layout

        clueText = findViewById(R.id.clueText); // Connect clue TextView
        submitGuessBtn = findViewById(R.id.submitGuessBtn); // Connect submit button

        setupCities(); // Initialize list of cities with names, locations, and clues
        shuffleCities(); // Shuffle cities for random order

        // Initialize Google Maps fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Async load map
        }

        // Handle guess submission
        submitGuessBtn.setOnClickListener(v -> {
            if (mMap == null || currentCity == null) return;

            LatLng guess = mMap.getCameraPosition().target; // Get the center of the map as user's guess

            float[] result = new float[1];
            Location.distanceBetween(
                    guess.latitude, guess.longitude, // user's guessed location
                    currentCity.location.latitude, currentCity.location.longitude, // actual city location
                    result);

            if (result[0] <= ALLOWED_RADIUS_METERS) { // Check if guess is within allowed radius
                correctGuessCount++; // Increase correct count
                Toast.makeText(UnlockCityActivity.this,
                        "✅ Correct! You unlocked " + currentCity.name + "!",
                        Toast.LENGTH_LONG).show();

                if (correctGuessCount >= TOTAL_CORRECT_TO_FINISH) { // Completed all required cities
                    Intent intent = new Intent(UnlockCityActivity.this, CorrectScreen9.class);
                    startActivity(intent);
                    finish();
                } else { // Move to next city
                    pickRandomCity();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // Reset map zoom
                }
            } else {
                Toast.makeText(UnlockCityActivity.this,
                        "❌ Try again, not close enough.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Initialize all cities with name, coordinates, and a clue
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

    // Shuffle city order randomly
    private void shuffleCities() {
        shuffledCities = new ArrayList<>(cityList);
        Collections.shuffle(shuffledCities);
        currentIndex = 0;
    }

    // Pick the next city to show clue for
    private void pickRandomCity() {
        if (shuffledCities == null || shuffledCities.isEmpty()) {
            shuffleCities();
        }
        if (currentIndex >= shuffledCities.size()) {
            shuffleCities();
        }
        currentCity = shuffledCities.get(currentIndex);
        currentIndex++;
        clueText.setText(currentCity.clue); // Display clue to user
    }

    // Called when Google Map is ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        pickRandomCity(); // Show first city
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // Set initial zoom to world view
    }
}
