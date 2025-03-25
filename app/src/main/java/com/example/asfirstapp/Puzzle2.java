package com.example.asfirstapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Puzzle2 extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private TextView hint;
    private TextView directionText;
    private boolean puzzleCompleted = false;
    private String lastDirection = "";
    private long lastUpdateTime = 0;
    private int currentStep = 0;

    private static final long SENSOR_UPDATE_THRESHOLD = 500;
    private long lastSensorUpdate = 0;

    private final String[] riddles = {
            "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",  // East
            "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",  // West
            "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"  // North
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puzzle2);

        hint = findViewById(R.id.hint);
        directionText = findViewById(R.id.directionText);

        hint.setText(riddles[currentStep]);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rotationSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (puzzleCompleted || event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_THRESHOLD) return;
        lastSensorUpdate = currentTime;

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        int azimuth = (int) Math.toDegrees(orientation[0]);
        azimuth = (azimuth + 360) % 360;

        String currentDirection = getDirection(azimuth);

        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection;
            lastUpdateTime = currentTime;
        }

        // Show current direction
        directionText.setText("Current Direction: " + currentDirection);

        // Progress steps only after East and West are completed, then move to North
        if (currentDirection.equals(getDirectionForStep(currentStep)) && (currentTime - lastUpdateTime > 1000)) {
            currentStep++;

            // If all steps are completed and facing North, move to CorrectScreen5
            if (currentStep == riddles.length) {
                puzzleCompleted = true;
                sensorManager.unregisterListener(this);
                startActivity(new Intent(this, CorrectScreen5.class));
                finish();
            } else {
                // Update riddle for next step
                hint.setText(riddles[currentStep]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Correct real-world compass directions
    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";    // 90° is East
        if (azimuth >= 135 && azimuth < 225) return "South";   // 180° is South
        if (azimuth >= 225 && azimuth < 315) return "West";    // 270° is West
        return "North";                                        // 0° or 360° is North
    }

    // Get direction based on current step
    private String getDirectionForStep(int step) {
        switch (step) {
            case 0:
                return "East";
            case 1:
                return "West";
            case 2:
                return "North";
            default:
                return "North";  // Default case
        }
    }
}
