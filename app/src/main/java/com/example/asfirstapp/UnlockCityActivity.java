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

public class UnlockCityActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView clueText;
    private Button submitGuessBtn;

    private List<City> cityList = new ArrayList<>();
    private List<City> shuffledCities;
    private int currentIndex = 0;
    private City currentCity;

    private int correctGuessCount = 0;
    private static final int TOTAL_CORRECT_TO_FINISH = 6;
    private static final float ALLOWED_RADIUS_METERS = 500000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_city);

        clueText = findViewById(R.id.clueText);
        submitGuessBtn = findViewById(R.id.submitGuessBtn);

        setupCities();
        shuffleCities();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        submitGuessBtn.setOnClickListener(v -> {
            if (mMap == null || currentCity == null) return;

            LatLng guess = mMap.getCameraPosition().target;

            float[] result = new float[1];
            Location.distanceBetween(
                    guess.latitude, guess.longitude,
                    currentCity.location.latitude, currentCity.location.longitude,
                    result);

            if (result[0] <= ALLOWED_RADIUS_METERS) {
                correctGuessCount++;

                Toast.makeText(UnlockCityActivity.this,
                        "✅ Correct! You unlocked " + currentCity.name + "!",
                        Toast.LENGTH_LONG).show();

                if (correctGuessCount >= TOTAL_CORRECT_TO_FINISH) {
                    Intent intent = new Intent(UnlockCityActivity.this, CorrectScreen9.class);
                    startActivity(intent);
                    finish();
                } else {
                    pickRandomCity();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
                }
            } else {
                Toast.makeText(UnlockCityActivity.this,
                        "❌ Try again, not close enough.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void shuffleCities() {
        shuffledCities = new ArrayList<>(cityList);
        Collections.shuffle(shuffledCities);
        currentIndex = 0;
    }

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        pickRandomCity();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
    }
}
