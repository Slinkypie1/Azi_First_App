package com.example.asfirstapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Second extends AppCompatActivity implements View.OnClickListener {
    TextView TV;
    EditText ET;
    Button BtCLick1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            initViews();
            return insets;
        })
    ;}
    private void initViews() {
        TV = findViewById(R.id.TV);
        ET =  findViewById(R.id.ET);
        TV.setText("Ready " + getIntent().getStringExtra("name")+"?");
        BtCLick1 = findViewById(R.id.BtClick1);
        BtCLick1.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        Intent intent = new Intent(this, Third.class);
        startActivity(intent);
    }

    ;
}