package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// ייבוא מחלקות אנדרואיד נחוצות עבור שירות (Service) והשמעת אודיו
import android.app.Service;       // מחלקת הבסיס לשירותי אנדרואיד
import android.content.Intent;    // דרוש להפעלת/עצירת השירות באמצעות Intents
import android.content.SharedPreferences; // לניהול העדפות משתמש
import android.media.MediaPlayer; // משמש להשמעת קבצי אודיו
import android.os.IBinder;        // משמש לשירותים קשורים (לא בשימוש כאן)

/**
 * MusicService הוא שירות רקע שמשמיע מוזיקה בלולאה
 * לאורך כל האפליקציה. הוא יכול להשמיע רצועות שונות בהתבסס על ה-intent שנשלח.
 */
public class MusicService extends Service {

    private MediaPlayer mediaPlayer; // מופע של MediaPlayer לטיפול באודיו
    private int currentResId = -1;    // מזהה המשאב (Resource ID) שמתנגן כעת

    @Override
    public void onCreate() {
        super.onCreate();
        // נקרא פעם אחת כאשר השירות נוצר לראשונה
    }

    /**
     * נקרא בכל פעם שמתבצעת קריאה ל-startService().
     * בודק אם התבקש משאב מוזיקה ספציפי או אם יש צורך בפעולת השהיה/חידוש.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = ProgressStorage.getAppPrefs(this);
        // גישה להעדפות האפליקציה (משמש לאחסון הגדרות מוזיקה והרצועה האחרונה שהושמעה)

        // 1. אם התבקשה רצועת מוזיקה ספציפית, זכור אותה
        if (intent != null && intent.hasExtra("MUSIC_RES_ID")) {
            int requestedMusicId = intent.getIntExtra("MUSIC_RES_ID", R.raw.main_activity_music);
            prefs.edit().putInt("last_music_res_id", requestedMusicId).apply();
            // שמירת המוזיקה שנבחרה כך שתמשיך בין מסכים
        }

        // 2. בדיקה אם המוזיקה מושתקת בהעדפות
        if (prefs.getBoolean("music_muted", false)) {
            stopCurrentMusic();
            return START_STICKY;
            // עצירת המוזיקה מיד אם המשתמש השתיק אותה
        }

        // 3. קביעה איזו רצועה צריכה להתנגן עכשיו
        // אנו משתמשים ב-ID מה-intent אם הוא קיים, אחרת חוזרים לשמור/ברירת מחדל
        int musicResId;
        if (intent != null && intent.hasExtra("MUSIC_RES_ID")) {
            musicResId = intent.getIntExtra("MUSIC_RES_ID", R.raw.main_activity_music);
        } else {
            musicResId = prefs.getInt("last_music_res_id", R.raw.main_activity_music);
        }

        // טיפול בפעולות השהיה (Pause) וחידוש (Resume)
        if (intent != null) {
            String action = intent.getAction();

            if ("ACTION_PAUSE".equals(action)) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    // השהיית ההשמעה מבלי להרוס את ה-MediaPlayer
                }
                return START_STICKY;

            } else if ("ACTION_RESUME".equals(action)) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    // חידוש ההשמעה אם הייתה מושהית
                }
                return START_STICKY;
            }
        }

        // 4. הפעלה מחדש רק אם המשאב השתנה או שאינו מתנגן
        if (mediaPlayer == null || currentResId != musicResId) {
            stopCurrentMusic();
            // עצירה ושחרור מוזיקה ישנה לפני החלפת רצועות

            currentResId = musicResId;
            mediaPlayer = MediaPlayer.create(this, musicResId);

            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                // התחלת מוזיקה חדשה במצב לולאה
            }

        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            // חידוש אם הוא נוצר אך אינו מתנגן כעת
        }

        return START_STICKY;
        // שומר על השירות פועל גם אם המערכת סוגרת אותו
    }

    private void stopCurrentMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            // עצירת ההשמעה

            mediaPlayer.release();
            // שחרור משאבי מערכת

            mediaPlayer = null;
            // מניעת דליפות זיכרון
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCurrentMusic();
        // וידוא שהמוזיקה נעצרת לחלוטין כאשר השירות מושמד
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // זהו שירות מסוג started service, לא bound service
    }
}