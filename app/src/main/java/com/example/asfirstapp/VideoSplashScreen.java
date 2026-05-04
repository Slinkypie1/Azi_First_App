package com.example.asfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * VideoSplashScreen
 * ------------------
 * First screen shown when the app launches.
 * Plays a full-screen video and then automatically navigates
 * to the next splash screen.
 */
public class VideoSplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge fullscreen layout
        EdgeToEdge.enable(this);

        // Set layout containing the VideoView
        setContentView(R.layout.activity_video_splash);

        // Reference to VideoView in layout
        VideoView videoView = findViewById(R.id.videoView);

        // Build URI pointing to video stored in res/raw
        String path = "android.resource://" + getPackageName() + "/" + R.raw.slinkypie1_launch;
        videoView.setVideoURI(Uri.parse(path));

        // When video finishes, go to next screen
        videoView.setOnCompletionListener(mp -> navigateToNext());

        // If video fails to play, still continue to next screen
        videoView.setOnErrorListener((mp, what, extra) -> {
            navigateToNext();
            return true;
        });

        // Start video playback
        videoView.start();
    }

    /**
     * Navigates from video splash screen to the main splash screen.
     */
    private void navigateToNext() {
        Intent intent = new Intent(VideoSplashScreen.this, SplashScreen.class);
        startActivity(intent);
        finish(); // Close this activity so user cannot return to it
    }
}