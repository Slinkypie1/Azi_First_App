package com.example.asfirstapp;
// Defines the package namespace of this class.

import android.content.Context;        // Used to access system services like sensors.
import android.content.Intent;         // Used to navigate between activities.
import android.hardware.Sensor;        // Represents hardware sensors.
import android.hardware.SensorEvent;   // Represents a sensor reading event.
import android.hardware.SensorEventListener; // Interface to listen to sensor updates.
import android.hardware.SensorManager; // Manages hardware sensors.
import android.os.Bundle;              // Stores activity state information.
import android.os.Handler;             // For scheduling delayed tasks.
import android.os.Looper;              // Ties handler to main thread.
import android.util.Log;               // Logging utility.
import android.widget.TextView;        // UI element for displaying text.

import androidx.appcompat.app.AppCompatActivity; // Base class for activities.

public class Puzzle1 extends BaseMenuActivity implements SensorEventListener {
    // Activity for the light sensor puzzle.
    // Implements SensorEventListener to react to light sensor changes.

    private SensorManager sensorManager;        // Manages device sensors.
    private Sensor lightSensor;                 // Reference to the light sensor.
    private TextView lightTextView;             // Displays sensor readings.

    private static boolean hasNavigated = false; // Prevents multiple navigation triggers.
    private boolean isFirstReading = true;      // Ignore the very first sensor reading.
    private Handler handler = new Handler(Looper.getMainLooper()); // For delayed navigation.
    private Runnable navigateRunnable;          // Runnable task to navigate after delay.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1); // Sets layout for this activity.

        Log.d("Puzzle1", "Puzzle1 started!"); // Debug log.

        lightTextView = findViewById(R.id.lightTextView); // Link TextView from layout.

        // Get the sensor service and retrieve the light sensor.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            // No light sensor found on this device.
            lightTextView.setText("No Light Sensor Found!");
        }

        // Reset navigation flag if coming from a correct answer screen.
        if (getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }

        // Define navigation behavior after light condition is met.
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    Log.d("Puzzle1", "Navigating to CorrectScreen4...");
                    hasNavigated = true; // Prevent multiple navigations.
                    sensorManager.unregisterListener(Puzzle1.this); // Stop sensor updates.

                    Intent intent = new Intent(Puzzle1.this, CorrectScreen4.class);
                    // Flags prevent going back to this puzzle.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent); // Navigate to success screen.
                    finish(); // Close this activity.
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0]; // Get current light intensity.
        Log.d("Puzzle1", "Light Sensor Value: " + lightValue); // Debug log.

        // Ignore first reading to avoid spurious triggers.
        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        // Update TextView with current light intensity.
        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        if (lightValue <= 5 && !hasNavigated) {
            // If light is very low and not yet navigated, start delayed navigation.
            handler.postDelayed(navigateRunnable, 3000); // Wait 3 seconds.
        } else {
            // If light rises above threshold, cancel pending navigation.
            handler.removeCallbacks(navigateRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            // Start listening to light sensor updates.
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Reset navigation flag if not coming from correct screen.
        if (!getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); // Stop sensor updates to save battery.
        handler.removeCallbacks(navigateRunnable); // Cancel pending navigation if any.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required method for SensorEventListener; not used here.
    }
}
