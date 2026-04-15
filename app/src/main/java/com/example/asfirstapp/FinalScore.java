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
    private Button btnViewRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_final_score);

        // Start background music for Final Score Screen
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.final_score_music);
        startService(serviceIntent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalTime = findViewById(R.id.tvTotalTime);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnViewRanking = findViewById(R.id.btnViewRanking);

        displayAndSaveFinalTime();

        btnBackToMenu.setOnClickListener(v -> {
            // Re-lock all levels by setting the highest unlocked level to 1
            ProgressStorage.setHighestUnlockedLevel(FinalScore.this, 1);

            // Navigate back to MainActivity
            Intent intent = new Intent(FinalScore.this, MainActivity.class);
            // Clear activity stack so the user can't "go back" to the score screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        if (btnViewRanking != null) {
            btnViewRanking.setOnClickListener(v -> {
                Intent intent = new Intent(FinalScore.this, Ranking.class);
                startActivity(intent);
            });
        }
    }

    private void displayAndSaveFinalTime() {
        long startTime = ProgressStorage.getGameStartTime(this);
        long pausedTime = ProgressStorage.getTotalPausedTime(this);
        
        if (startTime == 0) {
            tvTotalTime.setText("Total Time: N/A");
            return;
        }

        long endTime = System.currentTimeMillis();
        long rawDurationMillis = endTime - startTime;
        
        // Subtract the time spent reading instructions
        long finalDurationMillis = rawDurationMillis - pausedTime;
        
        // Ensure we don't end up with a negative time (though unlikely)
        if (finalDurationMillis < 0) finalDurationMillis = 0;

        // Save to Firebase
        ProgressStorage.saveGameCompletion(this, finalDurationMillis);

        long seconds = (finalDurationMillis / 1000) % 60;
        long minutes = (finalDurationMillis / (1000 * 60)) % 60;
        long hours = (finalDurationMillis / (1000 * 60 * (long)60));

        String timeFormatted;
        if (hours > 0) {
            timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }

        tvTotalTime.setText("Total Time: " + timeFormatted);
    }
}
