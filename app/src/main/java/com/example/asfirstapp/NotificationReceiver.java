package com.example.asfirstapp; // הצהרת חבילה

// ייבוא נחוץ עבור התראות וטיפול בשידורים (broadcasts)
import android.app.NotificationChannel;       // ליצירת ערוצי התראה (אנדרואיד 8+)
import android.app.NotificationManager;       // לניהול והצגת התראות
import android.app.PendingIntent;             // עוטף אינטנטים להפעלה מאוחרת מהתראות
import android.content.BroadcastReceiver;     // מקבל אירועי שידור מהמערכת/אפליקציה
import android.content.Context;               // מספק הקשר (Context) עבור שירותי מערכת
import android.content.Intent;                // משמש להגדרת האקטיביטי שתיפתח
import android.os.Build;                      // לבדיקת גרסת אנדרואיד
import android.util.Log;                      // לרישום הודעות ניפוי שגיאות (debug)

import androidx.core.app.NotificationCompat;  // ספריית תמיכה לבניית התראות

/**
 * NotificationReceiver מטפל בהתראות מתוזמנות.
 * מופעל על ידי AlarmManager, הוא שולח התראה אם האפליקציה אינה בקדמה.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    // תגית לרישום הודעות ניפוי שגיאות

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "NotificationReceiver triggered.");
        // נקרא כאשר השידור מתקבל; משמש לניפוי שגיאות

        // בדיקה אם האפליקציה רצה (בקדמה) באמצעות מחלקת MyApp
        if (MyApp.isAppRunning(context)) {
            Log.d(TAG, "App is running, skipping notification.");
            // אם האפליקציה כבר פתוחה, לא נציג התראה
            return;
        }

        Log.d(TAG, "App is closed, sending notification.");
        // האפליקציה אינה פתוחה, לכן נמשיך לשליחת התראה

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // מקבל את שירות המערכת האחראי על התראות

        if (notificationManager != null) {

            // יצירת ערוץ התראות (נדרש עבור אנדרואיד 8 ומעלה)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(
                        "slinkypie",                 // מזהה ערוץ ייחודי
                        "Notifications",             // שם ערוץ גלוי למשתמש
                        NotificationManager.IMPORTANCE_DEFAULT // רמת חשיבות סטנדרטית
                );

                notificationManager.createNotificationChannel(channel);
                // רישום הערוץ במערכת
            }

            // אינטנט שפותח את MainActivity כאשר לוחצים על ההתראה
            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // מונע כפילויות במחסנית האקטיביטיז

            // עטיפת האינטנט ב-PendingIntent כך שההתראה תוכל להפעיל אותו מאוחר יותר
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0, // קוד בקשה (משמש אם קיימות מספר התראות)
                    repeatingIntent,
                    PendingIntent.FLAG_IMMUTABLE // נדרש עבור אבטחה בגרסאות אנדרואיד חדשות
            );

            // בניית ההתראה
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "slinkypie")
                            .setSmallIcon(R.drawable.app_icon)
                            // אייקון המוצג בשורת המצב

                            .setContentTitle("SlinkyPie's Quiz")
                            // כותרת ההתראה

                            .setContentText("Come and play me!")
                            // הודעת ההתראה

                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // התראה בעדיפות רגילה

                            .setAutoCancel(true)
                            // מסיר את ההתראה כאשר לוחצים עליה

                            .setContentIntent(pendingIntent);
            // פותח את האפליקציה בעת לחיצה

            // הצגת ההתראה
            notificationManager.notify(100, builder.build());
            Log.d(TAG, "Notification sent.");
            // רישום אישור שליחה
        }
    }
}