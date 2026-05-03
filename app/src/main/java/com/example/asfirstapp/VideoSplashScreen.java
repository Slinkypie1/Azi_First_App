package com.example.asfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class VideoSplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_splash);

        VideoView videoView = findViewById(R.id.videoView);
        
        // Build the URI for the video file
        String path = "android.resource://" + getPackageName() + "/" + R.raw.slinkypie1_launch;
        videoView.setVideoURI(Uri.parse(path));

        // When the video finishes, move to the next splash screen
        videoView.setOnCompletionListener(mp -> navigateToNext());

        // Also handle potential errors to avoid getting stuck
        videoView.setOnErrorListener((mp, what, extra) -> {
            navigateToNext();
            return true;
        });

        videoView.start();
    }

    private void navigateToNext() {
        Intent intent = new Intent(VideoSplashScreen.this, SplashScreen.class);
        startActivity(intent);
        finish();
    }
}
