package com.example.asfirstapp;
// מגדיר את שם החבילה של האפליקציה (עוזר בארגון קוד ומניעת התנגשויות).

import android.app.AlarmManager;
// משמש לתזמון משימות/התראות בזמנים ספציפיים.

import android.app.NotificationChannel;
// משמש ליצירת ערוצי התראה (אנדרואיד 8 ומעלה).

import android.app.NotificationManager;
// מנהל התראות עבור האפליקציה.

import android.app.PendingIntent;
// מעניק הרשאה לאפליקציה/רכיב אחר להריץ קוד מאוחר יותר.

import android.content.Context;
// מספק הקשר של האפליקציה (מידע גלובלי על האפליקציה).

import android.content.Intent;
// משמש להפעלת אקטיביטיז/שירותים או שליחת שידורים.

import android.content.SharedPreferences;
// משמש לאחסון נתוני מפתח-ערך קבועים וקטנים באופן מקומי.

import android.content.pm.PackageManager;
// משמש לבדיקת הרשאות אפליקציה.

import android.os.Build;
// מספק מידע על גרסת מערכת ההפעלה של המכשיר.

import android.os.Bundle;
// מחזיק נתוני מצב שמורים ליצירה מחדש של האקטיביטי.

import android.os.PowerManager;
// מנהל תכונות חשמל/סוללה.

import android.provider.Settings;
// מאפשר לפתוח מסכי הגדרות מערכת.

import android.util.Log;
// לרישום הודעות ניפוי שגיאות/שגיאות (Log).

import android.view.View;
// מחלקת בסיס לרכיבי ממשק משתמש.

import android.widget.Button;
// מייצג כפתור לחיץ.

import android.widget.EditText;
// מייצג שדה להזנת טקסט.

import android.widget.Toast;
// הודעות קופצות למשוב למשתמש.

import androidx.annotation.NonNull;
// הערה למניעת ערכי null בפרמטרים.

import androidx.appcompat.app.AppCompatActivity;
// מחלקת בסיס לאקטיביטיז מודרניות.

import androidx.core.app.ActivityCompat;
// עוזר לבקש הרשאות בזמן ריצה.

import androidx.core.content.ContextCompat;
// עוזר לבדוק אם הרשאות הוענקו.

import com.google.android.material.navigation.NavigationView;
// רכיב ניווט של Material UI (לא בשימוש ישיר כאן).

import com.google.firebase.auth.FirebaseAuth;
// גישה לאימות Firebase (Authentication).

import com.google.firebase.firestore.FirebaseFirestore;
// גישה למסד הנתונים Firebase Firestore.

import java.util.ArrayList;
// מימוש רשימה דינמית.

import java.util.HashMap;
// מימוש מפת מפתח-ערך (Map).

import java.util.List;
// ממשק רשימה עבור אוספים.

import java.util.Map;
// ממשק מפה עבור נתוני מפתח-ערך.

public class MainActivity extends BaseMenuActivity implements View.OnClickListener {
    // מסך הכניסה הראשי של האפליקציה; מטפל בהתחברות, הגדרה וניווט.
    // מממש טיפול בלחיצות עבור כפתורים.

    Button BtCLick;
    // כפתור המשמש להמשך לאחר הזנת פרטי המשתמש.

    EditText ET;
    // שדה קלט עבור שם המשתמש.

    EditText etEmail;
    // שדה קלט עבור אימייל המשתמש.

    EditText etPassword;
    // שדה קלט עבור סיסמת המשתמש.

    private static final int PERMISSION_REQUEST_CODE = 100;
    // קוד בקשה המשמש בעת בקשת הרשאת התראה.

    private static final String TAG = "MainActivity";
    // תגית המשמשת לרישום הודעות ניפוי שגיאות.

    private FirebaseFirestore db;
    // מופע של מסד הנתונים Firestore.

    private FirebaseAuth mAuth;
    // מופע של אימות Firebase.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // קריאה ללוגיקת הגדרת ההורה.

        db = FirebaseFirestore.getInstance();
        // אתחול החיבור למסד הנתונים Firestore.

        mAuth = FirebaseAuth.getInstance();
        // אתחול אימות Firebase.

