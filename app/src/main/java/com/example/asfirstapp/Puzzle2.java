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
    private TextView hint;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private TextView directionText;
    private final String[] directions = {"East", "West", "North"};
    private int currentStep = 0;
    private boolean puzzleCompleted = false;
    private String lastDirection = "";
    private long lastUpdateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puzzle2);

        directionText = findViewById(R.id.directionText);
        hint = findViewById(R.id.hint);
        initHint();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor == null) {
            finish();
        }

        directionText.setText("Face " + directions[currentStep]);
    }

    private void initHint() {
        String[] riddles = {
                "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",
                "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",
                "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"
        };
        hint.setText(riddles[currentStep]);
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

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        int azimuth = (int) Math.toDegrees(orientation[0]);
        azimuth = (azimuth + 360) % 360;

        String currentDirection = getDirection(azimuth);
        long currentTime = System.currentTimeMillis();

        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection;
            lastUpdateTime = currentTime;
        }

        if (currentDirection.equals(directions[currentStep]) && (currentTime - lastUpdateTime > 1000)) {
            currentStep++;

            if (currentStep == directions.length) {
                puzzleCompleted = true;
                sensorManager.unregisterListener(this);
                startActivity(new Intent(this, CorrectScreen5.class));
                finish();
            } else {
                directionText.setText("Good! Now face " + directions[currentStep]);
                hint.setText(getHintForStep(currentStep));
            }
        } else {
            directionText.setText("Current Direction: " + currentDirection);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";
        if (azimuth >= 135 && azimuth < 225) return "South";
        if (azimuth >= 225 && azimuth < 315) return "West";
        return "North";
    }

    private String getHintForStep(int step) {
        String[] riddles = {
                "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",
                "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",
                "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"
        };
        return step < riddles.length ? riddles[step] : "";
    }
}
