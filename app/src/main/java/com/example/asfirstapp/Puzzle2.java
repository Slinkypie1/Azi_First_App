package com.example.asfirstapp;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Puzzle2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float[] orientation = new float[3];
    private float[] rotationMatrix = new float[9];
    private TextView directionText;
    private String[] directions = {"East", "West", "North"};
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puzzle2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            directionText = findViewById(R.id.directionText);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (rotationSensor == null) {
                Toast.makeText(this, "Compass not available on this device", Toast.LENGTH_LONG).show();
                finish();
                return insets;
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
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                SensorManager.getOrientation(rotationMatrix, orientation);

                float azimuth = (float) Math.toDegrees(orientation[0]); // Get azimuth
                azimuth = (azimuth + 360) % 360;

                String currentDirection = getDirection(azimuth);


                directionText.setText("Current Direction: " + currentDirection);


                if (currentDirection.equals(directions[currentStep])) {
                    currentStep++;
                    Toast.makeText(this, "Good! Face " + directions[currentStep % directions.length], Toast.LENGTH_SHORT).show();

                    // Check if the sequence is completed
                    if (currentStep == directions.length) {
                        Toast.makeText(this, "Congratulations! You completed the puzzle.", Toast.LENGTH_LONG).show();
                        currentStep = 0;
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        private String getDirection(float azimuth) {
            if (azimuth >= 45 && azimuth < 135) {
                return "East";
            } else if (azimuth >= 135 && azimuth < 225) {
                return "South";
            } else if (azimuth >= 225 && azimuth < 315) {
                return "West";
            } else {
                return "North";
            }
        }

    }