        setupLoginUI();
    }

    /**
     * מגדיר את ממשק המשתמש הסטנדרטי של מסך הכניסה ושירותי הרקע.
     */
    private void setupLoginUI() {
        setContentView(R.layout.activity_main);
        // טעינת ממשק המשתמש של הפריסה הראשית.

        // אתחול מצב משחק לברירת מחדל "casual" אם עדיין לא הוגדר
        if (!ProgressStorage.getAppPrefs(this).contains("game_mode")) {
            ProgressStorage.getAppPrefs(this)
                    .edit()
                    .putString("game_mode", "casual")
                    .apply();
        }

        // הפעלת שירות מוזיקת רקע
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.main_activity_music);
        startService(serviceIntent);

        initViews();
        // אתחול רכיבי ממשק משתמש.

        startForegroundService();
        // הפעלת שירות קדמה קבוע.

        // בדיקת הרשאת התראה באנדרואיד 13 ומעלה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
                // בקשת הרשאה אם יש צורך.
            } else {
                setupNotification();
                // ההרשאה כבר הוענקה.
            }
        } else {
            setupNotification();
            // גרסאות אנדרואיד ישנות מעניקות הרשאה אוטומטית.
        }

        checkBatteryOptimization();
        // לוודא שהאפליקציה אינה מוגבלת על ידי הגדרות סוללה.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // הפעלה מחדש של המוזיקה כאשר האקטיביטי נראית.

        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.main_activity_music);
        startService(serviceIntent);
    }

    // הגדרת התראות: ערוץ + תזמון התראה יומית
    private void setupNotification() {
        createNotificationChannel();
        // יצירת ערוץ התראות.

        scheduleNotification();
        // תזמון התראה חוזרת.
    }

    // יצירת ערוץ התראות (אנדרואיד 8 ומעלה)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "slinkypie";
            CharSequence channelName = "Notifications";
            String channelDescription = "Channel for notifications";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                // רישום הערוץ במערכת.
            }
        }
    }

    // הפעלת שירות קדמה
    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
            // דרישת אנדרואיד מודרנית.
        } else {
            startService(serviceIntent);
            // תמיכה לאחור בגרסאות אנדרואיד ישנות.
        }
    }

    // תזמון התראות יומיות באמצעות AlarmManager
    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY;
        // הגדרת ההפעלה הראשונה ל-24 שעות מאוחר יותר.

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
            Log.d(TAG, "Alarm scheduled for daily notifications.");
        }
    }

    // טיפול בתוצאות בקשת הרשאה
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotification();
                // ההרשאה הוענקה.
            } else {
                Toast.makeText(this,
                        "Notification permission is required for this feature.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // בדיקת הגדרות אופטימיזציית סוללה
    private void checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {

                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
                // בקשה מהמשתמש לבטל אופטימיזציה.
            }
        }
    }

    // אתחול רכיבי ממשק משתמש
    private void initViews() {
        BtCLick = findViewById(R.id.BtClick);
        BtCLick.setOnClickListener(this);

        ET = findViewById(R.id.ET);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    // טיפול בלחיצה על כפתור
    @Override
    public void onClick(View view) {
        String inputText = ET.getText().toString().trim();
        String inputEmail = etEmail.getText().toString().trim();
        String inputPassword = etPassword.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        handleAuth(inputText, inputEmail, inputPassword);
    }

    private void handleAuth(String name, String email, String password) {
        // ניסיון התחברות
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // הצלחה בהתחברות, עדכון ממשק המשתמש עם פרטי המשתמש המחובר
                        Log.d(TAG, "signInWithEmail:success");
                        handleLogin(name, email);
                    } else {
                        // אם ההתחברות נכשלת, ניסיון ליצור חשבון חדש
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        createNewUser(name, email, password);
                    }
                });
    }

    private void createNewUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        Log.d(TAG, "createUserWithEmail:success");
                        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        String documentId = mAuth.getCurrentUser().getUid(); // שימוש ב-UID במקום אימייל
                        saveNewUser(name, email, deviceId, documentId);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(MainActivity.this, "Authentication failed: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // טעינת או יצירת משתמש
    private void handleLogin(String name, String email) {
        if (mAuth.getCurrentUser() == null) return;
        String documentId = mAuth.getCurrentUser().getUid(); // שימוש ב-UID

        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        // עדכון העדפות מקומיות עם נתונים מ-Firestore
                        String fetchedName = documentSnapshot.getString("name");
                        if (fetchedName == null) fetchedName = name;

                        // העדפות גלובליות עבור טופס ההתחברות
                        getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                                .putString("last_name", fetchedName)
                                .putString("last_email", email)
                                .apply();

                        Long unlockedLevels = documentSnapshot.getLong("unlockedLevels");

                        if (unlockedLevels != null) {
                            ProgressStorage.setHighestUnlockedLevelOffline(MainActivity.this, unlockedLevels.intValue());
                        }

                        List<String> achievements = (List<String>) documentSnapshot.get("achievements");

                        if (achievements != null) {
                            ProgressStorage.setAchievementsOffline(MainActivity.this, achievements);
                        }

                        String bgColor = documentSnapshot.getString("bg_color");
                        String gameMode = documentSnapshot.getString("game_mode");
                        Boolean isMuted = documentSnapshot.getBoolean("music_muted");

                        // העדפות ספציפיות למשתמש עבור הגדרות
                        SharedPreferences.Editor userEditor = ProgressStorage.getAppPrefs(MainActivity.this).edit();
                        userEditor.putString("last_name", fetchedName);

                        if (bgColor != null) userEditor.putString("bg_color", bgColor);
                        if (gameMode != null) userEditor.putString("game_mode", gameMode);
                        if (isMuted != null) userEditor.putBoolean("music_muted", isMuted);

                        userEditor.apply();

                        proceedToSecond(fetchedName);
                    } else {
                        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        saveNewUser(name, email, deviceId, documentId);
                    }
                })
                .addOnFailureListener(e -> proceedToSecond(name));
    }

    // שמירת משתמש חדש
    private void saveNewUser(String name, String email, String deviceId, String documentId) {
        Map<String, Object> user = new HashMap<>();

        user.put("name", name);
        user.put("email", email);
        user.put("deviceId", deviceId);
        user.put("unlockedLevels", 1);
        user.put("bg_color", "white");
        user.put("game_mode", "casual");
        user.put("music_muted", false);
        user.put("achievements", new ArrayList<String>());

        // העדפות גלובליות עבור טופס ההתחברות
        getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                .putString("last_name", name)
                .putString("last_email", email)
                .apply();

        // העדפות ספציפיות למשתמש עם ברירות מחדל
        ProgressStorage.getAppPrefs(MainActivity.this).edit()
                .putString("last_name", name)
                .putString("bg_color", "white")
                .putString("game_mode", "casual")
                .putBoolean("music_muted", false)
                .apply();

        db.collection("users").document(documentId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    ProgressStorage.setHighestUnlockedLevelOffline(MainActivity.this, 1);
                    proceedToSecond(name);
                })
                .addOnFailureListener(e -> proceedToSecond(name));
    }

    // מעבר למסך הבא
    private void proceedToSecond(String name) {
        Intent intent = new Intent(this, Second.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    // עצירת מוזיקה בעת עזיבת האקטיביטי
    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
    }
}