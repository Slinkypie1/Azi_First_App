package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // משמש למעבר בין אקטיביטיז (מסכים)
import android.os.Bundle;     // מחזיק נתוני מצב שמורים עבור מחזור החיים של האקטיביטי
import android.view.Menu;     // מייצג את תפריט האפשרויות
import android.view.MenuItem; // מייצג פריט בודד בתוך התפריט
import android.view.View;     // מחלקת הבסיס לכל רכיבי ממשק המשתמש
import android.widget.Button; // רכיב כפתור בממשק המשתמש
import android.widget.EditText; // שדה להזנת טקסט (לא בשימוש פעיל כאן)
import android.widget.TextView; // רכיב להצגת טקסט

import androidx.activity.EdgeToEdge; // מאפשר תמיכה בפריסת מסך מלא מקצה לקצה
import androidx.core.view.ViewCompat; // מספק כלי עזר לתאימות עבור תצוגות (Views)

/**
 * אקטיביטי Second
 * ----------------
 * זהו מסך הבית הראשי (הלובי) לאחר ההתחברות.
 * הוא מציג הודעת ברוך הבא, מאפשר למשתמש להתחיל את המשחק,
 * לפתוח הגדרות, או להחליף חשבון משתמש.
 */
public class Second extends BaseMenuActivity implements View.OnClickListener {

    // רכיבי ממשק משתמש
    private TextView TV;          // מציג הודעת ברוך הבא לשחקן
    private EditText ET;          // שדה קלט לשם השחקן (אופציונלי כרגע)
    private Button BtClick1;      // כפתור להתחלת שלב 1
    private Button BtSettings;    // כפתור לפתיחת מסך הגדרות המשחק
    private Button btnSwitchUser; // כפתור להתנתקות והחלפת משתמש

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה

        // הפעלת פריסת מסך מלא מודרנית (התוכן נמשך אל מאחורי סרגלי המערכת)
        EdgeToEdge.enable(this);

        // הגדרת קובץ ה-XML של הפריסה עבור אקטיביטי זו
        setContentView(R.layout.activity_second);

        // הפעלת מוזיקת רקע עבור מסך זה
        Intent serviceIntent = new Intent(this, MusicService.class);

        // העברת מזהה רצועת המוזיקה שיש להשמיע
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.second_music);

        // הפעלת שירות המוזיקה
        startService(serviceIntent);

        // אתחול כל רכיבי ממשק המשתמש והמאזינים
        initViews();

        // טיפול בשולי חלון (התאמה לשטחים בטוחים של סרגלי מערכת)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> insets);
    }

    @Override
    protected void onResume() {
        super.onResume(); // המשך מחזור החיים

        // רענון טקסט ברוך הבא בכל פעם שהאקטיביטי חוזרת לתצוגה
        updateNameDisplay();
    }

    /**
     * מאתחל רכיבי ממשק משתמש ומגדיר מאזינים ללחיצה
     */
    private void initViews() {

        // קישור התצוגות מה-XML למשתני ה-Java
        TV = findViewById(R.id.TV);
        ET = findViewById(R.id.ET);
        BtClick1 = findViewById(R.id.BtClick1);
        BtSettings = findViewById(R.id.BtSettings);
        btnSwitchUser = findViewById(R.id.btnSwitchUser);

        // הצגת שם השחקן השמור בטקסט ברוך הבא
        updateNameDisplay();

        // הגדרת מאזיני לחיצה עבור כל הכפתורים
        BtClick1.setOnClickListener(this);
        BtSettings.setOnClickListener(this);
        btnSwitchUser.setOnClickListener(this);
    }

    /**
     * מעדכן את טקסט ברוך הבא באמצעות שם השחקן השמור ב-SharedPreferences
     */
    private void updateNameDisplay() {

        // שליפת שם השחקן האחרון שנשמר
        String lastName = ProgressStorage.getAppPrefs(this)
                .getString("last_name", "Player"); // ערך ברירת מחדל אם לא קיים

        // עדכון ה-TextView עם הברכה
        TV.setText("Ready " + lastName + "?");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // הוספת כפתור הישגים לתפריט העליון
        MenuItem trophyItem = menu.add(Menu.NONE, 1002, 0, "Achievements");
        trophyItem.setIcon(R.drawable.achievements);
        trophyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // הוספת כפתור הגדרות לתפריט העליון
        MenuItem settingsItem = menu.add(Menu.NONE, 1001, 1, "Settings");
        settingsItem.setIcon(R.drawable.light_dark_mode);
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // טעינת פריטי תפריט הבסיס ממחלקת האב (בית, בחירת שלבים וכו')
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // פתיחת מסך הגדרות מראה
        if (item.getItemId() == 1001) {
            Intent intent = new Intent(this, AppearanceSettings.class);
            startActivity(intent);
            return true;
        }

        // פתיחת חדר הגביעים (מסך הישגים)
        if (item.getItemId() == 1002) {
            Intent intent = new Intent(this, TrophyRoom.class);
            startActivity(intent);
            return true;
        }

        // אפשור ל-BaseMenuActivity לטפל בשאר פעולות התפריט
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        // התחלת המשחק כאשר הכפתור הראשי נלחץ
        if (view.getId() == R.id.BtClick1) {

            // תיעוד זמן תחילת המשחק למעקב אחר זמן משחק כולל
            ProgressStorage.setGameStartTime(this, System.currentTimeMillis());

            // איפוס דגל המעקב אחר "ריצה מושלמת"
            ProgressStorage.resetPerfectionistFlag();

            // התחלת שלב 1
            startLevel(1);

        }
        // פתיחת מסך הגדרות משחק
        else if (view.getId() == R.id.BtSettings) {
            Intent intent = new Intent(this, GameSettings.class);
            startActivity(intent);

        }
        // החלפת משתמש / התנתקות
        else if (view.getId() == R.id.btnSwitchUser) {
            handleLogout();
        }
    }

    private void handleLogout() {

        // התנתקות מאימות Firebase
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

        // הסרת נתוני משתמש שמורים מהעדפות גלובליות (אופציונלי, שומר את המייל להתחברות הבאה)
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .remove("last_name")
                .remove("last_email")
                .apply();

        // חזרה למסך הכניסה וניקוי מחסנית האקטיביטיז
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}