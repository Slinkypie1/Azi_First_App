package com.example.asfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CorrectScreen8 extends AppCompatActivity implements View.OnClickListener {

    Button BtClick20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_screen8);

//        BtClick20 = findViewById(R.id.BtClick2!);
        BtClick20.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, FindTheCountry.class);
        startActivity(intent);
    }
}
