package com.example.asfirstapp;

// Imports necessary Android classes for a service and audio playback
import android.app.Service;       // Base class for Android services
import android.content.Intent;    // Needed to start/stop service via Intents
import android.media.MediaPlayer; // Used to play audio files
import android.os.IBinder;        // Used for bound services (not used here)

/**
 * MusicService is a background service that plays looping music
 * throughout the app. It can play different tracks based on the intent passed.
 */
public class MusicService extends Service {

    private MediaPlayer mediaPlayer; // MediaPlayer instance to handle audio
    private int currentResId = -1;    // Currently playing resource ID

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called whenever startService() is invoked.
     * Checks if a specific music resource was requested or if a pause/resume action is needed.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("ACTION_PAUSE".equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                return START_STICKY;
            } else if ("ACTION_RESUME".equals(action)) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                return START_STICKY;
            }
        }

        int musicResId = R.raw.main_activity_music; // Default music

        if (intent != null && intent.hasExtra("MUSIC_RES_ID")) {
            musicResId = intent.getIntExtra("MUSIC_RES_ID", R.raw.main_activity_music);
        }

        // Only restart if the resource has changed or isn't playing
        if (mediaPlayer == null || currentResId != musicResId) {
            stopCurrentMusic();
            currentResId = musicResId;
            mediaPlayer = MediaPlayer.create(this, musicResId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        return START_STICKY;
    }

    private void stopCurrentMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCurrentMusic();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
