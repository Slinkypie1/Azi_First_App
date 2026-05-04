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

    // Sensor-related objects used for compass detection
    private SensorManager sensorManager;    // Manages device sensors
    private Sensor rotationSensor;          // Rotation vector sensor (used as compass)
    private TextView hint;                  // Displays the current riddle text
    private TextView directionText;         // Shows current detected direction

    // Puzzle state tracking
    private boolean puzzleCompleted = false; // Prevents multiple navigation triggers
    private String lastDirection = "";       // Stores last detected direction
    private long lastUpdateTime = 0;         // Time of last valid direction update
    private int currentStep = 0;             // Tracks progress through riddles
    private long startTime;                  // Records puzzle start time

    private static final long SENSOR_UPDATE_THRESHOLD = 500; // Limits sensor update frequency
    private long lastSensorUpdate = 0; // Last time sensor data was processed

    private Handler handler = new Handler(Looper.getMainLooper()); // Main thread handler
    private Runnable failureRunnable; // Runnable that triggers failure screen

    // Riddle list for each step of the puzzle
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

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Load layout for Puzzle2 screen
        setContentView(R.layout.activity_puzzle2);

        // Start background music for this puzzle
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle2_music);
        startService(serviceIntent);

        // Record start time for scoring
        startTime = System.currentTimeMillis();

        // Link UI elements
        hint = findViewById(R.id.hint);
        directionText = findViewById(R.id.directionText);

        // Display first riddle
        hint.setText(riddles[currentStep]);

        // Initialize sensor system
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Exit if device has no compass sensor
        if (rotationSensor == null) {
            finish();
        }

        // Failure timer logic (20 seconds timeout)
        failureRunnable = new Runnable() {
            @Override
            public void run() {
                if (!puzzleCompleted) {
                    puzzleCompleted = true;
                    Log.d("Puzzle2", "20 seconds up! Navigating to Failure.");

                    // Stop sensor updates
                    sensorManager.unregisterListener(Puzzle2.this);

                    // Navigate to failure screen
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

        // Start listening to compass sensor updates
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }

        // Load game mode (casual or timed)
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        // Start failure timer only in timed mode
        if (mode.equals("timed") && !puzzleCompleted) {
            handler.postDelayed(failureRunnable, 20000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop sensor updates when activity is not visible
        if (rotationSensor != null) {
            sensorManager.unregisterListener(this);
        }

        // Cancel failure timer to avoid leaks
        handler.removeCallbacks(failureRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Ignore updates if puzzle is complete or wrong sensor type
        if (puzzleCompleted || event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;

        long currentTime = System.currentTimeMillis();

        // Prevent too frequent updates
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_THRESHOLD) return;
        lastSensorUpdate = currentTime;

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        // Convert rotation vector into usable orientation values
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        int azimuth = (int) Math.toDegrees(orientation[0]);
        azimuth = (azimuth + 360) % 360; // Normalize angle

        // Convert angle into direction string
        String currentDirection = getDirection(azimuth);

        // Track direction changes
        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection;
            lastUpdateTime = currentTime;
        }

        // Update UI with current direction
        directionText.setText("Current Direction: " + currentDirection);

        // Check if correct direction is held long enough
        if (currentDirection.equals(getDirectionForStep(currentStep))
                && (currentTime - lastUpdateTime > 1000)) {

            currentStep++;

            // If all steps completed → success
            if (currentStep == riddles.length) {
                puzzleCompleted = true;
                sensorManager.unregisterListener(this);

                // Cancel failure timer
                handler.removeCallbacks(failureRunnable);

                long timeTaken = System.currentTimeMillis() - startTime;

                // Navigate to success screen
                Intent intent = new Intent(this, CorrectScreen5.class);
                intent.putExtra("TIME_TAKEN", timeTaken);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Load next riddle
                hint.setText(riddles[currentStep]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this puzzle
    }

    /**
     * Converts compass angle into a cardinal direction.
     */
    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";
        if (azimuth >= 135 && azimuth < 225) return "South";
        if (azimuth >= 225 && azimuth < 315) return "West";
        return "North";
    }

    /**
     * Returns expected direction for each puzzle step.
     */
    private String getDirectionForStep(int step) {
        switch (step) {
            case 0: return "East";
            case 1: return "West";
            case 2: return "North";
            default: return "North";
        }
    }
}