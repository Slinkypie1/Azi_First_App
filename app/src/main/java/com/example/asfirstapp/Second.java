package com.example.asfirstapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.level_select, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){

            case R.id.level_1:
                Intent levelOne = new Intent(this, Third.class);
                startActivity(levelOne);
                return true;
            case R.id.level_2:
                Intent levelTwo = new Intent(this, SecondQuestion.class);
                startActivity(levelTwo);
                return true;
            case R.id.level_3:
                Intent levelThree = new Intent(this, ThirdQuestion.class);
                startActivity(levelThree);
                return true;
            case R.id.level_4:
                Intent levelFour = new Intent(this, Puzzle1.class);
                startActivity(levelFour);
                return true;
            case R.id.level_5:
                Intent levelFive = new Intent(this, Puzzle2.class);
                startActivity(levelFive);
                return true;
            case R.id.level_6:
                Intent levelSix = new Intent(this, Puzzle3.class);
                startActivity(levelSix);
                return true;
            case R.id.level_7:
                Intent levelSeven = new Intent(this, FillTheBlanks.class);
                startActivity(levelSeven);
                return true;
            case R.id.level_8:
                Intent levelEight = new Intent(this, FindTheCountry.class);
                startActivity(levelEight);
                return true;
            case R.id.level_9:
                Intent levelNine = new Intent(this, UnlockCityActivity.class);
                startActivity(levelNine);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
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


}