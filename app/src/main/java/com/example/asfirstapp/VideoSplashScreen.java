package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // משמש למעבר בין אקטיביטיז (מסכים)
import android.net.Uri; // משמש לטיפול בכתובות URI של משאבים
import android.os.Bundle; // מחזיק נתוני מצב שמורים עבור מחזור החיים של האקטיביטי
import android.widget.VideoView; // רכיב ממשק משתמש להצגת והפעלת וידאו

import androidx.activity.EdgeToEdge; // מאפשר פריסת מסך מלא מקצה לקצה
import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטיז עם תמיכה ב-AppCompat

/**
 * VideoSplashScreen
 * ------------------
 * המסך הראשון שמוצג כשהאפליקציה עולה.
 * מפעיל וידאו במסך מלא ולאחר מכן עובר אוטומטית
 * למסך הפתיחה הבא.
 */
public class VideoSplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה ללוגיקת ההגדרה של ההורה

        // הפעלת פריסת מסך מלא (קצה לקצה)
        EdgeToEdge.enable(this);

        // הגדרת קובץ ה-XML של הפריסה המכיל את ה-VideoView
        setContentView(R.layout.activity_video_splash);

        // קישור לרכיב ה-VideoView שנמצא בפריסה
        VideoView videoView = findViewById(R.id.videoView);

        // בניית כתובת URI המצביעה על הוידאו המאוחסן בתיקיית res/raw
        String path = "android.resource://" + getPackageName() + "/" + R.raw.slinkypie1_launch;
        videoView.setVideoURI(Uri.parse(path)); // הגדרת הוידאו לנגן

        // כאשר הוידאו מסתיים, מעבר למסך הבא
        videoView.setOnCompletionListener(mp -> navigateToNext());

        // אם הוידאו נכשל בהפעלה, עדיין נמשיך למסך הבא
        videoView.setOnErrorListener((mp, what, extra) -> {
            navigateToNext(); // קריאה לפונקציית הניווט
            return true; // ציון שהשגיאה טופלה
        });

        // התחלת הפעלת הוידאו
        videoView.start();
    }

    /**
     * מנווט ממסך וידאו הפתיחה אל מסך הפתיחה הראשי.
     */
    private void navigateToNext() {
        // יצירת אינטנט למעבר למסך SplashScreen
        Intent intent = new Intent(VideoSplashScreen.this, SplashScreen.class);
        startActivity(intent); // הפעלת האקטיביטי הבאה
        finish(); // סגירת האקטיביטי הנוכחית כדי שהמשתמש לא יוכל לחזור אליה
    }
}