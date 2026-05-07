package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

// ייבוא עבור ניהול אקטיביטי, ממשק משתמש ופריסות מודרניות
import android.content.Intent;           // משמש לניווט בין מסכים
import android.os.Bundle;                // מאחסן את מצב האקטיביטי
import android.view.View;                // מחלקת בסיס לרכיבי ממשק משתמש וטיפול בלחיצות
import android.widget.Button;            // מייצג כפתורים לחיצים
import android.widget.TextView;          // מציג טקסט

import androidx.activity.EdgeToEdge;           // מאפשר פריסות מסך מלא מודרניות
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטיז עם תאימות לאחור
import androidx.core.view.ViewCompat;          // כלי עזר עבור שולי תצוגה וריפוח

/**
 * אקטיביטי Third
 * --------------
 * זהו מסך חידון רב-ברירה (שאלה 1).
 * המשתמש בוחר אחת מתוך ארבע תשובות אפשריות.
 * תשובה אחת נכונה ומובילה למסך הצלחה,
 * בעוד שכל האחרות מובילות למסך כישלון.
 */
public class Third extends BaseMenuActivity implements View.OnClickListener {

    // רכיבי ממשק משתמש
    private TextView TV1;       // מציג את טקסט השאלה
    private Button BtClick2;    // אפשרות תשובה 1
    private Button BtClick3;    // אפשרות תשובה 2 (התשובה הנכונה)
    private Button BtClick4;    // אפשרות תשובה 3
    private Button BtClick5;    // אפשרות תשובה 4

    // טיימר המשמש למעקב אחר הזמן שלקח למשתמש לענות
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // הפעלת פריסת מסך מלא מודרנית (קצה לקצה)
        EdgeToEdge.enable(this);

        // הגדרת קובץ ה-XML של הפריסה עבור מסך זה
        setContentView(R.layout.activity_third);

        // הפעלת מוזיקת רקע עבור שלב/מסך זה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.third_music);
        startService(serviceIntent);

        // תיעוד הזמן שבו מסך השאלה הוצג
        startTime = System.currentTimeMillis();

        // חיבור אתחול ממשק המשתמש לאחר החלת הפריסה במלואה
        // מבטיח שכל התצוגות קיימות לפני הקריאה ל-findViewById
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {

            // אתחול כל הכפתורים ותצוגות הטקסט
            initViews();

            // החזרת שולי המערכת ללא שינוי
            return insets;
        });
    }

    /**
     * מחבר רכיבי ממשק משתמש מה-XML למשתני ה-Java ומגדיר מאזיני לחיצה
     */
    private void initViews() {

        // קישור תצוגת טקסט השאלה
        TV1 = findViewById(R.id.TV1);

        // קישור כפתורי התשובות
        BtClick2 = findViewById(R.id.BtClick2);
        BtClick3 = findViewById(R.id.BtClick3); // התשובה הנכונה
        BtClick4 = findViewById(R.id.BtClick4);
        BtClick5 = findViewById(R.id.BtClick5);

        // הגדרת מאזין לחיצה עבור כל הכפתורים
        BtClick2.setOnClickListener(this);
        BtClick3.setOnClickListener(this);
        BtClick4.setOnClickListener(this);
        BtClick5.setOnClickListener(this);
    }

    /**
     * מטפל בלחיצות משתמש עבור כל אפשרויות התשובה
     */
    @Override
    public void onClick(View view) {

        // אם נבחרה התשובה הנכונה
        if (view == BtClick3) {

            // חישוב הזמן שלקח לענות נכונה
            long timeTaken = System.currentTimeMillis() - startTime;

            // ניווט למסך ההצלחה
            Intent intent = new Intent(this, CorrectScreen1.class);

            // העברת הזמן שלקח לאקטיביטי הבאה
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