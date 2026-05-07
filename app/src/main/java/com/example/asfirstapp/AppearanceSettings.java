package com.example.asfirstapp; // מגדיר את החבילה של האפליקציה

import android.content.Intent; // משמש למעבר בין מסכים (אקטיביטיז)
import android.content.SharedPreferences; // משמש לשמירת נתונים קבועים קטנים באופן מקומי
import android.graphics.Color; // מאפשר עבודה עם צבעים (לא בשימוש ישיר בקובץ זה כרגע)
import android.os.Bundle; // משמש להעברת נתונים לתוך onCreate()
import android.view.View; // מחלקת הבסיס לכל רכיבי ממשק המשתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש
import android.widget.RadioButton; // כפתור בחירה בודד
import android.widget.RadioGroup; // קבוצת כפתורי בחירה (רק אחד ניתן לבחירה)
import android.widget.TextView; // רכיב להצגת טקסט (לא בשימוש כאן)
import android.widget.Toast; // הודעה קופצת קטנה

import androidx.activity.EdgeToEdge; // מאפשר רינדור פריסה מקצה לקצה (Edge-to-Edge)
import androidx.core.graphics.Insets; // מטפל בשולי חלון המערכת (שורת מצב/ניווט)
import androidx.core.view.ViewCompat; // עוזר להחיל תכונות תאימות על תצוגות
import androidx.core.view.WindowInsetsCompat; // מספק מידע על שולי סרגלי המערכת

public class AppearanceSettings extends BaseMenuActivity {
    // מסך זה יורש מ-BaseMenuActivity (פונקציונליות תפריט משותפת)

    private RadioGroup rgBgColor; // קבוצה עבור אפשרויות צבע הרקע
    private RadioButton rbWhite, rbBlack; // שתי אפשרויות צבע לבחירה
    private Button btnSaveAppearance; // כפתור לשמירת המראה הנבחר
    private SharedPreferences sharedPreferences;
    // אחסון לשמירת העדפות משתמש באופן מקומי במכשיר

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // נקרא כאשר מסך זה נוצר

        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה
        EdgeToEdge.enable(this); // הפעלת ממשק משתמש מלא מקצה לקצה
        setContentView(R.layout.activity_appearance_settings);
        // קישור קובץ ה-Java הזה לפריסת ה-XML שלו

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // התאמת הפריסה כאשר סרגלי המערכת (מצב/ניווט) חופפים לממשק המשתמש

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // קבלת גודל סרגלי המערכת

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // החלת ריפוח (padding) כדי שממשק המשתמש לא יחפוף לסרגלי המערכת

            return insets; // החזרת השוליים המעודכנים
        });

        sharedPreferences = ProgressStorage.getAppPrefs(this);
        // יצירת/פתיחת קובץ אחסון פרטי עבור המשתמש הנוכחי

        rgBgColor = findViewById(R.id.rgBgColor); // קישור קבוצת הרדיו מה-XML
        rbWhite = findViewById(R.id.rbWhite); // קישור אפשרות "לבן"
        rbBlack = findViewById(R.id.rbBlack); // קישור אפשרות "שחור"
        btnSaveAppearance = findViewById(R.id.btnSaveAppearance); // כפתור שמירה
        Button btnBackToSecond = findViewById(R.id.btnBackToSecond);
        // כפתור לחזרה למסך הקודם

        // טעינת הגדרה קיימת
        String bgColor = sharedPreferences.getString("bg_color", "white");
        // קריאת צבע הרקע השמור (ברירת מחדל = לבן)

        if (bgColor.equals("black")) {
            // אם הערך השמור הוא שחור
            rbBlack.setChecked(true); // בחירת אפשרות שחור
        } else {
            rbWhite.setChecked(true); // אחרת בחירת אפשרות לבן
        }

        btnSaveAppearance.setOnClickListener(v -> {
            // רץ כאשר המשתמש לוחץ על כפתור השמירה

            SharedPreferences.Editor editor = sharedPreferences.edit();
            // פתיחת עורך לשינוי ההעדפות המאוחסנות

            String chosenColor;
            // משתנה לאחסון הצבע הנבחר

            if (rbBlack.isChecked()) {
                chosenColor = "black"; // המשתמש בחר שחור
            } else {
                chosenColor = "white"; // אחרת לבן
            }

            editor.putString("bg_color", chosenColor);
            // שמירת הצבע הנבחר תחת המפתח "bg_color"

            editor.apply();
            // ביצוע השינויים בצורה אסינכרונית

            // סנכרון ל-Firebase
            ProgressStorage.syncAppearanceToFirebase(this, chosenColor);
            // שליחת המראה הנבחר למסד הנתונים Firebase (סנכרון ענן)

            Toast.makeText(this, "Appearance Saved", Toast.LENGTH_SHORT).show();
            // הצגת הודעת אישור קופצת

            recreate();
            // הפעלה מחדש של האקטיביטי כדי להחיל את השינויים מיד
        });

        btnBackToSecond.setOnClickListener(v -> {
            // רץ כאשר כפתור החזור נלחץ

            Intent intent = new Intent(AppearanceSettings.this, Second.class);
            // יצירת אינטנט למעבר למסך השני (Second)

            startActivity(intent); // פתיחת האקטיביטי השנייה

            finish(); // סגירת המסך הנוכחי כדי שהמשתמש לא יוכל לחזור אליו
        });
    }
}
