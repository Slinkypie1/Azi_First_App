package com.example.asfirstapp;
// Defines the package name of your app (helps organize code and avoid conflicts).

import android.app.AlarmManager;
// Used to schedule tasks/notifications at specific times.

import android.app.NotificationChannel;
// Used to create notification channels (Android 8+).

import android.app.NotificationManager;
// Manages notifications for the app.

import android.app.PendingIntent;
// Grants permission to another app/component to run code later.

import android.content.Context;
// Provides app context (global info about the app).

import android.content.Intent;
// Used to start activities/services or send broadcasts.

import android.content.SharedPreferences;
// Used to store small persistent key-value data locally.

import android.content.pm.PackageManager;
// Used to check app permissions.

import android.os.Build;
// Provides device OS version info.

import android.os.Bundle;
// Holds saved state data for activity recreation.

import android.os.PowerManager;
// Manages power/battery features.

import android.provider.Settings;
// Lets you open system settings screens.

import android.util.Log;
// For logging debug/error messages.

import android.view.View;
// Base class for UI components.

import android.widget.Button;
// Represents a clickable button.

import android.widget.EditText;
// Represents a text input field.

import android.widget.Toast;
// Popup messages for user feedback.

import androidx.annotation.NonNull;
// Annotation to prevent null values in parameters.

import androidx.appcompat.app.AppCompatActivity;
// Base class for modern activities.

import androidx.core.app.ActivityCompat;
// Helps request runtime permissions.

import androidx.core.content.ContextCompat;
// Helps check if permissions are granted.

import com.google.android.material.navigation.NavigationView;
// Material UI navigation component (not directly used here).

import com.google.firebase.auth.FirebaseAuth;
// Firebase Authentication access.

import com.google.firebase.firestore.FirebaseFirestore;
// Firebase Firestore database access.

import java.util.ArrayList;
// Dynamic list implementation.

import java.util.HashMap;
// Key-value map implementation.

import java.util.List;
// List interface for collections.

import java.util.Map;
// Map interface for key-value data.

public class MainActivity extends BaseMenuActivity implements View.OnClickListener {
    // Main entry screen of the app; handles login, setup, and navigation.
    // Implements click handling for buttons.

    Button BtCLick;
    // Button used to proceed after entering user info.

    EditText ET;
    // Input field for user name.

    EditText etEmail;
    // Input field for user email.

    private static final int PERMISSION_REQUEST_CODE = 100;
    // Request code used when asking for notification permission.

    private static final String TAG = "MainActivity";
    // Tag used for logging debug messages.

    private FirebaseFirestore db;
    // Firestore database instance.

    private FirebaseAuth mAuth;
    // Firebase authentication instance.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calls parent setup logic.

        db = FirebaseFirestore.getInstance();
        // Initializes Firestore database connection.

        mAuth = FirebaseAuth.getInstance();
        // Initializes Firebase authentication.

        // Check if a player is already logged in
        SharedPreferences appPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        // Access local stored preferences.

        String savedName = appPrefs.getString("last_name", "");
        // Retrieve saved user name if exists.

        String savedEmail = appPrefs.getString("last_email", "");
        // Retrieve saved email if exists.

        if (!savedName.isEmpty() && !savedEmail.isEmpty()) {
            // If user data exists, try silent login.

            mAuth.signInWithEmailAndPassword("azriel.zev@gmail.com", "A'$Sc80ol@9p")
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            handleLogin(savedName, savedEmail);
                            // Proceed with loaded user.
                        } else {
                            setupLoginUI();
                            // Fallback to login screen.
                        }
                    });
            return;
        }

        setupLoginUI();
        // Default path: show login UI.
    }

    /**
     * Sets up the standard login screen UI and background services.
     */
    private void setupLoginUI() {
        setContentView(R.layout.activity_main);
        // Loads main layout UI.

        // Sign in if not already signed in (path for first-time users)
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword("azriel.zev@gmail.com", "A'$Sc80ol@9p")
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("FIREBASE_AUTH", "Logged in successfully");
                        }
                    });
        }

        // Initialize default game mode to casual if not already set
        if (!getSharedPreferences("app_prefs", MODE_PRIVATE).contains("game_mode")) {
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("game_mode", "casual")
                    .apply();
        }

        // Start background music service
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.main_activity_music);
        startService(serviceIntent);

        initViews();
        // Initialize UI elements.

        startForegroundService();
        // Starts persistent foreground service.

        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
                // Request permission if needed.
            } else {
                setupNotification();
                // Permission already granted.
            }
        } else {
            setupNotification();
            // Older Android versions auto-grant.
        }

        checkBatteryOptimization();
        // Ensure app is not restricted by battery settings.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart music when activity is visible.

        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.main_activity_music);
        startService(serviceIntent);
    }

    // Setup notifications: channel + schedule daily notification
    private void setupNotification() {
        createNotificationChannel();
        // Create notification channel.

        scheduleNotification();
        // Schedule repeating notification.
    }

    // Create notification channel (Android 8+)
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
                // Register channel with system.
            }
        }
    }

    // Start foreground service
    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
            // Modern Android requirement.
        } else {
            startService(serviceIntent);
            // Older Android fallback.
        }
    }

    // Schedule daily notifications using AlarmManager
    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY;
        // Set first trigger 24h later.

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

    // Handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotification();
                // Permission granted.
            } else {
                Toast.makeText(this,
                        "Notification permission is required for this feature.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Check battery optimization settings
    private void checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {

                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
                // Ask user to disable optimization.
            }
        }
    }

    // Initialize UI elements
    private void initViews() {
        BtCLick = findViewById(R.id.BtClick);
        BtCLick.setOnClickListener(this);

        ET = findViewById(R.id.ET);
        etEmail = findViewById(R.id.etEmail);
    }

    // Handle button click
    @Override
    public void onClick(View view) {
        String inputText = ET.getText().toString().trim();
        String inputEmail = etEmail.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("last_name", inputText)
                .putString("last_email", inputEmail)
                .apply();

        handleLogin(inputText, inputEmail);
    }

    // Load or create user
    private void handleLogin(String name, String email) {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String documentId = email.toLowerCase().replace(".", "_");
        // Use email as document ID.

        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
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

                        SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();

                        if (bgColor != null) editor.putString("bg_color", bgColor);
                        if (gameMode != null) editor.putString("game_mode", gameMode);
                        if (isMuted != null) editor.putBoolean("music_muted", isMuted);

                        editor.apply();

                        proceedToSecond(name);
                    } else {
                        saveNewUser(name, email, deviceId, documentId);
                    }
                })
                .addOnFailureListener(e -> proceedToSecond(name));
    }

    // Save new user
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

        db.collection("users").document(documentId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    ProgressStorage.setHighestUnlockedLevelOffline(MainActivity.this, 1);
                    proceedToSecond(name);
                })
                .addOnFailureListener(e -> proceedToSecond(name));
    }

    // Go to next screen
    private void proceedToSecond(String name) {
        Intent intent = new Intent(this, Second.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    // Stop music when leaving activity
    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
    }
}