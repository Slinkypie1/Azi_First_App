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

/**
 * Puzzle1 Activity
 * ----------------
 * Uses the light sensor to create a puzzle:
 * When ambient light drops below a threshold, the user "solves" the puzzle.
 */
public class Puzzle1 extends BaseMenuActivity implements SensorEventListener {

    // Sensor-related objects
    private SensorManager sensorManager;       // Manages access to device sensors
    private Sensor lightSensor;                // Represents the light sensor
    private TextView lightTextView;            // Shows current light level to the user

    // Flags and helpers
    private static boolean hasNavigated = false; // Ensures success screen is triggered only once
    private boolean isFirstReading = true;      // Skip the first reading to avoid spurious sensor values
    private Handler handler = new Handler(Looper.getMainLooper()); // Handles delayed tasks on main thread
    private Runnable navigateRunnable;          // Runnable to navigate to CorrectScreen4
    private long startTime;                     // Records the time when the puzzle started

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1); // Load the layout

        Log.d("Puzzle1", "Puzzle1 started!"); // Debug log

        startTime = System.currentTimeMillis(); // Record start time to calculate time taken

        // Link TextView from layout
        lightTextView = findViewById(R.id.lightTextView);

        // Initialize sensor manager and get device's light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            // Device does not have a light sensor
            lightTextView.setText("No Light Sensor Found!");
        }

        // Reset navigation flag if returning from success screen
        if (getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }

        // Runnable to navigate to CorrectScreen4 when the light condition is satisfied
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) { // Only navigate once
                    Log.d("Puzzle1", "Navigating to CorrectScreen4...");
                    hasNavigated = true;

                    // Stop listening to sensor updates
                    sensorManager.unregisterListener(Puzzle1.this);

                    long timeTaken = System.currentTimeMillis() - startTime; // Calculate time taken

                    // Start CorrectScreen4 and pass time taken
                    Intent intent = new Intent(Puzzle1.this, CorrectScreen4.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);

                    // Clear previous activities from back stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish(); // Close current activity
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0]; // Current ambient light in lux
        Log.d("Puzzle1", "Light Sensor Value: " + lightValue);

        // Skip first reading (sometimes inaccurate)
        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        // Display current light value
        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        if (lightValue <= 5 && !hasNavigated) {
            // Low light detected, schedule navigation after 3 seconds
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
            // Start listening to light sensor updates
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Reset navigation flag if not returning from success screen
        if (!getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop sensor updates to save battery
        sensorManager.unregisterListener(this);

        // Cancel any pending navigation
        handler.removeCallbacks(navigateRunnable);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required override for SensorEventListener; not used here
    }
}
