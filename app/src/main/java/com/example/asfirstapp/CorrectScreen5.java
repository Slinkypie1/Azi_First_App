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

// מסך המוצג כאשר המשתמש מסיים את שלב 5 בהצלחה
public class CorrectScreen5 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick18; // כפתור שממשיך לשלב 6
    TextView leaderboardText; // מציג את תוצאות טבלת המובילים
    long timeTaken; // שומר את זמן הסיום עבור שלב 5

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה
        EdgeToEdge.enable(this); // הפעלת ממשק משתמש במסך מלא מקצה לקצה

        setContentView(R.layout.activity_correct_screen5); // טעינת קובץ ה-XML של הפריסה

        // הפעלת מוזיקת רקע עבור מסכי תשובה נכונה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // קבלת הזמן שלקח מהאקטיביטי הקודמת (ברירת מחדל = 0 אם חסר)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // החלת שולי חלון ואתחול רכיבי ממשק המשתמש
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // הגדרת רכיבי ממשק המשתמש
            unlockNextLevel(5); // פתיחת השלב הבא אם יש צורך
            saveAndLoadLeaderboard(); // שמירת התקדמות + טעינת טבלת מובילים

            return insets; // החזרת השוליים ללא שינוי
        });
    }

    // שמירת זמן הסיום וטעינת טבלת המובילים עבור שלב 5
    private void saveAndLoadLeaderboard() {

        // קבלת מצב המשחק הנוכחי (רגיל או מתוזמן)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // שמירת זמן סיום השלב (משמש גם למעקב אחר הישגים)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 5, timeTaken);
        }

        // הסתרת טבלת המובילים במצב רגיל
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // הבאת נתוני טבלת המובילים עבור שלב 5
        ProgressStorage.getLeaderboard(this, 5, new ProgressStorage.LeaderboardCallback() {

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

    // פותח את השלב הבא אם זהו השלב הגבוה ביותר שנפתח
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // מאתחל רכיבי ממשק משתמש
    private void initViews() {

        BtClick18 = findViewById(R.id.BtClick18); // קישור הכפתור מה-XML
        BtClick18.setOnClickListener(this); // הגדרת מאזין ללחיצה

        leaderboardText = findViewById(R.id.leaderboardText); // קישור תצוגת הטקסט
    }

    // מטפל באירועי לחיצה על כפתורים
    @Override
    public void onClick(View view) {

        // מעבר למסך הפאזל של שלב 6
        Intent intent = new Intent(this, Puzzle3.class);

        startActivity(intent); // פתיחת האקטיביטי הבאה
    }
}