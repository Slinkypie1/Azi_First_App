package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // משמש לניווט בין אקטיביטיז (מסכים)
import android.os.Bundle; // מחזיק נתוני מצב שמורים עבור מחזור החיים של האקטיביטי
import android.view.View; // מחלקת הבסיס לכל רכיבי ממשק המשתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש
import android.widget.TextView; // מציג טקסט על המסך

import androidx.activity.EdgeToEdge; // מאפשר פריסת מסך מלא מקצה לקצה
import androidx.core.view.ViewCompat; // מספק כלי עזר לתצוגה עם תאימות לאחור

import java.util.List; // מבנה רשימה עבור רשומות טבלת המובילים
import java.util.Map; // מבנה מפתח-ערך עבור נתוני טבלת המובילים

// מסך המוצג כאשר המשתמש מסיים את שלב 9 בהצלחה
public class CorrectScreen9 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick22; // כפתור שממשיך לאחר סיום שלב 9
    TextView leaderboardText; // מציג את תוצאות טבלת המובילים
    long timeTaken; // שומר את זמן הסיום עבור שלב 9

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה
        EdgeToEdge.enable(this); // הפעלת ממשק משתמש במסך מלא מקצה לקצה

        setContentView(R.layout.activity_correct_screen9); // טעינת קובץ ה-XML של הפריסה

        // הפעלת מוזיקת רקע עבור מסכי תשובה נכונה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // קבלת זמן הסיום מהמסך הקודם (ברירת מחדל = 0)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // החלת שולי מערכת ואתחול ממשק המשתמש
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // אתחול כפתורים ותצוגות טקסט

            unlockNextLevel(9); // פתיחת השלב הבא אם רלוונטי

            saveAndLoadLeaderboard(); // שמירת התקדמות + טעינת טבלת מובילים

            return insets; // החזרת שולי הפריסה ללא שינוי
        });
    }

    // שמירת זמן הסיום וטעינת טבלת המובילים עבור שלב 9
    private void saveAndLoadLeaderboard() {

        // קבלת מצב המשחק הנוכחי (רגיל או מתוזמן)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // שמירת זמן הסיום (משמש להתקדמות + הישגים)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 9, timeTaken);
        }

        // הסתרת טבלת המובילים במצב רגיל
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // הבאת נתוני טבלת המובילים עבור שלב 9
        ProgressStorage.getLeaderboard(this, 9, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n"); // כותרת טבלת המובילים
                int rank = 1; // מונה דירוג

                // לולאה על רשומות טבלת המובילים
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName"); // שם השחקן
                    long time = (long) entry.get("timeTakenMillis"); // זמן סיום

                    // עיצוב שורת טבלת המובילים
                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");

                    rank++; // הדירוג הבא
                }

                leaderboardText.setText(sb.toString()); // הצגת טבלת המובילים
            }

            @Override
            public void onError(Exception e) {
                // אם טעינת טבלת המובילים נכשלת
                leaderboardText.setText("Leaderboard unavailable");
            }
        });
    }

    // מאתחל רכיבי ממשק משתמש
    private void initViews() {

        BtClick22 = findViewById(R.id.BtClick22); // קישור הכפתור מה-XML
        BtClick22.setOnClickListener(this); // הגדרת מאזין ללחיצה

        leaderboardText = findViewById(R.id.leaderboardText); // קישור תצוגת הטקסט
    }

    // פותח את השלב הבא אם זהו השלב הגבוה ביותר שנפתח
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // מטפל בלחיצה על כפתור (לוגיקת ניווט סופית)
    @Override
    public void onClick(View view) {

        // בדיקת מצב משחק
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        Intent intent;

        // אם במצב מתוזמן ← הצגת מסך התוצאה הסופית
        if (mode.equals("timed")) {
            intent = new Intent(this, FinalScore.class);
        }
        // אם במצב רגיל ← הצגת מסך סיום רגיל
        else {
            intent = new Intent(this, CasualFinish.class);
        }

        startActivity(intent); // הפעלת המסך הבא
        finish(); // סגירת האקטיביטי הנוכחית
    }
}