package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

// ייבוא עבור ניהול אקטיביטי, ממשק משתמש ופריסות מודרניות
import android.content.Intent;           // ניווט בין מסכים
import android.os.Bundle;                // מאחסן את מצב האקטיביטי
import android.view.View;                // מחלקת בסיס לרכיבי ממשק משתמש
import android.widget.Button;            // מייצג כפתורים לחיצים
import android.widget.TextView;          // מציג טקסט

import androidx.activity.EdgeToEdge;           // מאפשר פריסת מסך מלא (קצה לקצה)
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטיז עם תאימות לאחור
import androidx.core.view.ViewCompat;          // כלי עזר עבור שולי תצוגה

/**
 * אקטיביטי ThirdQuestion
 * ----------------------
 * מציג את שאלת החידון השלישית (רב-ברירה) עם ארבעה כפתורי תשובה.
 * מטפל בלחיצות על כפתורים כדי לנווט למסך התשובה הנכונה או למסך הכישלון.
 */
public class ThirdQuestion extends BaseMenuActivity implements View.OnClickListener {

    // רכיבי ממשק משתמש
    private TextView TV3;       // מציג את טקסט השאלה
    private Button BtClick11;   // אפשרות תשובה 1
    private Button BtClick12;   // אפשרות תשובה 2
    private Button BtClick13;   // אפשרות תשובה 3 (נכונה)
    private Button BtClick14;   // אפשרות תשובה 4

    private long startTime;     // מתעד את הזמן שבו השאלה מוצגת

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה

        // הפעלת פריסת מסך מלא מקצה לקצה
        EdgeToEdge.enable(this);

        // טעינת קובץ ה-XML של הפריסה עבור אקטיביטי זו
        setContentView(R.layout.activity_third_question);

        // הפעלת מוזיקת רקע עבור שלב 3 (מסך ThirdQuestion)
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.third_question_music);
        startService(serviceIntent);

        // תיעוד זמן ההתחלה כדי למדוד כמה זמן לוקח למשתמש לענות
        startTime = System.currentTimeMillis();

        // הגדרת מאזין לשולי החלון כדי להבטיח שממשק המשתמש מאותחל כראוי לאחר ציור הפריסה
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            initViews();   // אתחול רכיבי ממשק המשתמש
            return insets; // שמירה על שולי המערכת ללא שינוי
        });
    }

    /**
     * מאתחל רכיבי ממשק משתמש ומגדיר מאזיני לחיצה לכל הכפתורים
     */
    private void initViews() {
        // קישור ה-TextView מפריסת ה-XML
        TV3 = findViewById(R.id.TV3);

        // קישור כפתורי התשובות מפריסת ה-XML
        BtClick11 = findViewById(R.id.BtClick11);
        BtClick12 = findViewById(R.id.BtClick12);
        BtClick13 = findViewById(R.id.BtClick13); // התשובה הנכונה
        BtClick14 = findViewById(R.id.BtClick14);

        // הגדרת מאזין לחיצה עבור כל הכפתורים
        BtClick11.setOnClickListener(this);
        BtClick12.setOnClickListener(this);
        BtClick13.setOnClickListener(this);
        BtClick14.setOnClickListener(this);
    }

    /**
     * מטפל באירועי לחיצה על כפתורים עבור כל אפשרויות התשובה
     */
    @Override
    public void onClick(View view) {
        if (view == BtClick13) {
            // נבחרה התשובה הנכונה

            // חישוב הזמן שלקח לענות על השאלה
            long timeTaken = System.currentTimeMillis() - startTime;

            // ניווט למסך ההצלחה והעברת הזמן
            Intent intent = new Intent(this, CorrectScreen3.class);
            intent.putExtra("TIME_TAKEN", timeTaken);
            startActivity(intent);

        } else {
            // נבחרה תשובה שגויה

            // ניווט למסך כישלון
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
        }
    }
}