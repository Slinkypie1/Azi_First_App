package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// ייבוא למעקב אחר מצב האפליקציה ושמירת נתונים קבועים
import android.app.Activity;            // למעקב אחר אירועים של אקטיביטי בודדת
import android.app.Application;         // מחלקת בסיס ללוגיקה גלובלית ברמת האפליקציה
import android.content.Context;         // מספק גישה למשאבי מערכת
import android.content.Intent;          // נוסף להפעלת שירותים (services)
import android.content.SharedPreferences; // לאחסון נתונים קבועים פשוטים
import android.os.Bundle;               // בשימוש בשיטות מחזור חיים של אקטיביטי
import android.util.Log;                // לרישום (Log) של מידע לניפוי שגיאות (debug)

/**
 * MyApp מרחיבה את Application כדי לעקוב אחר מצב האפליקציה כולה.
 * היא מנטרת האם האפליקציה נמצאת בקדמה (foreground) או ברקע (background).
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp"; // תגית לרישום ביומן (Log)
    private static final String APP_STATE_PREF = "AppStatePref"; // שם קובץ ה-SharedPreferences
    private static final String IS_APP_RUNNING = "IsAppRunning"; // מפתח עבור מצב הריצה בקדמה

    private int activityCount = 0; // עוקב אחר מספר האקטיביטיז המוצגות כעת

    @Override
    public void onCreate() {
        super.onCreate(); // קריאה לשיטת ההורה

        // רישום מאזינים למחזור חיים כדי לנטר את כל האקטיביטיז באופן גלובלי
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // נקרא כאשר אקטיביטי נוצרת. לא בשימוש כאן.
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activityCount == 0) {
                    // האפליקציה עברה לקדמה מהרקע
                    Intent serviceIntent = new Intent(activity, MusicService.class);
                    serviceIntent.setAction("ACTION_RESUME");
                    activity.startService(serviceIntent);
                    // חידוש המוזיקה כשהאפליקציה חוזרת לקדמה

                    Log.d(TAG, "App moved to foreground."); // רישום הודעה שהאפליקציה עברה לקדמה
                }

                activityCount++; // העלאת המונה כשאקטיביטי הופכת לנראית
                updateAppState(true); // עדכון שהאפליקציה נמצאת בקדמה
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--; // הורדת המונה כשאקטיביטי כבר לא נראית

                if (activityCount == 0) {
                    // אין אקטיביטיז נראות ← האפליקציה ברקע
                    Intent serviceIntent = new Intent(activity, MusicService.class);
                    serviceIntent.setAction("ACTION_PAUSE");
                    activity.startService(serviceIntent);
                    // השהיית המוזיקה כשהאפליקציה עוברת לרקע

                    updateAppState(false); // עדכון שהאפליקציה ברקע
                    Log.d(TAG, "App moved to background."); // רישום הודעה שהאפליקציה עברה לרקע
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activityCount == 0) {
                    // אופציונלי: האפליקציה נסגרה לחלוטין אם האקטיביטי האחרונה הושמדה
                    Log.d(TAG, "App is fully closed.");
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // נקרא כשאקטיביטי מתחדשת; לא בשימוש כאן
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // נקרא כשאקטיביטי מושהית; לא בשימוש כאן
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                // נקרא כשמצב האקטיביטי נשמר; לא בשימוש כאן
            }
        });
    }

    /**
     * מעדכן את מצב האפליקציה המאוחסן ב-SharedPreferences.
     *
     * @param isRunning true אם האפליקציה בקדמה, false אם ברקע
     */
    private void updateAppState(boolean isRunning) {
        SharedPreferences prefs = getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        prefs.edit().putBoolean(IS_APP_RUNNING, isRunning).apply();
        // שמירה האם האפליקציה פעילה כעת
    }

    /**
     * בודק האם האפליקציה רצה כעת בקדמה.
     *
     * @param context הקשר כלשהו (אקטיביטי/שירות/אפליקציה)
     * @return true אם האפליקציה בקדמה, אחרת false
     */
    public static boolean isAppRunning(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        return prefs.getBoolean(IS_APP_RUNNING, false);
        // קריאת מצב הקדמה/רקע המאוחסן
    }
}