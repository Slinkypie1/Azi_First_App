package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// ייבוא עבור חיישנים, ממשק משתמש וניהול אקטיביטי
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

/**
 * Puzzle2 Activity
 * ----------------
 * פאזל חידה מבוסס מצפן:
 * השחקן חייב לפנות לכיוונים רוחות שמים ספציפיים (מזרח, מערב, צפון)
 * כדי להתקדם בשלבים ולהגיע למסך ההצלחה.
 * כולל טיימר של 20 שניות שמוביל למסך כישלון.
 */
public class Puzzle2 extends BaseMenuActivity implements SensorEventListener {

    // אובייקטים הקשורים לחיישנים המשמשים לזיהוי מצפן
    private SensorManager sensorManager;    // מנהל את חיישני המכשיר
    private Sensor rotationSensor;          // חיישן וקטור סיבוב (משמש כמצפן)
    private TextView hint;                  // מציג את טקסט החידה הנוכחית
    private TextView directionText;         // מציג את הכיוון שזוהה כעת

    // מעקב אחר מצב הפאזל
    private boolean puzzleCompleted = false; // מונע הפעלת ניווט מספר פעמים
    private String lastDirection = "";       // שומר את הכיוון האחרון שזוהה
    private long lastUpdateTime = 0;         // זמן העדכון האחרון של כיוון תקין
    private int currentStep = 0;             // עוקב אחר ההתקדמות בחידות
    private long startTime;                  // מתעד את זמן התחלת הפאזל

    private static final long SENSOR_UPDATE_THRESHOLD = 500; // מגביל את תדירות עדכון החיישן
    private long lastSensorUpdate = 0; // הפעם האחרונה שבה עובדו נתוני החיישן

    private Handler handler = new Handler(Looper.getMainLooper()); // מטפל (Handler) לשרשור הראשי
    private Runnable failureRunnable; // פעולה המפעילה את מסך הכישלון

    // רשימת חידות לכל שלב בפאזל
    private final String[] riddles = {
            // שלב 0: מזרח
            "I rise each day, yet I am not the sun.\nTravelers seek me when their journey’s begun.\nOn a compass, I take my place,\nOpposite where the sun sets with grace.\nWhat am I?",
            // שלב 1: מערב
            "I follow the sun as it ends the day,\nGuiding travelers along their way.\nOn a compass, I take my stand,\nOpposite where the day began.\nWhat am I?",
            // שלב 2: צפון
            "I point the way, steady and true,\nThrough icy lands and skies so blue.\nThe compass trusts me, never astray,\nLeading explorers on their way.\nWhat am I?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // הפעלת תצוגה מקצה לקצה
        EdgeToEdge.enable(this);

        // טעינת פריסה עבור מסך Puzzle2
        setContentView(R.layout.activity_puzzle2);

        // הפעלת מוזיקת רקע עבור פאזל זה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle2_music);
        startService(serviceIntent);

        // תיעוד זמן התחלה לצורך ניקוד
        startTime = System.currentTimeMillis();

        // קישור רכיבי ממשק משתמש
        hint = findViewById(R.id.hint);
        directionText = findViewById(R.id.directionText);

        // הצגת החידה הראשונה
        hint.setText(riddles[currentStep]);

        // אתחול מערכת החיישנים
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // יציאה אם למכשיר אין חיישן מצפן
        if (rotationSensor == null) {
            finish();
        }

        // לוגיקת טיימר כישלון (פסק זמן של 20 שניות)
        failureRunnable = new Runnable() {
            @Override
            public void run() {
                if (!puzzleCompleted) {
                    puzzleCompleted = true;
                    Log.d("Puzzle2", "20 seconds up! Navigating to Failure.");

                    // הפסקת עדכוני חיישנים
                    sensorManager.unregisterListener(Puzzle2.this);

                    // ניווט למסך כישלון
                    Intent intent = new Intent(Puzzle2.this, Failure.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // התחלת האזנה לעדכוני חיישן מצפן
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }

        // טעינת מצב משחק (רגיל או מתוזמן)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // הפעלת טיימר כישלון רק במצב מתוזמן
        if (mode.equals("timed") && !puzzleCompleted) {
            handler.postDelayed(failureRunnable, 20000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // הפסקת עדכוני חיישנים כאשר האקטיביטי אינה גלויה
        if (rotationSensor != null) {
            sensorManager.unregisterListener(this);
        }

        // ביטול טיימר כישלון למניעת דליפות זיכרון
        handler.removeCallbacks(failureRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // התעלמות מעדכונים אם הפאזל הושלם או סוג חיישן שגוי
        if (puzzleCompleted || event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;

        long currentTime = System.currentTimeMillis();

        // מניעת עדכונים תכופים מדי
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_THRESHOLD) return;
        lastSensorUpdate = currentTime;

        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];

        // המרת וקטור סיבוב לערכי כיוון שמישים
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        int azimuth = (int) Math.toDegrees(orientation[0]);
        azimuth = (azimuth + 360) % 360; // נרמול הזווית

        // המרת זווית למחרוזת כיוון
        String currentDirection = getDirection(azimuth);

        // מעקב אחר שינויי כיוון
        if (!currentDirection.equals(lastDirection)) {
            lastDirection = currentDirection;
            lastUpdateTime = currentTime;
        }

        // עדכון ממשק המשתמש בכיוון הנוכחי
        directionText.setText("Current Direction: " + currentDirection);

        // בדיקה אם הכיוון הנכון מוחזק מספיק זמן
        if (currentDirection.equals(getDirectionForStep(currentStep))
                && (currentTime - lastUpdateTime > 1000)) {

            currentStep++;

            // אם כל השלבים הושלמו ← הצלחה
            if (currentStep == riddles.length) {
                puzzleCompleted = true;
                sensorManager.unregisterListener(this);

                // ביטול טיימר כישלון
                handler.removeCallbacks(failureRunnable);

                long timeTaken = System.currentTimeMillis() - startTime;

                // ניווט למסך הצלחה
                Intent intent = new Intent(this, CorrectScreen5.class);
                intent.putExtra("TIME_TAKEN", timeTaken);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // טעינת החידה הבאה
                hint.setText(riddles[currentStep]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // לא בשימוש בפאזל זה
    }

    /**
     * המרת זווית מצפן לכיוון רוחות שמים.
     */
    private String getDirection(int azimuth) {
        if (azimuth >= 45 && azimuth < 135) return "East";
        if (azimuth >= 135 && azimuth < 225) return "South";
        if (azimuth >= 225 && azimuth < 315) return "West";
        return "North";
    }

    /**
     * מחזיר את הכיוון המצופה עבור כל שלב בפאזל.
     */
    private String getDirectionForStep(int step) {
        switch (step) {
            case 0: return "East";
            case 1: return "West";
            case 2: return "North";
            default: return "North";
        }
    }
}