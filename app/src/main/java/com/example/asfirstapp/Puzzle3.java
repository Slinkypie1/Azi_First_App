package com.example.asfirstapp;

// Imports for sensors, context, and activity management
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Puzzle3 Activity
 * ----------------
 * Maze puzzle controlled by tilting the device.
 * The ball moves inside a custom MazeGridView according to accelerometer readings.
 */
public class Puzzle3 extends BaseMenuActivity implements SensorEventListener {

    // Sensor system used to detect device tilt
    private SensorManager sensorManager;  // Manages all device sensors
    private Sensor accelerometer;         // Detects device tilt (x/y movement)

    // Custom view that handles drawing maze + ball logic
    private MazeGridView mazeGridView;

    // Used to measure pause duration before starting game
    private long pauseStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start background music for Puzzle 3
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle3_music);
        startService(serviceIntent);

        // Create maze view programmatically
        mazeGridView = new MazeGridView(this);

        // Set custom view as the entire screen content
        setContentView(mazeGridView);

        // Show instructions dialog before gameplay begins
        showInstructions();

        // Get system sensor service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            // Get accelerometer sensor (used for tilt controls)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    /**
     * Displays instructions before starting the puzzle.
     * In timed mode, this is skipped automatically.
     */
    private void showInstructions() {

        // Check current game mode from shared preferences
        String mode = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("game_mode", "casual");

        if (mode.equals("timed")) {
            // Skip instructions in timed mode to avoid wasting time
            mazeGridView.beginGame();
            return;
        }

        // Record when instruction screen started
        pauseStartTime = System.currentTimeMillis();

        // Show popup dialog with instructions
        new AlertDialog.Builder(this)
                .setTitle("Level 6: Tilt Maze")
                .setMessage("Tilt your phone to guide the red ball to the green goal!\n\n" +
                        "Avoid the black walls")
                .setCancelable(false)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Calculate how long player spent on instructions
                        long pausedDuration = System.currentTimeMillis() - pauseStartTime;

                        // Save paused time for scoring adjustments
                        ProgressStorage.addPausedTime(Puzzle3.this, pausedDuration);

                        // Start maze countdown + gameplay
                        mazeGridView.beginGame();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register accelerometer listener when screen is active
        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME // Balanced speed for gameplay
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop listening to sensors to save battery and performance
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Only respond to accelerometer updates
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // X axis = left/right tilt
            float tiltX = event.values[0];

            // Y axis = forward/back tilt
            float tiltY = event.values[1];

            // Send movement data to maze view
            mazeGridView.updateBall(tiltX, tiltY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this puzzle
    }
}