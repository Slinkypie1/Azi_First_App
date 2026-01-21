package com.example.asfirstapp;

// Imports for sensors, context, and activity management
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Puzzle3 Activity
 * ----------------
 * Maze puzzle controlled by tilting the device.
 * The ball moves inside a custom MazeGridView according to accelerometer readings.
 */
public class Puzzle3 extends BaseMenuActivity implements SensorEventListener {

    private SensorManager sensorManager;  // Manages all device sensors
    private Sensor accelerometer;         // Detects device tilt
    private MazeGridView mazeGridView;    // Custom view that draws maze and ball

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate custom maze view
        mazeGridView = new MazeGridView(this);
        // Set the custom maze view as the activity’s content
        setContentView(mazeGridView);

        // Get sensor service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // Get accelerometer sensor
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            // Register listener for accelerometer updates
            // SENSOR_DELAY_GAME is suitable for games with moderate refresh
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listener to save battery when activity is not visible
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float tiltX = event.values[0]; // Tilt left/right
            float tiltY = event.values[1]; // Tilt forward/backward
            // Update the ball position in the maze based on tilt
            mazeGridView.updateBall(tiltX, tiltY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not required for this puzzle but must be implemented
    }
}
