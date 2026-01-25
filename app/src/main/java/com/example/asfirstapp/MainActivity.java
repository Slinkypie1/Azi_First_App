package com.example.asfirstapp;
// Defines the package name of your app (helps organize code and avoid conflicts).

import android.app.AlarmManager;              // Used to schedule tasks/notifications at specific times.
import android.app.NotificationChannel;       // Used to create notification channels (Android 8+).
import android.app.NotificationManager;       // Manages notifications for the app.
import android.app.PendingIntent;             // Grants permission to another app/component to run code later.
import android.content.Context;               // Provides app context (global info about the app).
import android.content.Intent;                // Used to start activities/services or send broadcasts.
import android.content.pm.PackageManager;     // Used to check app permissions.
import android.os.Build;                      // Provides device OS version info.
import android.os.Bundle;                     // Holds saved state data for activity recreation.
import android.os.PowerManager;               // Manages power/battery features.
import android.provider.Settings;             // Lets you open system settings screens.
import android.util.Log;                      // For logging debug/error messages.
import android.view.View;                     // Base class for UI components.
import android.widget.Button;                 // Represents a clickable button.
import android.widget.EditText;               // Represents a text input field.
import android.widget.Toast;                  // Popup messages for user feedback.

import androidx.annotation.NonNull;           // Annotation to prevent null values in parameters.
import androidx.appcompat.app.AppCompatActivity; // Base class for modern activities.
import androidx.core.app.ActivityCompat;      // Helps request runtime permissions.
import androidx.core.content.ContextCompat;   // Helps check if permissions are granted.

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore; // Firebase Firestore database access.
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseMenuActivity implements View.OnClickListener {
    // MainActivity handles the first screen and login logic.
    // Implements OnClickListener for handling button clicks.

    Button BtCLick; // Button to start next activity.
    EditText ET;    // EditText for user to enter their name.
    private static final int PERMISSION_REQUEST_CODE = 100; // ID for permission request.
    private static final String TAG = "MainActivity";       // Tag for logging messages.

    private FirebaseFirestore db; // Reference to Firebase Firestore database.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                 // Call parent setup
        setContentView(R.layout.activity_main);             // Load main layout

        db = FirebaseFirestore.getInstance();               // Initialize Firestore

        // Start background music service
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);

        initViews();                                        // Initialize UI elements

        startForegroundService();                           // Keep app alive in foreground service

        // Check for notification permission on Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Ask user for notification permission if not already granted
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {
                setupNotification();                        // If permission granted, set up notifications
            }
        } else {
            setupNotification();                            // Older Android versions don't need permission
        }

        checkBatteryOptimization();                         // Ensure app is excluded from battery optimization
    }

    // Setup notifications: channel + schedule daily notification
    private void setupNotification() {
        createNotificationChannel(); // Create notification channel for Android 8+
        scheduleNotification();      // Schedule recurring daily notification
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
                notificationManager.createNotificationChannel(channel); // Register the channel
            }
        }
    }

    // Start foreground service to keep app alive
    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent); // For Android 8+, must call startForegroundService
        } else {
            startService(serviceIntent);           // For older versions, normal startService
        }
    }

    // Schedule daily notifications using AlarmManager
    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY;

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,         // Wake device if asleep
                    triggerTime,                     // Start time
                    AlarmManager.INTERVAL_DAY,       // Repeat interval = 1 day
                    pendingIntent                    // Action to execute
            );
            Log.d(TAG, "Alarm scheduled for daily notifications.");
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotification(); // Permission granted → set up notifications
            } else {
                Toast.makeText(this,
                        "Notification permission is required for this feature.",
                        Toast.LENGTH_LONG).show(); // Show warning if denied
            }
        }
    }

    // Check if app is excluded from battery optimization
    private void checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                // If app is not exempt → open settings so user can allow
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        }
    }

    // Initialize UI elements (Button + EditText)
    private void initViews() {
        BtCLick = findViewById(R.id.BtClick);   // Find button in layout
        BtCLick.setOnClickListener(this);       // Set click listener
        ET = findViewById(R.id.ET);             // Find EditText in layout
    }

    // Handle button click
    @Override
    public void onClick(View view) {
        String inputText = ET.getText().toString().trim();  // Get text from EditText

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return; // Stop if no input
        }

        // Save the name in SharedPreferences
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("last_name", inputText)
                .apply();

        // Save to Firebase Firestore or load existing progress
        handleLogin(inputText);
    }

    // Load user progress or create new user
    private void handleLogin(String name) {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String documentId = deviceId + "_" + name; // Unique ID for user + device

        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User exists → load progress
                        Long unlockedLevels = documentSnapshot.getLong("unlockedLevels");
                        if (unlockedLevels != null) {
                            ProgressStorage.setHighestUnlockedLevelOffline(this, unlockedLevels.intValue());
                        }
                        proceedToSecond(name); // Continue to next activity
                    } else {
                        // New user → save record
                        saveNewUser(name, deviceId, documentId);
                    }
                })
                .addOnFailureListener(e -> proceedToSecond(name)); // Fallback to offline
    }

    // Save a new user to Firestore
    private void saveNewUser(String name, String deviceId, String documentId) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("deviceId", deviceId);
        user.put("unlockedLevels", 1); // Start at level 1

        db.collection("users").document(documentId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    ProgressStorage.setHighestUnlockedLevelOffline(this, 1);
                    proceedToSecond(name);
                })
                .addOnFailureListener(e -> proceedToSecond(name)); // Fallback
    }

    // Proceed to next activity (Second)
    private void proceedToSecond(String name) {
        Intent intent = new Intent(this, Second.class);
        intent.putExtra("name", name); // Pass name to next screen
        startActivity(intent);
    }

    // Stop music when leaving activity
    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
    }
}
