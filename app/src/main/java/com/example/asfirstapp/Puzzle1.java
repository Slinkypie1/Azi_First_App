package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

// ייבוא עבור חיישנים, ממשק משתמש, שרשורים וניהול אקטיביטי
import android.content.Context;
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

/**
 * Puzzle1 Activity
 * ----------------
 * פאזל מבוסס חיישן אור:
 * השחקן חייב להפחית את האור בסביבה מתחת לסף מסוים כדי להצליח.
 */
public class Puzzle1 extends BaseMenuActivity implements SensorEventListener {

    // אובייקטים הקשורים לחיישנים
    private SensorManager sensorManager;       // מנהל את הגישה לחיישני המכשיר
    private Sensor lightSensor;                // חיישן האור של המכשיר
    private TextView lightTextView;            // מציג את ערך האור הנוכחי

    // משתני בקרת מצב
    private static boolean hasNavigated = false; // מונע מעברי מסך מרובים
    private boolean isFirstReading = true;      // מדלג על קריאת חיישן ראשונה (לעתים לא יציבה)
    private Handler handler = new Handler(Looper.getMainLooper()); // מטפל (Handler) לשרשור הראשי
    private Runnable navigateRunnable;          // מטפל בהשהיית ניווט להצלחה
    private Runnable failureRunnable;           // מטפל בניווט לכישלון עקב פסק זמן
    private long startTime;                     // שומר את חותמת זמן תחילת הפאזל

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle1); // טעינת פריסת ממשק המשתמש

        Log.d("Puzzle1", "Puzzle1 started!"); // רישום שהפאזל התחיל

        // הפעלת מוזיקת רקע ספציפית לפאזל זה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.puzzle1_music);
        startService(serviceIntent);

        startTime = System.currentTimeMillis(); // תיעוד זמן ההתחלה

        // קישור רכיבי ממשק משתמש
        lightTextView = findViewById(R.id.lightTextView);

        // אתחול מערכת החיישנים
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            // המכשיר אינו תומך בחיישן אור
            lightTextView.setText("No Light Sensor Found!");
        }

        // איפוס נעילת הניווט
        hasNavigated = false;

        // לוגיקת כישלון: מופעלת לאחר פסק זמן
        failureRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    hasNavigated = true;
                    Log.d("Puzzle1", "Time up → Failure screen");

                    // הפסקת האזנה לחיישן
                    sensorManager.unregisterListener(Puzzle1.this);

                    // מעבר למסך כישלון
                    Intent intent = new Intent(Puzzle1.this, Failure.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        // לוגיקת הצלחה: מופעלת לאחר תנאי אור נמוך
        navigateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!hasNavigated) {
                    hasNavigated = true;
                    Log.d("Puzzle1", "Success → CorrectScreen4");

                    // הפסקת עדכוני חיישן
                    sensorManager.unregisterListener(Puzzle1.this);

                    // ביטול טיימר הכישלון
                    handler.removeCallbacks(failureRunnable);

                    long timeTaken = System.currentTimeMillis() - startTime;

                    // ניווט למסך הצלחה
                    Intent intent = new Intent(Puzzle1.this, CorrectScreen4.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0]; // רמת האור הנוכחית
        Log.d("Puzzle1", "Light Sensor Value: " + lightValue);

        // התעלמות מקריאה ראשונה לא יציבה
        if (isFirstReading) {
            isFirstReading = false;
            return;
        }

        // עדכון ממשק המשתמש עם ערך האור
        lightTextView.setText("Light Intensity: " + lightValue + " lx");

        // אם הסביבה חשוכה מספיק ← תזמון הצלחה
        if (lightValue <= 5 && !hasNavigated) {
            handler.postDelayed(navigateRunnable, 3000);
        } else {
            // ביטול הצלחה אם האור עולה שוב
            handler.removeCallbacks(navigateRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // התחלת האזנה לעדכוני חיישן האור
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // בדיקת מצב משחק (רגיל או מתוזמן)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        // הפעלת טיימר כישלון רק במצב מתוזמן
        if (mode.equals("timed") && !hasNavigated) {
            handler.postDelayed(failureRunnable, 20000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // הפסקת עדכוני חיישן כאשר האקטיביטי אינה נראית
        sensorManager.unregisterListener(this);

        // מניעת דליפות זיכרון על ידי הסרת הקולבקים
        handler.removeCallbacks(navigateRunnable);
        handler.removeCallbacks(failureRunnable);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // לא נדרש עבור פאזל זה
    }
}