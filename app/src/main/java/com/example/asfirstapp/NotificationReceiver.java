package com.example.asfirstapp;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;

        if (powerManager != null){
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"NotificationReceiver::WakeLock");
        }
        if (wakeLock!=null){
            wakeLock.acquire(10000);
        }
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent =PendingIntent.getActivity(context, 0,repeatingIntent,PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder =new NotificationCompat.Builder(context,"slinkypie")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.quiz_icon)
                    .setContentTitle("SlinkyPie's Quiz")
                    .setContentText("Come and play me!")
                    .setAutoCancel(true);
            notificationManager.notify(100,builder.build());

        }
        catch(Exception e){
            Log.e(TAG,"Notification Failed",e);
        }
        finally {
            if (wakeLock!=null && wakeLock.isHeld()){
                wakeLock.release();
            }
        }



}
}
