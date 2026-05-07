package com.example.asfirstapp; // מגדיר את החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // משמש לניווט בין אקטיביטיז
import android.os.Bundle;      // מאחסן מידע על מצב האקטיביטי
import android.view.Menu;      // מייצג את תפריט האפשרויות
import android.view.MenuInflater; // הופך תפריט XML לאובייקטי Java
import android.view.MenuItem;  // מייצג פריט בודד בתפריט
import android.view.View;      // מחלקת בסיס לרכיבי ממשק משתמש
import android.view.ViewGroup; // מיכל עבור מספר תצוגות (Views)
import android.widget.RadioButton; // רכיב ממשק משתמש של כפתור בחירה (Radio Button)
import android.widget.TextView; // רכיב ממשק משתמש להצגת טקסט
import android.graphics.Color;  // משמש עבור ערכי צבע
import android.content.SharedPreferences; // מאחסן נתוני מפתח-ערך פשוטים
import android.widget.FrameLayout; // מכולת פריסה (Layout container)

import androidx.activity.OnBackPressedCallback; // מטפל בהתנהגות כפתור החזור
import androidx.annotation.Nullable; // מאפשר הערות לבדיקת null
import androidx.appcompat.app.AppCompatActivity; // אקטיביטי בסיס עם תמיכה ב-ActionBar

import java.util.concurrent.TimeUnit; // משמש לפעולות מבוססות זמן

import nl.dionsegijn.konfetti.core.Party; // מחלקת מסיבה עבור קונפטי
import nl.dionsegijn.konfetti.core.PartyFactory; // מפעל ליצירת מסיבות קונפטי
import nl.dionsegijn.konfetti.core.emitter.Emitter; // מגדיר פולט קונפטי
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig; // הגדרות עבור פולט הקונפטי
import nl.dionsegijn.konfetti.core.models.Shape; // צורות של קונפטי
import nl.dionsegijn.konfetti.core.models.Size; // גדלים של קונפטי
import nl.dionsegijn.konfetti.xml.KonfettiView; // תצוגת קונפטי ב-XML

// מחלקה מופשטת המשמשת כבסיס לכל האקטיביטיז מבוססות התפריט
public abstract class BaseMenuActivity extends AppCompatActivity {

    // נקרא כאשר האקטיביטי נוצרת לראשונה
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה ללוגיקת הגדרת ההורה

