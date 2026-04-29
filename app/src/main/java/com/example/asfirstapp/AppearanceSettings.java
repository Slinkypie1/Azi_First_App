package com.example.asfirstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AppearanceSettings extends BaseMenuActivity {

    private RadioGroup rgBgColor;
    private RadioButton rbWhite, rbBlack;
    private Button btnSaveAppearance;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appearance_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        rgBgColor = findViewById(R.id.rgBgColor);
        rbWhite = findViewById(R.id.rbWhite);
        rbBlack = findViewById(R.id.rbBlack);
        btnSaveAppearance = findViewById(R.id.btnSaveAppearance);
        Button btnBackToSecond = findViewById(R.id.btnBackToSecond);

        // Load existing setting
        String bgColor = sharedPreferences.getString("bg_color", "white");
        if (bgColor.equals("black")) {
            rbBlack.setChecked(true);
        } else {
            rbWhite.setChecked(true);
        }

        btnSaveAppearance.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (rbBlack.isChecked()) {
                editor.putString("bg_color", "black");
            } else {
                editor.putString("bg_color", "white");
            }
            editor.apply();
            
            Toast.makeText(this, "Appearance Saved", Toast.LENGTH_SHORT).show();
            
            // Re-create the activity to apply changes immediately (if implemented in BaseMenuActivity)
            recreate();
        });

        btnBackToSecond.setOnClickListener(v -> {
            Intent intent = new Intent(AppearanceSettings.this, Second.class);
            startActivity(intent);
            finish();
        });
    }
}
