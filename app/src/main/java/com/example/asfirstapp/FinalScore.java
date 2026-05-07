package com.example.asfirstapp; // שם החבילה עבור מחלקה זו

import android.content.Intent; // משמש לניווט בין אקטיביטיז
import android.os.Bundle; // מחזיק נתוני מצב של האקטיביטי
import android.view.View; // מחלקת הבסיס לרכיבי ממשק משתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש
import android.widget.TextView; // רכיב להצגת טקסט

import androidx.activity.EdgeToEdge; // מאפשר פריסת מסך מלא מקצה לקצה
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטי (לא הכרחי כאן כי BaseMenuActivity מרחיבה אותה)
import androidx.core.graphics.Insets; // מייצג את שולי סרגלי המערכת
import androidx.core.view.ViewCompat; // מטפל בהתאמות תצוגה עם תאימות לאחור
import androidx.core.view.WindowInsetsCompat; // מספק מידע על שולי החלון

import java.util.Locale; // משמש לעיצוב מחרוזות זמן

// מסך המציג את הזמן הסופי הכולל לאחר סיום המשחק
public class FinalScore extends BaseMenuActivity {

    private TextView tvTotalTime; // מציג את זמן המשחק הכולל
    private Button btnBackToMenu; // כפתור לחזרה לתפריט הראשי
    private Button btnViewRanking; // כפתור לצפייה בדירוג/טבלת מובילים

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // קריאה להגדרת ההורה
        EdgeToEdge.enable(this); // הפעלת פריסה מלאה מקצה לקצה

        setContentView(R.layout.activity_final_score); // טעינת פריסת ממשק המשתמש

        // הפעלת מוזיקת רקע עבור מסך התוצאה הסופית
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.final_score_music);
        startService(serviceIntent);

        // החלת ריפוח עבור סרגלי מערכת (שורת מצב/ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        // קישור רכיבי ממשק המשתמש
        tvTotalTime = findViewById(R.id.tvTotalTime);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnViewRanking = findViewById(R.id.btnViewRanking);

        displayAndSaveFinalTime(); // חישוב והצגת הזמן הסופי

        // לוגיקה של כפתור חזרה לתפריט
        btnBackToMenu.setOnClickListener(v -> {

            // איפוס התקדמות (נועל את כל השלבים מחדש)
            ProgressStorage.setHighestUnlockedLevel(FinalScore.this, 1);

            // חזרה לתפריט הראשי
            Intent intent = new Intent(FinalScore.this, Second.class);

            // ניקוי מחסנית האקטיביטיז כדי שהמשתמש לא יוכל לחזור לכאן
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });

        // כפתור דירוג (רק אם הוא קיים בפריסה)
        if (btnViewRanking != null) {
            btnViewRanking.setOnClickListener(v -> {

                Intent intent = new Intent(FinalScore.this, Ranking.class);
                startActivity(intent);
            });
        }
    }

    // מחשב את זמן המשחק הכולל, מעצב אותו, שומר אותו ומציג אותו
    private void displayAndSaveFinalTime() {

        long startTime = ProgressStorage.getGameStartTime(this); // חותמת זמן תחילת המשחק
        long pausedTime = ProgressStorage.getTotalPausedTime(this); // זמן שהועבר בהשהיה

        // אם לא קיימת שעת התחלה, הצגת הודעת חסר
        if (startTime == 0) {
            tvTotalTime.setText("Total Time: N/A");
            return;
        }

        long endTime = System.currentTimeMillis(); // הזמן הנוכחי
        long rawDurationMillis = endTime - startTime; // הזמן הכולל שחלף

        // הסרת זמן השהיה/הוראות מהזמן הכולל
        long finalDurationMillis = rawDurationMillis - pausedTime;

        // מניעת ערכי זמן שליליים
        if (finalDurationMillis < 0) finalDurationMillis = 0;

        // שמירת זמן הסיום הסופי (ב-Firebase או באחסון)
        ProgressStorage.saveGameCompletion(this, finalDurationMillis);

        // הענקת הישג "Ranked" (סיום מתוזמן)
        ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_RANKED);

        // הענקת הישג "פרפקציוניסט" אם לא נעשו טעויות
        if (!ProgressStorage.wasWallHit()) {
            ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_PERFECTIONIST);
        }

        // המרת מילישניות לשעות, דקות, שניות
        long seconds = (finalDurationMillis / 1000) % 60;
        long minutes = (finalDurationMillis / (1000 * 60)) % 60;
        long hours = (finalDurationMillis / (1000 * 60 * 60));

        String timeFormatted;

        // עיצוב הזמן בהתאם לשאלה אם קיימות שעות
        if (hours > 0) {
            timeFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        // הצגת הזמן המעוצב הסופי
        tvTotalTime.setText("Total Time: " + timeFormatted);
    }
}