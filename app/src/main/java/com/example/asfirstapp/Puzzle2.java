package com.example.asfirstapp;

// Imports for sensors, UI, and activity management
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

import androidx.activity.EdgeToEdge;

/**
 * Puzzle2 Activity
 * ----------------
 * Compass-based riddle puzzle:
 * The player must face specific cardinal directions (East, West, North)
 * in order to progress through the steps and reach the success screen.
 * Includes a 20-second timeout that leads to the Failure screen.
 */
public class Puzzle2 extends BaseMenuActivity implements SensorEventListener {

    // Sensor-related objects
    private SensorManager sensorManager;    // Manages device sensors
    private Sensor rotationSensor;          // Rotation vector sensor (compass)
    private TextView hint;                  // Displays the current riddle
    private TextView directionText;         // Shows current detected compass direction

    // Puzzle state
    private boolean puzzleCompleted = false; // Prevent multiple transitions
    private String lastDirection = "";       // Last detected compass direction
    private long lastUpdateTime = 0;         // Timestamp of last direction change
    private int currentStep = 0;             // Tracks which riddle step the user is on
    private long startTime;                  // Tracks when puzzle started

    private static final long SENSOR_UPDATE_THRESHOLD = 500; // Minimum ms between sensor updates
    private long lastSensorUpdate = 0; // Timestamp of last sensor reading

    private Handler handler = new Handler(Looper.getMainLooper()); // Main thread handler
    private Runnable failureRunnable; // Timeout task

    // Array of riddles for each step
    private final String[] riddles = {
            // Step 0: East
            "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",
            // Step 1: West
            "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",
            // Step 2: North
            "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge layout
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_puzzle2); // Load layout

        startTime = System.currentTimeMillis(); // Record start time

        // Link UI components
        hint = findViewById(R.id.hint);
        directionText = findViewById(R.id.directionText);

        hint.setText(riddles[currentStep]); // Show the first riddle

        // Initialize sensor manager and rotation vector sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor == null) {
            // Exit if no rotation sensor (compass) is available
            finish();
        }

        // Initialize the failure timeout runnable
        failureRunnable = new Runnable() {
            @Override
            public void run() {
                if (!puzzleCompleted) {
                    puzzleCompleted = true;
                    Log.d("Puzzle2", "20 seconds up! Navigating to Failure.");
                    
                    // Stop listening to sensors
                    sensorManager.unregisterListener(Puzzle2.this);

                    // Navigate to Failure activity
                    Intent intent = new Intent(Puzzle2.this, Failure.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotationSensor != null) {
            // Start receiving sensor updates
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }

        // Start the 20-second failure timer
        if (!puzzleCompleted) {
            handler.postDelayed(failureRunnable, 20000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rotationSensor != null) {
            // Stop sensor updates to save battery
            sensorManager.unregisterListener(this);
        }
        // Cancel the timeout if the activity is paused
        handler.removeCallbacks(failureRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Ignore if puzzle already completed or wrong sensor
        if (puzzleCompleted || event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;

        long currentTime = System.currentTimeMillis();
        // Throttle updates to SENSOR_UPDATE_THRESHOLD to avoid jitter
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_THRESHOLD) return;
        lastSensorUpdate = currentTime;

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        // Convert rotation vector to rotation matrix
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        // Convert rotation matrix to azimuth (compass angle), pitch, roll
        SensorManager.getOrientation(rotationMatrix, orientation);

        int azimuth = (int) Math.toDegrees(orientation[0]); // Azimuth in degrees
        azimuth = (azimuth + 360) % 360; // Normalize to 0–359°

        // Convert azimuth to a cardinal direction
        String currentDirection = getDirection(azimuth);

        // Update lastDirection only if direction changed
        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection;
            lastUpdateTime = currentTime;
        }

        // Update UI with current direction
        directionText.setText("Current Direction: " + currentDirection);

        // Check if facing correct direction for this step for more than 1 second
        if (currentDirection.equals(getDirectionForStep(currentStep))
                && (currentTime - lastUpdateTime > 1000)) {
            currentStep++; // Move to next puzzle step

            if (currentStep == riddles.length) {
                // All steps complete: puzzle solved
                puzzleCompleted = true;
                sensorManager.unregisterListener(this); // Stop listening

                // Cancel the failure timer since the user succeeded
                handler.removeCallbacks(failureRunnable);

                long timeTaken = System.currentTimeMillis() - startTime;

                // Navigate to success screen
                Intent intent = new Intent(this, CorrectScreen5.class);
                intent.putExtra("TIME_TAKEN", timeTaken);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Close this activity
            } else {
                // Show next riddle
                hint.setText(riddles[currentStep]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required override, not used here
    }

    /**
     * Convert azimuth angle to cardinal direction.
     * 0–44 & 315–359 = North
     * 45–134 = East
     * 135–224 = South
     * 225–314 = West
     */
    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";
        if (azimuth >= 135 && azimuth < 225) return "South";
        if (azimuth >= 225 && azimuth < 315) return "West";
        return "North";
    }

    /**
     * Returns expected direction for a given puzzle step.
     */
    private String getDirectionForStep(int step) {
        switch (step) {
            case 0: return "East";
            case 1: return "West";
            case 2: return "North";
            default: return "North"; // Fallback
        }
    }
}
