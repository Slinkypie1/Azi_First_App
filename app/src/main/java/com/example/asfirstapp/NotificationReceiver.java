package com.example.asfirstapp; // Package declaration

import android.app.NotificationChannel; // Used to create notification channels on Android 8.0+
import android.app.NotificationManager; // To manage and show notifications
import android.app.PendingIntent; // Allows notification to launch an activity when clicked
import android.content.BroadcastReceiver; // Receives system or app broadcast events
import android.content.Context; // Provides context for system services
import android.content.Intent; // Used to specify the target activity
import android.os.Build; // To check Android version
import android.util.Log; // Logging for debugging

import androidx.core.app.NotificationCompat; // Support library for notifications

public class NotificationReceiver extends BroadcastReceiver { // BroadcastReceiver triggered by alarms or system events
    private static final String TAG = "NotificationReceiver"; // Tag used for logging

    @Override
    public void onReceive(Context context, Intent intent) { // Called when the broadcast is received
        Log.d(TAG, "NotificationReceiver triggered."); // Log for debugging

        // Check if the app is currently running
        if (MyApp.isAppRunning(context)) {
            Log.d(TAG, "App is running, skipping notification."); // Log that no notification is sent
            return; // Do not send notification if app is in foreground
        }

        Log.d(TAG, "App is closed, sending notification."); // Log that notification will be sent

        // Get the system notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Create notification channel for Android 8.0+ devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "slinkypie", // Unique channel ID
                        "Notifications", // Human-readable channel name
                        NotificationManager.IMPORTANCE_DEFAULT // Importance level
                );
                notificationManager.createNotificationChannel(channel); // Register the channel
            }

            // Intent to open MainActivity when notification is clicked
            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Ensure the activity is brought to front

            // Wrap the intent in a PendingIntent so notification can trigger it
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0, // Request code
                    repeatingIntent,
                    PendingIntent.FLAG_IMMUTABLE // Immutable flag for security
            );

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "slinkypie")
                    .setSmallIcon(R.drawable.quiz_icon) // Notification icon
                    .setContentTitle("SlinkyPie's Quiz") // Title text
                    .setContentText("Come and play me!") // Body text
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Normal priority
                    .setAutoCancel(true) // Dismiss notification when clicked
                    .setContentIntent(pendingIntent); // Intent triggered on click

            // Show the notification with ID 100
            notificationManager.notify(100, builder.build());
            Log.d(TAG, "Notification sent."); // Log that notification was sent
        }
    }
}
