package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class FinalScore extends AppCompatActivity {

    private TextView tvTotalTime;
    private Button btnBackToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_final_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalTime = findViewById(R.id.tvTotalTime);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        displayFinalTime();

        btnBackToMenu.setOnClickListener(v -> {
            // Return to the welcome screen (Second activity)
            Intent intent = new Intent(FinalScore.this, Second.class);
            // Clear activity stack so the user can't "go back" to the score screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayFinalTime() {
        // Retrieve the start time saved when the user started the first level
        long startTime = ProgressStorage.getGameStartTime(this);
        
        if (startTime == 0) {
            tvTotalTime.setText("Total Time: N/A");
            return;
        }

        // Calculate total elapsed time
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;

        // Convert milliseconds to hours, minutes, and seconds
        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long hours = (durationMillis / (1000 * 60 * 60));

        String timeFormatted;
        if (hours > 0) {
            timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }

        tvTotalTime.setText("Total Time: " + timeFormatted);
    }
}
