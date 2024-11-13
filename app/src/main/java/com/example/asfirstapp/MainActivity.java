package com.example.asfirstapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button BtCLick;
    EditText ET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            CharSequence channelName = "SlinkyPie\'s Quiz";
            String channelDescription = "A quiz on anything I can think of";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("SlinkyPie_Quiz", "SlinkyPie\'s Quiz", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("A Quiz about random things can you answer them all correct?");
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
        intent.putExtra("name", ET.getText() + "");
        startActivity(intent);
    }
}
