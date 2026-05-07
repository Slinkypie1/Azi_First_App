package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

import android.content.SharedPreferences; // משמש לאחסון נתוני מפתח-ערך פשוטים באופן מקומי במכשיר
import android.os.Bundle; // מחזיק מידע על מצב שמור עבור מחזור החיים של האקטיביטי
import android.widget.Button; // רכיב ממשק משתמש עבור כפתורים לחיצים
import android.widget.RadioButton; // רכיב ממשק משתמש לבחירת אפשרות אחת מתוך קבוצה
import android.widget.RadioGroup; // מיכל המקבץ כפתורי בחירה (Radio Buttons) יחד
import android.widget.Toast; // משמש להצגת הודעות קופצות קצרות

import androidx.activity.EdgeToEdge; // מאפשר תמיכה בפריסת מסך מלא מקצה לקצה
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס עבור אקטיביטיז מודרניות באנדרואיד
import androidx.core.graphics.Insets; // מייצג את שולי סרגלי המערכת (מרווח עבור שורת מצב/ניווט)
import androidx.core.view.ViewCompat; // מספק כלי עזר לתאימות עבור תצוגות (Views)
import androidx.core.view.WindowInsetsCompat; // מטפל בשולי חלון המערכת עבור פריסות UI מודרניות

public class GameSettings extends BaseMenuActivity {

    private RadioGroup rgGameMode; // קבוצה המחזיקה את כפתורי הבחירה של מצב המשחק
    private RadioButton rbCasual, rbTimed; // כפתורי בחירה לבחירת מצב רגיל או מתוזמן
    private Button btnSaveSettings; // כפתור המשמש לשמירת ההגדרות שנבחרו
    private SharedPreferences sharedPreferences; // מאחסן הגדרות משתמש קבועות באופן מקומי

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה להגדרת אקטיביטי ההורה
        EdgeToEdge.enable(this); // הפעלת פריסת מסך מלא מקצה לקצה
        setContentView(R.layout.activity_game_settings); // טעינת פריסת ממשק המשתמש עבור מסך זה

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // קבלת המרווח של סרגלי המערכת (שורת מצב + שורת ניווט)

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // החלת ריפוח (padding) כך שממשק המשתמש לא יחפוף לסרגלי המערכת

            return insets;
        });

        sharedPreferences = ProgressStorage.getAppPrefs(this);
        // פתיחת האחסון המקומי עבור העדפות המשתמש הנוכחי

        rgGameMode = findViewById(R.id.rgGameMode); // קישור קבוצת הרדיו מקובץ ה-XML
        rbCasual = findViewById(R.id.rbCasual); // קישור כפתור הבחירה למצב רגיל
        rbTimed = findViewById(R.id.rbTimed); // קישור כפתור הבחירה למצב מתוזמן
        btnSaveSettings = findViewById(R.id.btnSaveSettings); // קישור כפתור השמירה

        // טעינת הגדרה קיימת
        String mode = sharedPreferences.getString("game_mode", "casual");
        // קריאת מצב המשחק השמור (ברירת מחדל היא "casual")

        if (mode.equals("timed")) {
            rbTimed.setChecked(true);
            // בחירת מצב מתוזמן אם נשמר בעבר
        } else {
            rbCasual.setChecked(true);
            // אחרת, ברירת המחדל היא מצב רגיל
        }

        btnSaveSettings.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // יצירת עורך לשינוי ההעדפות המאוחסנות

            String chosenMode;
            // משתנה לאחסון המצב שנבחר

            if (rbTimed.isChecked()) {
                chosenMode = "timed";
                // המשתמש בחר במצב מתוזמן
            } else {
                chosenMode = "casual";
                // המשתמש בחר במצב רגיל
            }

            editor.putString("game_mode", chosenMode);
            // שמירת המצב הנבחר באופן מקומי

            editor.apply();
            // החלת השינויים בצורה אסינכרונית

            // סנכרון ל-Firebase
            ProgressStorage.syncGameModeToFirebase(this, chosenMode);
            // שליחת המצב הנבחר לאחסון בענן

            Toast.makeText(this, "ההגדרות נשמרו", Toast.LENGTH_SHORT).show();
            // הצגת הודעת אישור

            finish();
            // סגירת מסך ההגדרות וחזרה למסך הקודם
        });
    }
}