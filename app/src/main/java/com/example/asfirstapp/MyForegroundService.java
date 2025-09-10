package com.example.asfirstapp; // Package declaration

import android.app.NotificationChannel; // Used to create a notification channel on Android 8.0+
import android.app.NotificationManager; // To register notification channels
import android.app.Service; // Base class for all services
import android.content.Intent; // Used to start service or pass data
import android.os.Build; // To check Android version
import android.os.IBinder; // Required for bound services
import androidx.annotation.Nullable; // For nullable annotations
import androidx.core.app.NotificationCompat; // For building notifications

public class MyForegroundService extends Service { // Define custom foreground service
    private static final String CHANNEL_ID = "ForegroundServiceChannel"; // Unique ID for notification channel

    @Override
    public void onCreate() {
        super.onCreate(); // Call parent onCreate
        createNotificationChannel(); // Ensure notification channel exists (Android 8+)
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Build the notification for the foreground service
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running") // Title of the notification
                .setContentText("This app is staying active in the foreground.") // Description text
                .setSmallIcon(R.drawable.quiz_icon) // Icon shown in the status bar
                .setPriority(NotificationCompat.PRIORITY_LOW); // Low priority for minimal intrusion

        startForeground(1, notification.build()); // Start service in foreground with notification ID 1

        return START_STICKY; // Service restarts automatically if killed by the system
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); // Call parent onDestroy
        stopForeground(true); // Remove the foreground status and notification
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service, so return null
    }

    private void createNotificationChannel() {
        // Only required for Android 8.0+ (Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, // Unique channel ID
                    "Foreground Service Channel", // Human-readable channel name
                    NotificationManager.IMPORTANCE_LOW // Importance level: low
            );
            NotificationManager manager = getSystemService(NotificationManager.class); // Get system notification manager
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel); // Register the channel
            }
        }
    }
}
