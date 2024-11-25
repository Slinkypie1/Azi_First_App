package com.example.asfirstapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.PowerManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import static androidx.core.content.ContextCompat.startActivity;
import  android.Manifest;
import android.content.pm.PackageManager;
import  androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button BtCLick;
    EditText ET;

    public void sendNotification(Context context) {
        String channelId = "slinkypie_channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.quiz_icon).setContentTitle("SlinkyPie1").setContentText("Come play with me again please").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(1, builder.build());
            } else {
                Toast.makeText(context, "Notification permission not granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checkAndRequestPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                requestPermissions(new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 1);
            } else {
                // Schedule the notification if permission is already granted
                scheduleNotification();
            }
        } else {
            // No need for permission on versions below Android 12
            scheduleNotification();
        }
        checkAndRequestPermissionForExactAlarm();
        checkAndRequestPermissions();
        requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if(isGranted){
                    scheduleNotification();
                }
                else{
                    Toast.makeText(MainActivity.this,"Permission denied. Notifications won't be scheduled.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );

    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM);
            } else {
            scheduleNotification();
            }
        } else {
            scheduleNotification();
        }
    }

    private void checkAndRequestPermissionForExactAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (Settings.canDrawOverlays(this)) {
                if (checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 1);
                } else {
                    scheduleNotification();
                }
            } else {
                Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_SHORT).show();
            }
        } else {
            scheduleNotification();
        }
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "slinkypie_channel";
            CharSequence channelName = "SlinkyPie";
            String channelDescription = "A Quiz about random things";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.e("Notification", "NotificationManager is null");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scheduleNotification();
        } else {
            Toast.makeText(this, "Permission denied, cannot schedule notifications", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("NewApi")
    public void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds from now

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }


    private void requestExactAlarmPermission() {
       requestPermissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM);
    }

    private void requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==1){
            if (checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)==PackageManager.PERMISSION_GRANTED){
                Log.d("Permission", "SCHEDULE_EXACT_ALARM permission granted.");
                scheduleNotification();
            }
            else{
                Log.d("Permission", "SCHEDULE_EXACT_ALARM permission denied.");
                Toast.makeText(this, "Permission denied. Notifications won't be scheduled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}