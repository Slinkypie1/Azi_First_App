package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// ייבוא עבור ניווט, רכיבי ממשק משתמש וניהול מחזור החיים של אנדרואיד
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

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Ranking Activity
 * ----------------
 * מציג את טבלת המובילים של המשחק על ידי הבאת נתונים מ-Firebase דרך ProgressStorage.
 * מציג שחקנים מדורגים לפי זמן הסיום הכולל שלהם.
 */
public class Ranking extends BaseMenuActivity {

    // רכיבי ממשק משתמש
    private TextView leaderboardText;          // מציג את תוצאות טבלת המובילים
    private Button btnBackFromRanking;        // כפתור לחזרה למסך הקודם

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // הפעלת פריסת ממשק משתמש מקצה לקצה
        EdgeToEdge.enable(this);

        // הגדרת הפריסה עבור אקטיביטי זו
        setContentView(R.layout.activity_ranking);

        // הפעלת מוזיקת רקע עבור מסך הדירוג
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.ranking_music);
        startService(serviceIntent);

        // טיפול בשולי ממשק המערכת (שורת מצב, שורת ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קישור רכיבי ממשק משתמש
        leaderboardText = findViewById(R.id.leaderboardText);
        btnBackFromRanking = findViewById(R.id.btnBackFromRanking);

        // כפתור חזור שסוגר את האקטיביטי
        btnBackFromRanking.setOnClickListener(v -> finish());

        // טעינת נתוני טבלת המובילים מ-Firebase או מאחסון מקומי
        loadLeaderboard();
    }

    /**
     * מביא נתוני טבלת מובילים מ-ProgressStorage ומציג אותם.
     */
    private void loadLeaderboard() {

        ProgressStorage.getGameLeaderboard(this, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder(); // בונה את מחרוזת הדירוג
                int rank = 1; // מונה דירוג

                // בניית טקסט התצוגה של טבלת המובילים
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName");              // שם השחקן
                    Long timeMillis = (Long) entry.get("totalTimeMillis");     // זמן הסיום

                    if (name != null && timeMillis != null) {

                        // המרת מילישניות לפורמט קריא
                        String timeFormatted = formatTime(timeMillis);

                        // הוספת שורת דירוג מעוצבת
                        sb.append(rank).append(". ")
                                .append(name)
                                .append(" - ")
                                .append(timeFormatted)
                                .append("\n\n");

                        rank++; // העלאת הדירוג
                    }
                }

                // אם לא קיימות רשומות, הצגת הודעה זמנית
                if (sb.length() == 0) {
                    leaderboardText.setText("No rankings yet. Be the first to finish!");
                } else {
                    leaderboardText.setText(sb.toString());
                }
            }

            @Override
            public void onError(Exception e) {
                // הצגת הודעת שגיאה אם הבקשה ל-Firebase נכשלת
                leaderboardText.setText("Error loading leaderboard. Please try again later.");
            }
        });
    }

    /**
     * המרת מילישניות לפורמט זמן קריא.
     * דוגמה:
     * - 90,000 מילישניות ← 01:30
     * - 3,660,000 מילישניות ← 01:01:00
     */
    private String formatTime(long durationMillis) {

        long seconds = (durationMillis / 1000) % 60;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long hours = (durationMillis / (1000 * 60 * 60));

        if (hours > 0) {
            // פורמט שעות:דקות:שניות
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            // פורמט דקות:שניות
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }
}
