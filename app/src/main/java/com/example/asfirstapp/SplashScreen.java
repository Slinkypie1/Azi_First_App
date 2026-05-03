package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        // Initialize Firebase early
        FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Perform silent sign-in if needed
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword("azriel.zev@gmail.com", "A'$Sc80ol@9p")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SPLASH", "Silent sign-in successful");
                        }
                    });
        }

        // Delay for 2 seconds to show the logo, then decide where to go
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close splash screen
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // No menu on splash screen
        return false;
    }
}
