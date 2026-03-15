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
    private static boolean hasNavigated = false; // Ensures screen transitions happen only once
    private boolean isFirstReading = true;      // Skip the first reading to avoid spurious sensor values
    private Handler handler = new Handler(Looper.getMainLooper()); // Handles delayed tasks on main thread
    private Runnable navigateRunnable;          // Runnable to navigate to CorrectScreen4 (Success)
    private Runnable failureRunnable;           // Runnable to navigate to Failure screen (Timeout)
    private long startTime;                     // Records the time when the puzzle started

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1); // Load the layout

        Log.d("Puzzle1", "Puzzle1 started!");

        // Start background music for Puzzle 1
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle1_music);
        startService(serviceIntent);

        startTime = System.currentTimeMillis(); // Record start time

        // Link TextView from layout
        lightTextView = findViewById(R.id.lightTextView);

        // Initialize sensor manager and get device's light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            // Device does not have a light sensor
            lightTextView.setText("No Light Sensor Found!");
        }

        // Reset navigation flag
        hasNavigated = false;

        // Runnable for failure (10-second timeout)
        failureRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    hasNavigated = true;
                    Log.d("Puzzle1", "10 seconds up! Navigating to Failure.");
                    
                    // Stop listening to sensor updates
                    sensorManager.unregisterListener(Puzzle1.this);
                    
                    // Navigate to Failure activity
                    Intent intent = new Intent(Puzzle1.this, Failure.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // Runnable for success (Low light detected)
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    hasNavigated = true;
                    Log.d("Puzzle1", "Navigating to CorrectScreen4...");

                    // Stop listening to sensor updates
                    sensorManager.unregisterListener(Puzzle1.this);
                    
                    // Cancel the failure timer since the user succeeded
                    handler.removeCallbacks(failureRunnable);

                    long timeTaken = System.currentTimeMillis() - startTime;

                    // Start CorrectScreen4
                    Intent intent = new Intent(Puzzle1.this, CorrectScreen4.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0];
        Log.d("Puzzle1", "Light Sensor Value: " + lightValue);

        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        if (lightValue <= 5 && !hasNavigated) {
            // Low light detected, schedule success navigation after 3 seconds
            handler.postDelayed(navigateRunnable, 3000);
        } else {
            // Light above threshold: cancel pending success navigation
            handler.removeCallbacks(navigateRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        
        // Start the 10-second failure timer
        if (!hasNavigated) {
            handler.postDelayed(failureRunnable, 10000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop sensor updates
        sensorManager.unregisterListener(this);

        // Cancel any pending transitions to avoid background leaks
        handler.removeCallbacks(navigateRunnable);
        handler.removeCallbacks(failureRunnable);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
