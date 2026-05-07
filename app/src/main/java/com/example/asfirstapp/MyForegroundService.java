package com.example.asfirstapp; // הצהרת חבילה

// ייבוא נחוץ עבור שירות, התראות ובדיקות גרסת אנדרואיד
import android.app.NotificationChannel;     // ליצירת ערוצי התראה (אנדרואיד 8+)
import android.app.NotificationManager;     // לרישום ערוצי התראה
import android.app.Service;                 // מחלקת בסיס לכל השירותים
import android.content.Intent;              // משמש להפעלת השירות או העברת נתונים
import android.os.Build;                    // לבדיקת גרסת האנדרואיד
import android.os.IBinder;                  // נדרש עבור שירותים קשורים (bound services)
import androidx.annotation.Nullable;        // עבור הערות (annotations) של ערכים שיכולים להיות null
import androidx.core.app.NotificationCompat; // לבניית התראות

/**
 * MyForegroundService שומר על האפליקציה פעילה בקדמה.
 * שירותי קדמה חייבים להציג התראה קבועה.
 */
public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    // מזהה ייחודי עבור ערוץ ההתראות

    @Override
    public void onCreate() {
        super.onCreate();
        // נקרא כאשר השירות נוצר לראשונה (פעם אחת בכל מחזור חיים)

        createNotificationChannel();
        // וידוא שקיים ערוץ התראות עבור אנדרואיד 8.0 ומעלה
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // בניית ההתראה שהמשתמש רואה בזמן שהשירות רץ

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running")
                // כותרת ההתראה

                .setContentText("This app is staying active in the foreground.")
                // טקסט תיאור ההתראה

                .setSmallIcon(R.drawable.app_icon)
                // אייקון קטן המוצג בשורת המצב

                .setPriority(NotificationCompat.PRIORITY_LOW);
        // עדיפות נמוכה מפחיתה הפרעות (ללא צליל/התראה קופצת)

        startForeground(1, notification.build());
        // מקדם את השירות לקדמה כך שהמערכת תשמור עליו פעיל

        return START_STICKY;
        // אם המערכת סוגרת את השירות, היא תנסה ליצור אותו מחדש
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        // הסרת התראת הקדמה והפסקת מצב הקדמה
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // שירות זה אינו קשור לאף אקטיביטי
    }

    /**
     * יוצר ערוץ התראות עבור אנדרואיד 8.0 ומעלה.
     * נדרש כדי להציג התראות בשירותי קדמה.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, // מזהה ערוץ ייחודי
                    "Foreground Service Channel", // שם הערוץ הגלוי למשתמש
                    NotificationManager.IMPORTANCE_LOW // חשיבות נמוכה (שקט)
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            // גישה למנהל ההתראות של המערכת

            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                // רישום הערוץ במערכת
            }
        }
    }
}