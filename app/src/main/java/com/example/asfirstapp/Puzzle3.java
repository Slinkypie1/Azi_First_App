package com.example.asfirstapp;
// Defines the package namespace for this class.

import android.content.Context;              // Needed for system services and views.
import android.hardware.Sensor;              // Represents hardware sensors.
import android.hardware.SensorEvent;         // Contains sensor reading data.
import android.hardware.SensorEventListener; // Interface to listen to sensor updates.
import android.hardware.SensorManager;       // Manages sensors on the device.
import android.os.Bundle;                     // Holds activity state information.

import androidx.appcompat.app.AppCompatActivity; // Base class for activities with AppCompat support.

public class Puzzle3 extends AppCompatActivity implements SensorEventListener {
    // Activity for maze puzzle controlled by tilting the device.
    // Implements SensorEventListener to track accelerometer data.

    private SensorManager sensorManager;  // Manages device sensors.
    private Sensor accelerometer;         // The accelerometer sensor (tilt detection).
    private MazeGridView mazeGridView;    // Custom View that draws the maze and the ball.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mazeGridView = new MazeGridView(this); // Instantiate custom maze view.
        setContentView(mazeGridView);          // Set the maze view as the content view.

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get access to the device's sensors.
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // Get the accelerometer sensor (used for tilt detection).
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            // Register listener for accelerometer updates when activity is visible.
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listener when activity is not visible to save battery.
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float tiltX = event.values[0]; // X-axis tilt (left/right)
            float tiltY = event.values[1]; // Y-axis tilt (forward/backward)
            mazeGridView.updateBall(tiltX, tiltY);
            // Update ball position in maze based on device tilt.
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this puzzle, but required by SensorEventListener.
    }
}