        // הסתרת טקסט ברירת המחדל של שורת הכותרת
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // דריסת התנהגות כפתור החזור
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog(); // הצגת דיאלוג אישור במקום יציאה מיידית
            }
        });
    }

    /**
     * מציג דיאלוג אישור לפני יציאה מהמסך
     */
    private void showExitConfirmationDialog() {
        String message;

        // התאמת ההודעה בהתאם לאקטיביטי הנוכחית
        if (this instanceof MainActivity) {
            message = "Are you sure you want to exit the app?";
        } else if (this instanceof Second) {
            message = "Are you sure you want to go back to the login screen?";
        } else if (this instanceof GameSettings || this instanceof AppearanceSettings) {
            message = "Are you sure you want to exit the settings? Your changes may not change";
        } else {
            message = "Are you sure you want to quit this level? Your current progress will be lost.";
        }

        // בנייה והצגה של דיאלוג התראה
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage(message)

                // התנהגות כפתור "כן"
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (this instanceof MainActivity) {
                        finishAffinity(); // סגירת כל האפליקציה
                    } else if (this instanceof Second) {
                        startActivity(new Intent(this, MainActivity.class)); // חזרה למסך הראשי
                        finish();
                    } else if (this instanceof GameSettings || this instanceof AppearanceSettings) {
                        finish(); // פשוט חזור אחורה
                    } else {
                        startActivity(new Intent(this, Second.class)); // חזרה למסך הבית של המשחק
                        finish();
                    }
                })

                // כפתור "לא" לא עושה כלום (הדיאלוג נסגר)
                .setNegativeButton("No", null)
                .show();
    }

    // יוצר את תפריט האפשרויות בפינה הימנית העליונה
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); // קבלת מנפח תפריטים
        inflater.inflate(R.menu.level_select, menu); // טעינת פריסת התפריט מה-XML
        return true; // הצגת התפריט
    }

    // נקרא בכל פעם שהתפריט מוצג (רענון מצב)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // קבלת כפתור הפעלה/השתקה של המוזיקה
        MenuItem musicItem = menu.findItem(R.id.music_toggle);

        if (musicItem != null) {
            // בדיקת מצב השתקה שמור
            boolean isMuted = ProgressStorage.getAppPrefs(this)
                    .getBoolean("music_muted", false);

            // עדכון האייקון בהתאם למצב
            musicItem.setIcon(isMuted ? R.drawable.music_off : R.drawable.music_on);
        }

        // הסתרת התפריט לחלוטין במסך הכניסה
        if (this instanceof MainActivity) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
            return true;
        }

        // קבלת השלב הגבוה ביותר שנפתח
        int highest = ProgressStorage.getHighestUnlockedLevel(this);

        SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
        String mode = prefs.getString("game_mode", "casual");

        boolean isTimedMode = mode.equals("timed"); // בדיקה אם מצב מתוזמן
        boolean isSecondActivity = this instanceof Second; // בדיקה אם זהו מסך הבית
        boolean showLevels = isSecondActivity && !isTimedMode; // הצגת שלבים רק אם מותר

        // לולאה על כל השלבים
        for (int level = 1; level <= 9; level++) {

            // המרת מספר השלב למזהה תפריט (level_1, level_2 וכו')
            int resId = getResources().getIdentifier(
                    "level_" + level,
                    "id",
                    getPackageName()
            );

            MenuItem item = menu.findItem(resId); // קבלת פריט התפריט

            if (item != null) {

                // הסתרת שלבים אם לא מותר
                if (!showLevels) {
                    item.setVisible(false);
                    continue;
                }

                // לוגיקת פתיחה
                if (level <= highest) {
                    item.setEnabled(true); // ניתן ללחיצה
                    item.setIcon(R.drawable.ic_unlock); // אייקון פתוח
                } else {
                    item.setEnabled(false); // נעול
                    item.setIcon(R.drawable.ic_lock); // אייקון מנעול
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // הפעלת שלב (פונקציית דמה)
    public void startLevel(int level) {
        Intent intent = new Intent(this, Third.class); // דוגמה למסך שלב
        startActivity(intent);
    }

    // טיפול בלחיצות על התפריט
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId(); // קבלת ה-ID של הפריט שנלחץ

        // תפריט ראשי
        if (id == R.id.main_menu) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        // החלפת מצב מוזיקה
        if (id == R.id.music_toggle) {

            SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
            boolean isMuted = prefs.getBoolean("music_muted", false);
            boolean newMuted = !isMuted;

            prefs.edit().putBoolean("music_muted", newMuted).apply(); // שמירת המצב

            ProgressStorage.syncMusicToFirebase(this, newMuted); // סנכרון לענן

            Intent serviceIntent = new Intent(this, MusicService.class);

            if (newMuted) {
                stopService(serviceIntent); // הפסקת מוזיקה
            } else {
                startService(serviceIntent); // הפעלת מוזיקה
            }

            invalidateOptionsMenu(); // רענון האייקון
            return true;
        }

        // ניווט לשלבים
        if (id == R.id.level_1) { startActivity(new Intent(this, Third.class)); return true; }
        if (id == R.id.level_2) { startActivity(new Intent(this, SecondQuestion.class)); return true; }
        if (id == R.id.level_3) { startActivity(new Intent(this, ThirdQuestion.class)); return true; }
        if (id == R.id.level_4) { startActivity(new Intent(this, Puzzle1.class)); return true; }
        if (id == R.id.level_5) { startActivity(new Intent(this, Puzzle2.class)); return true; }
        if (id == R.id.level_6) { startActivity(new Intent(this, Puzzle3.class)); return true; }
        if (id == R.id.level_7) { startActivity(new Intent(this, FillTheBlanks.class)); return true; }
        if (id == R.id.level_8) { startActivity(new Intent(this, FindTheCountry.class)); return true; }
        if (id == R.id.level_9) { startActivity(new Intent(this, UnlockCityActivity.class)); return true; }

        return super.onOptionsItemSelected(item);
    }

    // כאשר האקטיביטי חוזרת לקדמה
    @Override
    protected void onResume() {
        super.onResume(); // המשך מחזור החיים

        invalidateOptionsMenu(); // רענון מצב התפריט
        applyAppearance(); // החלת ערכת נושא/צבעים

        // הפעלת קונפטי אוטומטית במסכי הצלחה
        if (this.getClass().getSimpleName().contains("Correct") ||
                this.getClass().getSimpleName().contains("Finish") ||
                this instanceof FinalScore) {
            triggerConfetti();
        }
    }

    // מציג אנימציית קונפטי
    public void triggerConfetti() {

        View rootView = findViewById(android.R.id.content);
        if (!(rootView instanceof ViewGroup)) return;

        ViewGroup rootGroup = (ViewGroup) rootView;

        KonfettiView konfettiView = null;

        // חיפוש תצוגת קונפטי קיימת
        for (int i = 0; i < rootGroup.getChildCount(); i++) {
            if (rootGroup.getChildAt(i) instanceof KonfettiView) {
                konfettiView = (KonfettiView) rootGroup.getChildAt(i);
                break;
            }
        }

        // יצירה אם חסרה
        if (konfettiView == null) {
            konfettiView = new KonfettiView(this);
            konfettiView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootGroup.addView(konfettiView);
        }

        // הגדרות קונפטי
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(30);

        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .shapes(new Shape.Rectangle(0.2f), Shape.Circle.INSTANCE)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0)
                .build();

        konfettiView.start(party);
    }

    // החלת צבעי ערכת הנושא
    private void applyAppearance() {

        SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
        String bgColor = prefs.getString("bg_color", "white");

        int backgroundColor = bgColor.equals("black") ? Color.BLACK : Color.WHITE;
        int textColor = bgColor.equals("black") ? Color.WHITE : Color.BLACK;

        getWindow().getDecorView().setBackgroundColor(backgroundColor);

        View rootView = findViewById(android.R.id.content);

        if (rootView != null) {
            applyColorsRecursively(rootView, backgroundColor, textColor, true);
        }
    }

    // החלת צבעים באופן רקורסיבי על כל התצוגות
    private void applyColorsRecursively(View view, int bgColor, int textColor, boolean isRoot) {

        if (isRoot) {
            view.setBackgroundColor(bgColor);
        } else if (view instanceof ViewGroup && !(view instanceof android.widget.AdapterView)) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        if (view instanceof TextView) {
            TextView tv = (TextView) view;

            if (!(view instanceof android.widget.Button) || bgColor == Color.BLACK) {
                tv.setTextColor(textColor);
            }

            if (view instanceof RadioButton) {
                ((RadioButton) view).setButtonTintList(
                        android.content.res.ColorStateList.valueOf(textColor));
            }
        }

        if (view instanceof android.widget.ProgressBar) {
            ((android.widget.ProgressBar) view)
                    .setIndeterminateTintList(
                            android.content.res.ColorStateList.valueOf(textColor));
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                applyColorsRecursively(group.getChildAt(i), bgColor, textColor, false);
            }
        }
    }
}