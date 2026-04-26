package com.example.asfirstapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameSettings extends BaseMenuActivity {

    private RadioGroup rgGameMode;
    private RadioButton rbCasual, rbTimed;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        rgGameMode = findViewById(R.id.rgGameMode);
        rbCasual = findViewById(R.id.rbCasual);
        rbTimed = findViewById(R.id.rbTimed);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Load existing setting
        String mode = sharedPreferences.getString("game_mode", "casual");
        if (mode.equals("timed")) {
            rbTimed.setChecked(true);
        } else {
            rbCasual.setChecked(true);
        }

        btnSaveSettings.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (rbTimed.isChecked()) {
                editor.putString("game_mode", "timed");
            } else {
                editor.putString("game_mode", "casual");
            }
            editor.apply();
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the previous activity
        });
    }
}