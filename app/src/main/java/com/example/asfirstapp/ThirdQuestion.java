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

public class ThirdQuestion extends AppCompatActivity implements View.OnClickListener {
TextView TV3;
Button BtClick11;
Button BtClick12;
Button BtClick13;
Button BtClick14;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        initViews();
            return insets;
        });
    }

    private void initViews() {
        TV3 = findViewById(R.id.TV3);
        BtClick11 = findViewById(R.id.BtClick11);
        BtClick12 = findViewById(R.id.BtClick12);
        BtClick13 = findViewById(R.id.BtClick13);
        BtClick14 = findViewById(R.id.BtClick14);
        BtClick11.setOnClickListener(this);
        BtClick12.setOnClickListener(this);
        BtClick13.setOnClickListener(this);
        BtClick14.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == BtClick13){
            Intent intent = new Intent(this, CorrectScreen3.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, Failure.class);
            startActivity(intent);
        }
    }
}