package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CorrectScreen9 extends AppCompatActivity implements View.OnClickListener {

    Button BtClick22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen9);

        BtClick22 = findViewById(R.id.BtClick22);
        BtClick22.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, UnlockCityActivity.class);
        startActivity(intent);
    }
}
