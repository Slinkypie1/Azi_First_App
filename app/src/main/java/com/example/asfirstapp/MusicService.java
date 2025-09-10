package com.example.asfirstapp;
// Defines the package name of this class (same as the app’s namespace).

import android.app.Service;       // Base class for Android background services.
import android.content.Intent;    // Used for starting/stopping the service.
import android.media.MediaPlayer; // Used to play audio files.
import android.os.IBinder;        // Used for binding services (not needed here).

public class MusicService extends Service {
    // MusicService is a background service that plays music.

    private MediaPlayer mediaPlayer;
    // MediaPlayer object to handle audio playback.

    @Override
    public void onCreate() {
        super.onCreate();
        // Called when the service is first created (only once in its lifecycle).

        mediaPlayer = MediaPlayer.create(this, R.raw.puzzle_game_music);
        // Initializes MediaPlayer with the audio file located in res/raw/puzzle_game_music.mp3 (or .wav, etc.).

        mediaPlayer.setLooping(true);
        // Makes the music loop continuously instead of stopping when finished.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Called every time startService() is called from an activity.

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            // Start playing music if it isn’t already playing.
        }
        return START_STICKY;
        // If the system kills the service, it restarts automatically with a null intent.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Called when the service is stopped or destroyed.

        if (mediaPlayer != null) {
            mediaPlayer.stop();    // Stops playback.
            mediaPlayer.release(); // Frees resources used by MediaPlayer.
            mediaPlayer = null;    // Avoids memory leaks by setting it to null.
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // This service is not designed for binding, only for starting/stopping.
    }
}
