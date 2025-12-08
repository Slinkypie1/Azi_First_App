package com.example.asfirstapp;
// Defines the package namespace of this activity.

import android.content.Intent;         // Used to navigate between activities.
import android.hardware.Sensor;        // Represents hardware sensors.
import android.hardware.SensorEvent;   // Contains sensor reading data.
import android.hardware.SensorEventListener; // Interface to listen to sensor updates.
import android.hardware.SensorManager; // Manages hardware sensors.
import android.os.Bundle;              // Stores activity state information.
import android.widget.TextView;        // UI element for displaying text.

import androidx.activity.EdgeToEdge;   // Enables fullscreen edge-to-edge layouts.
import androidx.appcompat.app.AppCompatActivity; // Base activity class with AppCompat support.

public class Puzzle2 extends BaseMenuActivity implements SensorEventListener {
    // Activity for the compass/riddle puzzle.
    // Implements SensorEventListener to track device rotation.

    private SensorManager sensorManager;    // Manages device sensors.
    private Sensor rotationSensor;          // Sensor used to detect rotation vector.
    private TextView hint;                  // Displays current riddle.
    private TextView directionText;         // Displays current facing direction.
    private boolean puzzleCompleted = false; // Tracks whether puzzle is solved.
    private String lastDirection = "";      // Stores last detected compass direction.
    private long lastUpdateTime = 0;        // Timestamp of last direction change.
    private int currentStep = 0;            // Tracks which riddle step user is on.

    private static final long SENSOR_UPDATE_THRESHOLD = 500; // Minimum ms between sensor reads.
    private long lastSensorUpdate = 0; // Tracks last sensor update time.

    // Array of riddles for the compass puzzle.
    private final String[] riddles = {
            "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",  // Step 0: East
            "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",  // Step 1: West
            "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"  // Step 2: North
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable modern fullscreen layouts.
        setContentView(R.layout.activity_puzzle2); // Set layout for the activity.

        hint = findViewById(R.id.hint);               // Link TextView for riddle.
        directionText = findViewById(R.id.directionText); // Link TextView for direction display.

        hint.setText(riddles[currentStep]); // Show first riddle.

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // Get sensor manager.
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); // Get rotation sensor.

        if (rotationSensor == null) {
            finish(); // Exit if device has no rotation sensor.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotationSensor != null) {
            // Register listener to receive rotation sensor updates.
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rotationSensor != null) {
            sensorManager.unregisterListener(this); // Stop sensor updates when paused.
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (puzzleCompleted || event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;
        // Exit if puzzle is already solved or event is from another sensor.

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_THRESHOLD) return;
        // Skip updates if too soon since last reading.
        lastSensorUpdate = currentTime;

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        // Convert rotation vector to rotation matrix.
        SensorManager.getOrientation(rotationMatrix, orientation);
        // Get azimuth, pitch, roll angles from rotation matrix.

        int azimuth = (int) Math.toDegrees(orientation[0]); // Convert azimuth to degrees.
        azimuth = (azimuth + 360) % 360; // Normalize to 0–359°.

        String currentDirection = getDirection(azimuth); // Convert azimuth to compass direction.

        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection; // Update last detected direction.
            lastUpdateTime = currentTime;     // Record the time of change.
        }

        directionText.setText("Current Direction: " + currentDirection); // Show direction.

        // If user is facing correct direction for the step for >1s, progress.
        if (currentDirection.equals(getDirectionForStep(currentStep))
                && (currentTime - lastUpdateTime > 1000)) {
            currentStep++; // Move to next step.

            if (currentStep == riddles.length) {
                // All steps completed, puzzle solved.
                puzzleCompleted = true;
                sensorManager.unregisterListener(this); // Stop listening to sensor.
                startActivity(new Intent(this, CorrectScreen5.class)); // Navigate to success screen.
                finish(); // Close puzzle activity.
            } else {
                // Show next riddle.
                hint.setText(riddles[currentStep]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used but required for SensorEventListener interface.
    }

    // Converts azimuth degrees to cardinal compass direction.
    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";   // 90° = East
        if (azimuth >= 135 && azimuth < 225) return "South"; // 180° = South
        if (azimuth >= 225 && azimuth < 315) return "West";  // 270° = West
        return "North";                                       // 0°/360° = North
    }

    // Returns expected direction for the current puzzle step.
    private String getDirectionForStep(int step) {
        switch (step) {
            case 0: return "East"; // First riddle answer
            case 1: return "West"; // Second riddle answer
            case 2: return "North"; // Third riddle answer
            default: return "North"; // Fallback
        }
    }
}
