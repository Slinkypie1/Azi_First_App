package com.example.asfirstapp;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button BtCLick;
    EditText ET;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
        initViews();
        startForegroundService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            } else {
                setupNotification();
            }
        } else {
            setupNotification();
        }
        checkBatteryOptimization();
    }

    private void setupNotification() {
        createNotificationChannel();
        scheduleNotification();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "slinkypie";
            CharSequence channelName = "Notifications";
            String channelDescription = "Channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }



    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY;

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
            Log.d(TAG, "Alarm scheduled for daily notifications.");
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotification();
            } else {
                Toast.makeText(this, "Notification permission is required for this feature.", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void initViews() {
        BtCLick = findViewById(R.id.BtClick);
        BtCLick.setOnClickListener(this);
        ET = findViewById(R.id.ET);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Second.class);
        String inputText = ET.getText().toString();
        intent.putExtra("name", inputText);
        startActivity(intent);

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class)); // Stop the music when the app goes to the background
    }

}