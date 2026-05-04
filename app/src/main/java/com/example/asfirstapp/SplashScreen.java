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

        // Attempt silent sign-in if no user is currently logged in
        if (mAuth.getCurrentUser() == null) {

            // Hardcoded login (used for automatic authentication at startup)
            mAuth.signInWithEmailAndPassword("azriel.zev@gmail.com", "A'$Sc80ol@9p")
                    .addOnCompleteListener(task -> {

                        // Log success if login worked
                        if (task.isSuccessful()) {
                            Log.d("SPLASH", "Silent sign-in successful");
                        }
                    });
        }

        // Wait 2 seconds before moving to the next screen
        // This gives time for splash logo animation / branding display
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // Navigate to MainActivity after splash delay
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);

            // Close splash screen so user cannot return to it
            finish();

        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // Disable menu for splash screen (no settings or actions here)
        return false;
    }
}