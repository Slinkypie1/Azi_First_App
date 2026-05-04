package com.example.asfirstapp; // Package declaration

// Imports needed for notifications and broadcast handling
import android.app.NotificationChannel;       // For creating notification channels (Android 8+)
import android.app.NotificationManager;       // To manage and display notifications
import android.app.PendingIntent;             // Wraps intents to trigger later from notifications
import android.content.BroadcastReceiver;     // Receives broadcast events from system/app
import android.content.Context;               // Provides context for system services
import android.content.Intent;                // Used to specify which activity to open
import android.os.Build;                      // To check Android version
import android.util.Log;                      // For logging debug messages

import androidx.core.app.NotificationCompat;  // Support library for building notifications

/**
 * NotificationReceiver handles scheduled notifications.
 * Triggered by AlarmManager, it sends a notification if the app is not in the foreground.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    // Tag for logging debug messages

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "NotificationReceiver triggered.");
        // Called when the broadcast is received; used for debugging

        // Check if app is running (foreground) using MyApp class
        if (MyApp.isAppRunning(context)) {
            Log.d(TAG, "App is running, skipping notification.");
            // If app is already open, we do not show a notification
            return;
        }

        Log.d(TAG, "App is closed, sending notification.");
        // App is not open, so we proceed to send a notification

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Gets system service responsible for notifications

        if (notificationManager != null) {

            // Create notification channel (required for Android 8+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(
                        "slinkypie",                 // Unique channel ID
                        "Notifications",             // User-visible channel name
                        NotificationManager.IMPORTANCE_DEFAULT // Standard importance level
                );

                notificationManager.createNotificationChannel(channel);
                // Register channel with system
            }

            // Intent that opens MainActivity when notification is tapped
            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Prevents duplicate activity stack entries

            // Wrap intent in PendingIntent so notification can trigger it later
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0, // Request code (used if multiple notifications exist)
                    repeatingIntent,
                    PendingIntent.FLAG_IMMUTABLE // Required for security on newer Android versions
            );

            // Build the notification
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "slinkypie")
                            .setSmallIcon(R.drawable.app_icon)
                            // Icon shown in status bar

                            .setContentTitle("SlinkyPie's Quiz")
                            // Notification title

                            .setContentText("Come and play me!")
                            // Notification message

                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Normal priority notification

                            .setAutoCancel(true)
                            // Removes notification when tapped

                            .setContentIntent(pendingIntent);
            // Opens app when clicked

            // Display the notification
            notificationManager.notify(100, builder.build());
            Log.d(TAG, "Notification sent.");
            // Log confirmation
        }
    }
}