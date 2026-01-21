package com.example.asfirstapp; // Package declaration

// Imports needed for service, notifications, and Android version checks
import android.app.NotificationChannel;     // For creating notification channels (Android 8+)
import android.app.NotificationManager;     // To register notification channels
import android.app.Service;                 // Base class for all services
import android.content.Intent;              // Used to start the service or pass data
import android.os.Build;                    // To check the Android version
import android.os.IBinder;                  // Required for bound services
import androidx.annotation.Nullable;        // For nullable annotations
import androidx.core.app.NotificationCompat; // For building notifications

/**
 * MyForegroundService keeps the app alive in the foreground.
 * Foreground services must display a persistent notification.
 */
public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    // Unique ID for the notification channel

    @Override
    public void onCreate() {
        super.onCreate();
        // Called when the service is first created (once per lifecycle)
        createNotificationChannel();
        // Ensure the notification channel exists for Android 8.0+
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Build the notification that the user sees while the service runs
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running")
                // Notification title
                .setContentText("This app is staying active in the foreground.")
                // Notification description text
                .setSmallIcon(R.drawable.quiz_icon)
                // Small icon that appears in the status bar
                .setPriority(NotificationCompat.PRIORITY_LOW);
        // Low priority: minimal interruption for the user

        startForeground(1, notification.build());
        // Starts the service as a foreground service with notification ID 1

        return START_STICKY;
        // If the system kills this service, restart it automatically with null intent
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        // Stop foreground status and remove the notification
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // Not a bound service, so binding is not supported
    }

    /**
     * Creates a notification channel for Android 8.0+.
     * Required to show notifications in foreground services.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, // Unique channel ID
                    "Foreground Service Channel", // User-visible name of channel
                    NotificationManager.IMPORTANCE_LOW // Low importance (no sound)
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            // Get the system's notification manager
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                // Register the channel with the system
            }
        }
    }
}
