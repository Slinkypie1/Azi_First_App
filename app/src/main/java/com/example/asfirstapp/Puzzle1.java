package com.example.asfirstapp;

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

import androidx.appcompat.app.AppCompatActivity;

public class Puzzle1 extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView lightTextView;
    private static boolean hasNavigated = false; // Persistent flag
    private boolean isFirstReading = true; // Ignore first reading
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable navigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1);

        Log.d("Puzzle1", "Puzzle1 started!");

        lightTextView = findViewById(R.id.lightTextView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            lightTextView.setText("No Light Sensor Found!");
        }

        // Reset navigation flag if coming from another screen
        if (getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }

        // Define navigation behavior with delay
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    Log.d("Puzzle1", "Navigating to CorrectScreen4...");
                    hasNavigated = true;
                    sensorManager.unregisterListener(Puzzle1.this);
                    Intent intent = new Intent(Puzzle1.this, CorrectScreen4.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent going back
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

        // Ignore the first sensor reading
        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        if (lightValue <= 5 && !hasNavigated) {
            handler.postDelayed(navigateRunnable, 3000); // Ensure light stays low for 3 seconds before navigating
        } else {
            handler.removeCallbacks(navigateRunnable); // Cancel navigation if light increases
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Reset navigation flag if coming from another screen
        if (!getIntent().getBooleanExtra("from_correct_screen", false)) {
            hasNavigated = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(navigateRunnable); // Stop any pending navigation
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
