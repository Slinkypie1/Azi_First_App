package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Third extends AppCompatActivity implements View.OnClickListener {
    TextView TV1;
    Button BtClick2;
    Button BtClick3;
    Button BtClick4;
    Button BtClick5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            initViews();
            return insets;
        });
    }

    private void initViews() {
        TV1 = findViewById(R.id.TV1);
        BtClick2 = findViewById(R.id.BtClick2);
        BtClick3 = findViewById(R.id.BtClick3);
        BtClick4 = findViewById(R.id.BtClick4);
        BtClick5 = findViewById(R.id.BtClick5);
        BtClick2.setOnClickListener(this);
        BtClick4.setOnClickListener(this);
        BtClick5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent =  new Intent(this, Failure.class);
        startActivity(intent);
    }
}