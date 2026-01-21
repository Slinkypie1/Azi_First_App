package com.example.asfirstapp;

// Imports for sensors, UI, threading, and activity management
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Puzzle1 Activity
 * ----------------
 * This activity uses the light sensor to create a puzzle.
 * When ambient light drops below a certain threshold, the user "solves" the puzzle.
 */
public class Puzzle1 extends BaseMenuActivity implements SensorEventListener {

    private SensorManager sensorManager;       // Handles all sensor-related operations
    private Sensor lightSensor;                // Light sensor reference
    private TextView lightTextView;            // Shows the current light intensity to user

    private static boolean hasNavigated = false; // Prevent multiple triggers of success screen
    private boolean isFirstReading = true;      // Skip first reading to avoid spurious values
    private Handler handler = new Handler(Looper.getMainLooper()); // Runs delayed tasks on UI thread
    private Runnable navigateRunnable;          // Runnable to navigate to the success screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1); // Set the layout XML for this activity

        Log.d("Puzzle1", "Puzzle1 started!"); // Debug log to confirm activity start

        // Link the TextView from the layout
        lightTextView = findViewById(R.id.lightTextView);

        // Initialize the sensor manager and get the device's light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            // Device has no light sensor
            lightTextView.setText("No Light Sensor Found!");
        }

        // Reset navigation flag if coming back from correct answer screen
        if (getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }

        // Define navigation behavior after light condition is satisfied
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    Log.d("Puzzle1", "Navigating to CorrectScreen4...");
                    hasNavigated = true; // Prevent multiple triggers

                    // Stop listening to sensor updates
                    sensorManager.unregisterListener(Puzzle1.this);

                    // Navigate to the success screen
                    Intent intent = new Intent(Puzzle1.this, CorrectScreen4.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Close current activity
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0]; // Current light intensity in lux
        Log.d("Puzzle1", "Light Sensor Value: " + lightValue); // Debug log

        // Skip first reading to avoid false triggers
        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        // Update the TextView with current light intensity
        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        if (lightValue <= 5 && !hasNavigated) {
            // Low light detected: schedule navigation after 3 seconds
            handler.postDelayed(navigateRunnable, 3000);
        } else {
            // Light above threshold: cancel pending navigation
            handler.removeCallbacks(navigateRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            // Start receiving light sensor updates
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Reset navigation flag if not coming from success screen
        if (!getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening to sensor updates to save battery
        sensorManager.unregisterListener(this);

        // Cancel any pending navigation if activity is paused
        handler.removeCallbacks(navigateRunnable);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required override for SensorEventListener; not used in this puzzle
    }
}
