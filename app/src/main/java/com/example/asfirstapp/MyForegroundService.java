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

                .setSmallIcon(R.drawable.app_icon)
                // Small icon shown in the status bar

                .setPriority(NotificationCompat.PRIORITY_LOW);
        // Low priority reduces interruption (no sound/alert)

        startForeground(1, notification.build());
        // Promotes service to foreground so system keeps it alive

        return START_STICKY;
        // If system kills service, it will try to recreate it
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        // Removes the foreground notification and stops foreground mode
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // This service is not bound to any activity
    }

    /**
     * Creates a notification channel for Android 8.0+.
     * Required to show notifications in foreground services.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, // Unique channel ID
                    "Foreground Service Channel", // User-visible channel name
                    NotificationManager.IMPORTANCE_LOW // Low importance (silent)
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            // Access system notification manager

            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                // Register channel with system
            }
        }
    }
}