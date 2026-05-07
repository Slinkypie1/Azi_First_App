package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// ייבוא עבור חיישנים, הקשר (Context) וניהול אקטיביטי
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Puzzle3 Activity
 * ----------------
 * פאזל מבוך הנשלט על ידי הטיית המכשיר.
 * הכדור נע בתוך MazeGridView מותאם אישית בהתאם לקריאות מד התאוצה (Accelerometer).
 */
public class Puzzle3 extends BaseMenuActivity implements SensorEventListener {

    // מערכת החיישנים המשמשת לזיהוי הטיית המכשיר
    private SensorManager sensorManager;  // מנהל את כל חיישני המכשיר
    private Sensor accelerometer;         // מזהה הטיית מכשיר (תנועה בציר X/Y)

    // תצוגה מותאמת אישית המטפלת בלוגיקת ציור המבוך והכדור
    private MazeGridView mazeGridView;

    // משמש למדידת משך ההפסקה לפני תחילת המשחק
    private long pauseStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // הפעלת מוזיקת רקע עבור פאזל 3
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle3_music);
        startService(serviceIntent);

        // יצירת תצוגת המבוך באופן תכנותי
        mazeGridView = new MazeGridView(this);

        // הגדרת התצוגה המותאמת אישית כתוכן של כל המסך
        setContentView(mazeGridView);

        // הצגת דיאלוג הוראות לפני תחילת המשחק
        showInstructions();

        // קבלת שירות החיישנים של המערכת
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            // קבלת חיישן מד התאוצה (משמש לבקרת הטיה)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    /**
     * מציג הוראות לפני תחילת הפאזל.
     * במצב מתוזמן, שלב זה מדלג אוטומטית.
     */
    private void showInstructions() {

        // בדיקת מצב המשחק הנוכחי מהעדפות משותפות
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        if (mode.equals("timed")) {
            // דילוג על הוראות במצב מתוזמן כדי למנוע בזבוז זמן
            mazeGridView.beginGame();
            return;
        }

        // תיעוד מתי מסך ההוראות התחיל
        pauseStartTime = System.currentTimeMillis();

        // הצגת דיאלוג קופץ עם הוראות
        new AlertDialog.Builder(this)
                .setTitle("Level 6: Tilt Maze")
                .setMessage("Tilt your phone to guide the red ball to the green goal!\n\n" +
                        "Avoid the black walls")
                .setCancelable(false)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // חישוב כמה זמן השחקן בילה בקריאת ההוראות
                        long pausedDuration = System.currentTimeMillis() - pauseStartTime;

                        // שמירת זמן ההשהיה לצורך התאמת הניקוד
                        ProgressStorage.addPausedTime(Puzzle3.this, pausedDuration);

                        // התחלת ספירה לאחור של המבוך ותחילת המשחק
                        mazeGridView.beginGame();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume(); // המשך מחזור החיים

        // רישום מאזין למד התאוצה כאשר המסך פעיל
        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME // מהירות מאוזנת עבור משחק
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause(); // השהיית מחזור החיים

        // הפסקת האזנה לחיישנים כדי לחסוך בסוללה ובביצועים
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // תגובה רק לעדכוני מד תאוצה
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // ציר X = הטיה שמאלה/ימינה
            float tiltX = event.values[0];

            // ציר Y = הטיה קדימה/אחורה
            float tiltY = event.values[1];

            // שליחת נתוני התנועה לתצוגת המבוך
            mazeGridView.updateBall(tiltX, tiltY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // לא נדרש עבור פאזל זה
    }
}