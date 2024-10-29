package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Failure extends AppCompatActivity implements View.OnClickListener {
    Button BtClickLose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_failure_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            initViews();
            return insets;
        });
    }

    private void initViews() {
        BtClickLose = findViewById(R.id.BtClickLose);
        BtClickLose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent  = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}