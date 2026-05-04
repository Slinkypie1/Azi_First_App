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
 * Light sensor-based puzzle:
 * The player must reduce ambient light below a threshold to succeed.
 */
public class Puzzle1 extends BaseMenuActivity implements SensorEventListener {

    // Sensor-related objects
    private SensorManager sensorManager;       // Handles access to device sensors
    private Sensor lightSensor;                // The device light sensor
    private TextView lightTextView;            // Displays current light value

    // State control variables
    private static boolean hasNavigated = false; // Prevents multiple screen transitions
    private boolean isFirstReading = true;      // Skips first sensor reading (often unstable)
    private Handler handler = new Handler(Looper.getMainLooper()); // Main-thread handler
    private Runnable navigateRunnable;          // Handles success navigation delay
    private Runnable failureRunnable;           // Handles timeout failure navigation
    private long startTime;                     // Stores puzzle start timestamp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1); // Load UI layout

        Log.d("Puzzle1", "Puzzle1 started!");

        // Start background music specific to this puzzle
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle1_music);
        startService(serviceIntent);

        startTime = System.currentTimeMillis(); // Record start time

        // Connect UI elements
        lightTextView = findViewById(R.id.lightTextView);

        // Initialize sensor system
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            // Device does not support light sensor
            lightTextView.setText("No Light Sensor Found!");
        }

        // Reset navigation lock
        hasNavigated = false;

        // FAILURE logic: triggers after timeout
        failureRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    hasNavigated = true;
                    Log.d("Puzzle1", "Time up → Failure screen");

                    // Stop sensor listening
                    sensorManager.unregisterListener(Puzzle1.this);

                    // Go to failure screen
                    Intent intent = new Intent(Puzzle1.this, Failure.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // SUCCESS logic: triggers after low light condition
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    hasNavigated = true;
                    Log.d("Puzzle1", "Success → CorrectScreen4");

                    // Stop sensor updates
                    sensorManager.unregisterListener(Puzzle1.this);

                    // Cancel failure timer
                    handler.removeCallbacks(failureRunnable);

                    long timeTaken = System.currentTimeMillis() - startTime;

                    // Navigate to success screen
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
        float lightValue = event.values[0]; // Current light level
        Log.d("Puzzle1", "Light Sensor Value: " + lightValue);

        // Ignore first unstable reading
        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        // Update UI with light value
        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        // If environment is dark enough → schedule success
        if (lightValue <= 5 && !hasNavigated) {
            handler.postDelayed(navigateRunnable, 3000);
        } else {
            // Cancel success if light increases again
            handler.removeCallbacks(navigateRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start listening to light sensor updates
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Check game mode (casual or timed)
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // Only start failure timer in timed mode
        if (mode.equals("timed") && !hasNavigated) {
            handler.postDelayed(failureRunnable, 20000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop sensor updates when activity is not visible
        sensorManager.unregisterListener(this);

        // Prevent memory leaks by removing callbacks
        handler.removeCallbacks(navigateRunnable);
        handler.removeCallbacks(failureRunnable);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this puzzle
    }
}