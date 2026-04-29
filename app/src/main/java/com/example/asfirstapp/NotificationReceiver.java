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
    // Tag for logging

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "NotificationReceiver triggered.");
        // Called when the broadcast is received; log for debugging

        // Check if app is running (foreground) using MyApp class
        if (MyApp.isAppRunning(context)) {
            Log.d(TAG, "App is running, skipping notification.");
            // If app is open, do not notify
            return;
        }

        Log.d(TAG, "App is closed, sending notification.");
        // App is not open, so we send a notification

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Get the system notification manager

        if (notificationManager != null) {

            // For Android 8.0+ (Oreo), notification channels are required
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "slinkypie",                 // Unique ID for this channel
                        "Notifications",             // Human-readable name
                        NotificationManager.IMPORTANCE_DEFAULT // Importance level
                );
                notificationManager.createNotificationChannel(channel);
                // Register the channel with the system
            }

            // Create an intent that opens MainActivity when user taps the notification
            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Bring activity to front instead of creating a new one

            // Wrap the intent in a PendingIntent
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0, // Request code (unique if you have multiple notifications)
                    repeatingIntent,
                    PendingIntent.FLAG_IMMUTABLE // Makes PendingIntent secure
            );

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "slinkypie")
                    .setSmallIcon(R.drawable.app_icon) // Icon shown in status bar
                    .setContentTitle("SlinkyPie's Quiz") // Notification title
                    .setContentText("Come and play me!") // Notification body
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Normal priority
                    .setAutoCancel(true) // Notification disappears when clicked
                    .setContentIntent(pendingIntent); // Launches MainActivity on click

            // Show the notification with a fixed ID
            notificationManager.notify(100, builder.build());
            Log.d(TAG, "Notification sent."); // Log that the notification was displayed
        }
    }
}
