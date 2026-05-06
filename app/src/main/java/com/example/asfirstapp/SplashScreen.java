package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * SplashScreen Activity
 * ----------------------
 * This is the first screen shown when the app launches.
 * It initializes Firebase, optionally performs a silent login,
 * and then navigates to the MainActivity after a short delay.
 */
public class SplashScreen extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern fullscreen edge-to-edge layout
        EdgeToEdge.enable(this);

        // Set splash screen layout (logo / loading screen)
        setContentView(R.layout.activity_splash_screen);

        // Initialize Firestore instance early so Firebase is ready for use
        FirebaseFirestore.getInstance();

        // Get Firebase authentication instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        final Intent intent;
        if (mAuth.getCurrentUser() != null) {
            // Already logged in, skip login screen
            intent = new Intent(SplashScreen.this, Second.class);
            Log.d("SPLASH", "User already logged in: " + mAuth.getCurrentUser().getEmail());
        } else {
            // Not logged in, go to login screen
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }

        // Wait 2 seconds before moving to the next screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // Disable menu for splash screen (no settings or actions here)
        return false;
    }
}