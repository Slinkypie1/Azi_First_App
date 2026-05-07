package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

import android.os.Bundle; // מחזיק נתוני מצב של האקטיביטי
import android.util.Log; // לרישום הודעות ביומן (Log)
import android.view.View; // מחלקת הבסיס לרכיבי ממשק משתמש
import android.widget.Button; // רכיב כפתור
import android.widget.LinearLayout; // פריסה קווית

import androidx.activity.EdgeToEdge; // תמיכה בפריסה מקצה לקצה
import androidx.core.view.ViewCompat; // כלי עזר לתאימות תצוגות

import java.util.Set; // ממשק עבור קבוצת ערכים ייחודיים

/**
 * אקטיביטי TrophyRoom
 * --------------------
 * מציג את כל ההישגים (הגביעים) של השחקן.
 * כל גביע מוצג כפתוח (גלוי לחלוטין) או נעול (שקוף למחצה)
 * בהתאם למה שמאוחסן ב-ProgressStorage.
 */
public class TrophyRoom extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // הפעלת פריסת מסך מלא מקצה לקצה
        EdgeToEdge.enable(this);

        // הגדרת הפריסה עבור מסך זה
        setContentView(R.layout.activity_trophy_room);

        // אתחול רכיבי ממשק המשתמש והלוגיקה
        initViews();

        // טיפול בשולי חלון המערכת (שורת מצב / שורת ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            return insets; // החזרת השוליים ללא שינוי
        });
    }

    @Override
    protected void onResume() {
        super.onResume(); // המשך מחזור החיים

        // BaseMenuActivity מטפלת בעדכוני מראה (ערכת נושא, צבעים וכו')
        // אין צורך בלוגיקה נוספת כאן
    }

    /**
     * מאתחל רכיבי ממשק משתמש ומעדכן את נראות הגביעים
     * בהתאם להישגים שנפתחו.
     */
    private void initViews() {

        // כפתור חזרה מחזיר למסך הקודם
        Button btnBack = findViewById(R.id.btnBackFromTrophy);
        btnBack.setOnClickListener(v -> finish());

        // קבלת כל ההישגים שהושגו מהאחסון
        Set<String> earned = ProgressStorage.getEarnedAchievements(this);
        Log.d("TrophyRoom", "Earned Achievements: " + earned.toString());

        // גביע Speed Demon (שד המהירות)
        if (earned.contains(ProgressStorage.ACHIEV_SPEED_DEMON)) {
            findViewById(R.id.layoutSpeedDemon).setAlpha(1.0f);
        }

        // גביע Perfectionist (פרפקציוניסט)
        if (earned.contains(ProgressStorage.ACHIEV_PERFECTIONIST)) {
            findViewById(R.id.layoutPerfectionist).setAlpha(1.0f);
        }

        // גביע World Traveler (מטייל עולמי)
        if (earned.contains(ProgressStorage.ACHIEV_WORLD_TRAVELER)) {
            findViewById(R.id.layoutWorldTraveler).setAlpha(1.0f);
        }

        // גביע Ranked (מדורג)
        if (earned.contains(ProgressStorage.ACHIEV_RANKED)) {
            findViewById(R.id.layoutRanked).setAlpha(1.0f);
        }

        // גביע Top 10 (עשרת הגדולים)
        if (earned.contains(ProgressStorage.ACHIEV_TOP_10)) {
            findViewById(R.id.layoutTop10).setAlpha(1.0f);
        }
    }
}