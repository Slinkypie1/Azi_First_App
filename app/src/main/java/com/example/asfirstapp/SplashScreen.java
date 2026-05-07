package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // משמש למעבר בין מסכים
import android.os.Bundle; // מחזיק נתוני מצב של האקטיביטי
import android.os.Handler; // לניהול פעולות מושהות
import android.os.Looper; // לניהול לולאת ההודעות של השרשור הראשי
import android.util.Log; // לרישום הודעות ביומן (Log)

import androidx.activity.EdgeToEdge; // תמיכה בפריסה מקצה לקצה

import com.google.firebase.auth.FirebaseAuth; // אימות Firebase
import com.google.firebase.firestore.FirebaseFirestore; // מסד הנתונים Firestore

/**
 * אקטיביטי SplashScreen
 * ----------------------
 * זהו המסך הראשון שמוצג כשהאפליקציה עולה.
 * הוא מאתחל את Firebase, מבצע התחברות שקטה (אם המשתמש כבר מחובר),
 * ועובר ל-MainActivity או ללובי אחרי השהיה קצרה.
 */
public class SplashScreen extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // הפעלת פריסת מסך מלא מודרנית
        EdgeToEdge.enable(this);

        // הגדרת פריסת מסך הפתיחה (לוגו / מסך טעינה)
        setContentView(R.layout.activity_splash_screen);

        // אתחול מופע Firestore מוקדם כדי ש-Firebase יהיה מוכן לשימוש
        FirebaseFirestore.getInstance();

        // קבלת מופע אימות Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // בדיקה האם המשתמש כבר מחובר
        final Intent intent;
        if (mAuth.getCurrentUser() != null) {
            // Already logged in, skip login screen
            intent = new Intent(SplashScreen.this, Second.class);
            Log.d("SPLASH", "User already logged in: " + mAuth.getCurrentUser().getEmail());
        } else {
            // לא מחובר, מעבר למסך הכניסה
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }

        // המתנה של 2 שניות לפני מעבר למסך הבא
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(intent); // הפעלת האקטיביטי הבאה
            finish(); // סגירת מסך הפתיחה
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // ביטול התפריט עבור מסך הפתיחה (אין כאן הגדרות או פעולות)
        return false;
    }
}