package com.example.asfirstapp;
// Defines the package name of your app (helps organize code and avoid conflicts).

import android.app.AlarmManager;              // Used to schedule tasks/notifications at specific times.
import android.app.NotificationChannel;       // Used to create notification channels (Android 8+).
import android.app.NotificationManager;       // Manages notifications for the app.
import android.app.PendingIntent;             // Grants permission to another app/component to run code later.
import android.content.Context;               // Provides app context (global information about the app).
import android.content.Intent;                // Used to start new activities/services or send broadcasts.
import android.content.pm.PackageManager;     // Used to check app permissions.
import android.os.Build;                      // Provides information about the device’s OS version.
import android.os.Bundle;                     // Holds saved state data for activity recreation.
import android.os.PowerManager;               // Manages power-related features like battery optimizations.
import android.provider.Settings;             // Lets you open system settings screens.
import android.util.Log;                      // For logging debug/error messages.
import android.view.View;                     // Basic building block for UI components.
import android.widget.Button;                 // Represents a button in the UI.
import android.widget.EditText;               // Represents a text input field in the UI.
import android.widget.Toast;                  // Small popup messages for user feedback.

import androidx.annotation.NonNull;           // Annotation to prevent null values in parameters.
import androidx.appcompat.app.AppCompatActivity; // Base class for activities using modern UI features.
import androidx.core.app.ActivityCompat;      // Helps request runtime permissions.
import androidx.core.content.ContextCompat;   // Helps check if permissions are granted.

import com.google.firebase.firestore.FirebaseFirestore; // For Firebase Firestore.
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseMenuActivity implements View.OnClickListener {
    // MainActivity class extends AppCompatActivity (so it can act as a screen).
    // Implements View.OnClickListener to handle button clicks.

    Button BtCLick; // A reference to the button in the layout.
    EditText ET;    // A reference to the EditText field.
    private static final int PERMISSION_REQUEST_CODE = 100; // Constant for permission request ID.
    private static final String TAG = "MainActivity";       // Tag for logging messages.

    private FirebaseFirestore db; // Reference to Firestore database.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                 // Calls parent method to set up activity.
        setContentView(R.layout.activity_main);             // Sets UI layout to activity_main.xml.

        db = FirebaseFirestore.getInstance();               // Initialize Firestore.

        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);                        // Starts background music service.

        initViews();                                        // Finds button & EditText in layout.

        startForegroundService();                           // Starts foreground service (keeps alive).

        // If Android version >= TIRAMISU (Android 13) → check notification permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Ask the user for notification permission if not granted.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {
                setupNotification();                        // If permission granted, setup notifications.
            }
        } else {
            setupNotification();                            // For older Android versions, no permission needed.
        }

        checkBatteryOptimization();                         // Check if app is ignored from battery optimization.
    }

    private void setupNotification() {
        createNotificationChannel(); // Create notification channel if Android 8+.
        scheduleNotification();      // Schedule daily notification.
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Notification channels required on Android 8+.
            String channelId = "slinkypie";                   // Channel ID.
            CharSequence channelName = "Notifications";       // Channel name.
            String channelDescription = "Channel for notifications"; // Channel description.
            int importance = NotificationManager.IMPORTANCE_DEFAULT; // Importance level.
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);        // Set description.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel); // Register the channel.
            }
        }
    }

    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent); // For Android 8+, must call startForegroundService().
        } else {
            startService(serviceIntent);           // For older versions, normal startService().
        }
    }

    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Gets system alarm manager for scheduling.

        Intent intent = new Intent(this, NotificationReceiver.class);
        // Intent to trigger NotificationReceiver when alarm fires.

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);
        // PendingIntent that will send broadcast to NotificationReceiver.

        long triggerTime = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY;
        // Sets first trigger time to 1 day from now.

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,         // Wakes device if asleep.
                    triggerTime,                     // Start time.
                    AlarmManager.INTERVAL_DAY,       // Repeat interval = 1 day.
                    pendingIntent                    // What to run.
            );
            Log.d(TAG, "Alarm scheduled for daily notifications.");
            // Debug log message.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Called after user responds to a permission request.

        if (requestCode == PERMISSION_REQUEST_CODE) { // If it’s our notification permission request.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotification(); // If granted, set up notifications.
            } else {
                Toast.makeText(this,
                        "Notification permission is required for this feature.",
                        Toast.LENGTH_LONG).show();
                // Show warning if user denied permission.
            }
        }
    }

    private void checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Battery optimization introduced in Android 6.
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                // If app is not exempt from battery optimizations...
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent); // Open settings screen so user can exempt it.
            }
        }
    }

    private void initViews() {
        BtCLick = findViewById(R.id.BtClick);   // Finds button in XML layout.
        BtCLick.setOnClickListener(this);       // Set MainActivity as the click handler.
        ET = findViewById(R.id.ET);             // Finds EditText in XML layout.
    }

    @Override
    public void onClick(View view) {
        String inputText = ET.getText().toString().trim();  // Get text from EditText

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save it in SharedPreferences
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("last_name", inputText)
                .apply();

        // Save to Firebase Firestore and load progress
        handleLogin(inputText);
    }

    private void handleLogin(String name) {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String documentId = deviceId + "_" + name; // Unique ID per name per device

        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User exists on this device, load their progress
                        Long unlockedLevels = documentSnapshot.getLong("unlockedLevels");
                        if (unlockedLevels != null) {
                            ProgressStorage.setHighestUnlockedLevelOffline(this, unlockedLevels.intValue());
                        }
                        proceedToSecond(name);
                    } else {
                        // New user for this device - Create new record
                        saveNewUser(name, deviceId, documentId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Fallback to offline if firebase fails
                    proceedToSecond(name);
                });
    }

    private void saveNewUser(String name, String deviceId, String documentId) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("deviceId", deviceId);
        user.put("unlockedLevels", 1); // Start at level 1 for new users

        db.collection("users").document(documentId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    ProgressStorage.setHighestUnlockedLevelOffline(this, 1);
                    proceedToSecond(name);
                })
                .addOnFailureListener(e -> proceedToSecond(name));
    }

    private void proceedToSecond(String name) {
        Intent intent = new Intent(this, Second.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
        // Stop music service when activity is paused (user leaves screen).
    }
}
