package com.example.asfirstapp;

// Imports necessary Android classes for a service and audio playback
import android.app.Service;       // Base class for Android services
import android.content.Intent;    // Needed to start/stop service via Intents
import android.media.MediaPlayer; // Used to play audio files
import android.os.IBinder;        // Used for bound services (not used here)

/**
 * MusicService is a background service that plays looping music
 * throughout the app. It starts when MainActivity launches and stops
 * when the app or activity stops the service.
 */
public class MusicService extends Service {

    private MediaPlayer mediaPlayer; // MediaPlayer instance to handle audio

    /**
     * Called when the service is first created. Initialize MediaPlayer here.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Load the audio file from res/raw folder
        mediaPlayer = MediaPlayer.create(this, R.raw.puzzle_game_music);

        // Set the music to loop indefinitely
        mediaPlayer.setLooping(true);
    }

    /**
     * Called whenever startService() is invoked.
     *
     * @param intent The Intent supplied to startService()
     * @param flags  Additional data about how the service was started
     * @param startId Unique ID for this start request
     * @return START_STICKY to restart if killed
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Begin playback
        }

        // START_STICKY ensures the service restarts if killed by the system
        return START_STICKY;
    }

    /**
     * Called when the service is stopped or destroyed.
     * Release MediaPlayer resources to avoid memory leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();    // Stop playback
            mediaPlayer.release(); // Release MediaPlayer resources
            mediaPlayer = null;    // Prevent memory leaks
        }
    }

    /**
     * Binding is not used in this service.
     * @param intent The Intent supplied to bindService()
     * @return null because we do not support binding
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
