package com.example.asfirstapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "NotificationReceiver triggered.");

        // Check if app is running
        if (MyApp.isAppRunning(context)) {
            Log.d(TAG, "App is running, skipping notification.");
            return; // Skip sending notifications if the app is running
        }

        Log.d(TAG, "App is closed, sending notification.");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "slinkypie",
                        "Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }

            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    repeatingIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "slinkypie")
                    .setSmallIcon(R.drawable.quiz_icon)
                    .setContentTitle("SlinkyPie's Quiz")
                    .setContentText("Come and play me!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(100, builder.build());
            Log.d(TAG, "Notification sent.");
        }
    }
}
