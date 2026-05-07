package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

// ייבוא עבור ממשק משתמש של אנדרואיד, ניהול אקטיביטי ופריסות מודרניות
import android.content.Intent;           // משמש לניווט למסכים אחרים
import android.os.Bundle;                // מאחסן את מצב האקטיביטי
import android.view.View;                // מחלקת בסיס לרכיבי ממשק משתמש וטיפול בלחיצות
import android.widget.Button;            // מייצג כפתורים לחיצים
import android.widget.TextView;          // מציג טקסט

import androidx.activity.EdgeToEdge;           // מאפשר פריסות מסך מלא מודרניות
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטיז עם תאימות לאחור
import androidx.core.view.ViewCompat;          // כלי עזר עבור שולי תצוגה וריפוח

/**
 * אקטיביטי SecondQuestion
 * -----------------------
 * זהו מסך חידון רב-ברירה (שאלה 2).
 * המשתמש בוחר אחת מתוך ארבע תשובות.
 * אחת נכונה ומובילה למסך הצלחה,
 * האחרות מובילות למסך כישלון.
 */
public class SecondQuestion extends BaseMenuActivity implements View.OnClickListener {

    // רכיבי ממשק משתמש
    private TextView TV2;       // מציג את טקסט השאלה על המסך
    private Button BtClick6;    // אפשרות תשובה 1
    private Button BtClick7;    // אפשרות תשובה 2 (התשובה הנכונה)
    private Button BtClick8;    // אפשרות תשובה 3
    private Button BtClick9;    // אפשרות תשובה 4

    // משתנה תזמון למדידת המהירות שבה המשתמש ענה
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה

        // הפעלת פריסת "קצה לקצה" (התוכן נמשך אל מאחורי סרגלי המערכת)
        EdgeToEdge.enable(this);

        // טעינת קובץ ה-XML של הפריסה עבור מסך זה
        setContentView(R.layout.activity_second_question);

        // הפעלת מוזיקת רקע עבור שלב זה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.second_question_music);
        startService(serviceIntent);

        // תיעוד מתי השאלה הזו התחילה (משמש למעקב אחר ניקוד/זמן)
        startTime = System.currentTimeMillis();

        // חיבור אתחול ממשק המשתמש לאחר שפריסת התצוגה מוכנה
        // זה מבטיח ש-findViewById יעבוד בצורה נכונה עם כל היררכיית הפריסה
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            // אתחול כל הכפתורים ותצוגות הטקסט
            initViews();

            // החזרת שולי המערכת ללא שינוי
            return insets;
        });
    }

    /**
     * מחבר את רכיבי ממשק המשתמש מה-XML למשתני ה-Java
     * ומגדיר מאזיני לחיצה לכל כפתורי התשובות
     */
    private void initViews() {

        // קישור תצוגת טקסט השאלה
        TV2 = findViewById(R.id.TV2);

        // קישור כפתורי התשובות
        BtClick6 = findViewById(R.id.BtClick6);
        BtClick7 = findViewById(R.id.BtClick7);
        BtClick8 = findViewById(R.id.BtClick8);
        BtClick9 = findViewById(R.id.BtClick9);

        // הגדרת מאזין לחיצה עבור כל הכפתורים
        BtClick6.setOnClickListener(this);
        BtClick7.setOnClickListener(this);
        BtClick8.setOnClickListener(this);
        BtClick9.setOnClickListener(this);
    }

    /**
     * מטפל בלחיצות על כפתורים עבור כל אפשרויות התשובה
     */
    @Override
    public void onClick(View view) {

        // אם נבחרה התשובה הנכונה
        if(view == BtClick7){

            // חישוב הזמן שלקח לענות על השאלה
            long timeTaken = System.currentTimeMillis() - startTime;

            // מעבר למסך ההצלחה
            Intent intent = new Intent(this, CorrectScreen2.class);

            // העברת הזמן שלקח למסך הבא
            intent.putExtra("TIME_TAKEN", timeTaken);

            // הפעלת אקטיביטי ההצלחה
            startActivity(intent);

        } else {
            // נבחרה תשובה שגויה ← מעבר למסך כישלון
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
        }
    }
}