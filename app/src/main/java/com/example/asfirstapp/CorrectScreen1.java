package com.example.asfirstapp; // שם החבילה עבור אקטיביטי זו

import android.content.Intent; // משמש לניווט בין מסכים
import android.os.Bundle; // מכיל נתוני מצב שמורים עבור מחזור החיים של האקטיביטי
import android.view.View; // מחלקת הבסיס לכל רכיבי ממשק המשתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש
import android.widget.TextView; // מציג טקסט על המסך
import android.widget.Toast; // מציג הודעות קופצות קטנות (לא בשימוש כאן)

import androidx.activity.EdgeToEdge; // מאפשר פריסת ממשק משתמש מקצה לקצה
import androidx.core.view.ViewCompat; // עוזר לטפל בשולי חלון בצורה בטוחה

import java.util.List; // מבנה נתונים של רשימה עבור רשומות טבלת המובילים
import java.util.Map; // זוגות מפתח-ערך עבור נתוני טבלת המובילים

// מסך המוצג כאשר השחקן עונה נכונה על שלב 1
public class CorrectScreen1 extends BaseMenuActivity implements View.OnClickListener {

    Button BtClick10; // כפתור שממשיך לשאלה הבאה
    TextView leaderboardText; // מציג את תוצאות טבלת המובילים
    long timeTaken; // שומר כמה זמן לקח לשחקן לסיים את השלב

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // קריאה להגדרת ההורה
        EdgeToEdge.enable(this); // הפעלת ממשק משתמש במסך מלא מקצה לקצה

        setContentView(R.layout.activity_correct_screen1); // טעינת קובץ ה-XML של הפריסה

        // הפעלת מוזיקת רקע עבור מסך תשובה נכונה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.correct_screens_music);
        startService(serviceIntent);

        // קבלת הזמן שלקח מהמסך הקודם (ברירת מחדל = 0 אם חסר)
        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);

        // החלת שולי חלון בטוחים ואתחול ממשק המשתמש
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // הגדרת כפתורים ותצוגות טקסט
            unlockNextLevel(1); // פתיחת השלב הבא אם יש צורך
            saveAndLoadLeaderboard(); // שמירת תוצאה + טעינת טבלת מובילים

            return insets; // החזרת השוליים ללא שינוי
        });
    }

    // שמירת התוצאה וטעינת טבלת המובילים מהאחסון/ענן
    private void saveAndLoadLeaderboard() {

        // קבלת מצב המשחק הנוכחי (רגיל או מתוזמן)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // שמירת זמן סיום השלב (מפעיל גם לוגיקת הישגים)
        if (timeTaken > 0) {
            ProgressStorage.saveLevelCompletion(this, 1, timeTaken);
        }

        // אם במצב רגיל ← הסתרת טבלת המובילים לחלוטין
        if (mode.equals("casual")) {
            if (leaderboardText != null) {
                leaderboardText.setVisibility(View.GONE);
            }
            return;
        }

        // טעינת נתוני טבלת המובילים בצורה אסינכרונית מהאחסון/שרת
        ProgressStorage.getLeaderboard(this, 1, new ProgressStorage.LeaderboardCallback() {

            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> entries) {

                StringBuilder sb = new StringBuilder("--- LEADERBOARD ---\n"); // כותרת טבלת המובילים
                int rank = 1; // מונה דירוג

                // לולאה על רשומות טבלת המובילים
                for (Map<String, Object> entry : entries) {

                    String name = (String) entry.get("userName"); // שם השחקן
                    long time = (long) entry.get("timeTakenMillis"); // זמן סיום

                    // עיצוב והוספת הרשומה
                    sb.append(rank).append(". ").append(name)
                            .append(": ").append(time / 1000.0).append("s\n");

                    rank++; // העלאת הדירוג
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

    // פתיחת השלב הבא אם השחקן סיים את השלב הגבוה ביותר הנוכחי
    private void unlockNextLevel(int currentLevel) {

        if (currentLevel == ProgressStorage.getHighestUnlockedLevel(this)) {
            ProgressStorage.setHighestUnlockedLevel(this, currentLevel + 1);
        }
    }

    // מוצא רכיבי ממשק משתמש ומחבר מאזינים
    private void initViews() {

        BtClick10 = findViewById(R.id.BtClick10); // קישור הכפתור מה-XML
        BtClick10.setOnClickListener(this); // הגדרת מאזין ללחיצה

        leaderboardText = findViewById(R.id.leaderboardText); // קישור תצוגת הטקסט
    }

    // מטפל באירועי לחיצה על כפתורים
    @Override
    public void onClick(View view) {

        // מעבר למסך השאלה הבא
        Intent intent = new Intent(this, SecondQuestion.class);

        startActivity(intent); // פתיחת האקטיביטי הבאה
    }
}