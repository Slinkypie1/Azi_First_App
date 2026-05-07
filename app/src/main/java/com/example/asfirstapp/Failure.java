package com.example.asfirstapp; // מרחב השמות של האפליקציה

import android.content.Intent; // משמש לניווט בין אקטיביטיז
import android.os.Bundle; // מאחסן מידע על מצב האקטיביטי
import android.view.View; // מחלקת הבסיס לרכיבי ממשק משתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש

import androidx.activity.EdgeToEdge; // מאפשר פריסת מסך מלא מקצה לקצה
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטי (לא בשימוש ישיר כאן כי BaseMenuActivity מרחיבה אותה)
import androidx.core.graphics.Insets; // מייצג את שולי סרגלי המערכת
import androidx.core.view.ViewCompat; // מטפל בתאימות עבור שינויי תצוגה
import androidx.core.view.WindowInsetsCompat; // מספק נתוני שולי חלון

// אקטיביטי המוצגת כאשר השחקן נכשל בשלב
public class Failure extends BaseMenuActivity implements View.OnClickListener {

    Button BtClickLose; // כפתור שמחזיר את השחקן לתפריט הראשי

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה

        EdgeToEdge.enable(this); // הפעלת פריסה מלאה מקצה לקצה

        setContentView(R.layout.activity_failure_screen); // טעינת פריסת מסך הכישלון

        // תיעוד שהשחקן נכשל (משמש להישגים כמו "פרפקציוניסט")
        ProgressStorage.recordWallHit();

        // הפעלת מוזיקת רקע של כישלון
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.failure_music);
        startService(serviceIntent);

        // טיפול בשולי ממשק המערכת (שורת מצב, שורת ניווט)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {

            initViews(); // אתחול רכיבי ממשק המשתמש

            return insets; // שמירה על שולי המערכת ללא שינוי
        });
    }

    // מאתחל רכיבי ממשק משתמש ומגדיר מאזינים ללחיצה
    private void initViews() {

        BtClickLose = findViewById(R.id.BtClickLose); // מציאת הכפתור בפריסה
        BtClickLose.setOnClickListener(this); // הגדרת מטפל בלחיצה
    }

    // מטפל באירועי לחיצה על כפתורים
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, Second.class);
        // יצירת אינטנט לחזרה למסך הבית הראשי של המשחק

        startActivity(intent); // הפעלת המסך הראשי
    }
}