package com.example.asfirstapp;

// Imports for sensors, UI, and activity management
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Puzzle2 Activity
 * ----------------
 * Compass-based riddle puzzle:
 * The player must face specific cardinal directions (East, West, North)
 * in order to progress through the steps and reach the success screen.
 */
public class Puzzle2 extends BaseMenuActivity implements SensorEventListener {

    private SensorManager sensorManager;    // Handles all sensor operations
    private Sensor rotationSensor;          // Rotation vector sensor for compass
    private TextView hint;                  // Displays current riddle text
    private TextView directionText;         // Shows current detected direction
    private boolean puzzleCompleted = false; // Prevent multiple success triggers
    private String lastDirection = "";      // Last detected compass direction
    private long lastUpdateTime = 0;        // Timestamp of last direction change
    private int currentStep = 0;            // Tracks which riddle step user is on

    private static final long SENSOR_UPDATE_THRESHOLD = 500; // ms between sensor readings
    private long lastSensorUpdate = 0; // Timestamp of last sensor reading

    // Array of riddles corresponding to each compass step
    private final String[] riddles = {
            "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",  // Step 0: East
            "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",  // Step 1: West
            "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"  // Step 2: North
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge modern layouts
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_puzzle2); // Set layout for this activity

        // Link UI components
        hint = findViewById(R.id.hint);
        directionText = findViewById(R.id.directionText);

        hint.setText(riddles[currentStep]); // Show first riddle

        // Initialize sensor manager and rotation vector sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor == null) {
            // Exit if device has no rotation sensor
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotationSensor != null) {
            // Register listener for rotation sensor updates
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rotationSensor != null) {
            sensorManager.unregisterListener(this); // Stop updates to save battery
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Exit if puzzle is already solved or event is from another sensor
        if (puzzleCompleted || event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;

        long currentTime = System.currentTimeMillis();
        // Limit sensor update frequency to avoid jitter
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_THRESHOLD) return;
        lastSensorUpdate = currentTime;

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        // Convert rotation vector to rotation matrix
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        // Convert rotation matrix to azimuth, pitch, roll
        SensorManager.getOrientation(rotationMatrix, orientation);

        int azimuth = (int) Math.toDegrees(orientation[0]); // Azimuth in degrees
        azimuth = (azimuth + 360) % 360; // Normalize 0–359°

        // Convert azimuth to cardinal direction
        String currentDirection = getDirection(azimuth);

        // Update last direction if it changed
        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection;
            lastUpdateTime = currentTime;
        }

        // Update UI with current direction
        directionText.setText("Current Direction: " + currentDirection);

        // Check if user is facing the correct direction for this step for >1s
        if (currentDirection.equals(getDirectionForStep(currentStep))
                && (currentTime - lastUpdateTime > 1000)) {
            currentStep++; // Move to next puzzle step

            if (currentStep == riddles.length) {
                // All riddles solved: puzzle completed
                puzzleCompleted = true;
                sensorManager.unregisterListener(this); // Stop sensor
                startActivity(new Intent(this, CorrectScreen5.class)); // Navigate to success
                finish(); // Close this activity
            } else {
                // Show next riddle
                hint.setText(riddles[currentStep]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used but required by interface
    }

    /**
     * Convert azimuth angle to cardinal direction.
     */
    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";
        if (azimuth >= 135 && azimuth < 225) return "South";
        if (azimuth >= 225 && azimuth < 315) return "West";
        return "North";
    }

    /**
     * Returns expected compass direction for the current puzzle step.
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
