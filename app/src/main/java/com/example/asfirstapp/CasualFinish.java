package com.example.asfirstapp; // שם החבילה עבור אקטיביטי זו

import android.content.Intent; // משמש לניווט בין אקטיביטיז
import android.os.Bundle; // מחזיק את מצב המופע השמור
import android.view.View; // מחלקת הבסיס לרכיבי ממשק משתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש

import androidx.activity.EdgeToEdge; // מאפשר פריסת ממשק משתמש מקצה לקצה
import androidx.core.view.ViewCompat; // משמש לטיפול בשולי החלון (Window Insets)

// מסך זה מוצג כאשר המשתמש מסיים את המצב הרגיל (Casual mode)
public class CasualFinish extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה

        EdgeToEdge.enable(this); // הפעלת פריסת מסך מלא מקצה לקצה

        setContentView(R.layout.activity_casual_finish); // טעינת קובץ ה-XML של הפריסה

        // בדיקת הישג: אם השחקן מעולם לא פגע בקיר
        if (!ProgressStorage.wasWallHit()) {
            ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_PERFECTIONIST);
        }

        // החלת טיפול בשולי חלון (ריפוח פריסה בטוח עבור סרגלי מערכת)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            initViews(); // אתחול כפתורים ואינטראקציות ממשק משתמש

            return insets; // החזרת השוליים ללא שינוי
        });
    }

    // מאתחל את כל הכפתורים והתנהגות הלחיצה
    private void initViews() {

        // מציאת כפתורי ממשק המשתמש מהפריסה
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        Button btnTryTimedMode = findViewById(R.id.btnTryTimedMode);

        // מטפל בלחיצה על כפתור חזרה לתפריט
        btnBackToMenu.setOnClickListener(v -> {

            // יצירת אינטנט לחזרה למסך הבית (הלובי)
            Intent intent = new Intent(CasualFinish.this, Second.class);

            startActivity(intent); // הפעלת האקטיביטי
            finish(); // סגירת המסך הנוכחי
        });

        // מטפל בלחיצה על כפתור ניסיון מצב מתוזמן
        btnTryTimedMode.setOnClickListener(v -> {

            // שמירת מצב המשחק כ-"timed" באחסון המקומי
            ProgressStorage.getAppPrefs(this)
                    .edit()
                    .putString("game_mode", "timed")
                    .apply();

            // סנכרון המצב המעודכן לאחסון הענן של Firebase
            ProgressStorage.syncGameModeToFirebase(this, "timed");

            // חזרה למסך הבית הראשי
            Intent intent = new Intent(CasualFinish.this, Second.class);

            startActivity(intent); // פתיחת הלובי
            finish(); // סגירת מסך הסיום
        });
    }
}