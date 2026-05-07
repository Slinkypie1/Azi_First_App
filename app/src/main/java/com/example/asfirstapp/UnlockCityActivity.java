package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

// ייבוא עבור ממשק משתמש של אנדרואיד, מפות, מיקום וניווט
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UnlockCityActivity
 * ------------------
 * פאזל מבוסס מפה שבו השחקן "פותח" ערים על ידי ניחוש המיקום שלהן.
 * השחקן מזיז את המפה למיקום הנכון ומגיש ניחוש.
 * לאחר מספר מוגדר של ניחושים נכונים, השחקן מתקדם למסך ההצלחה.
 * לשחקן יש 3 לבבות; איבוד כולם מוביל לכישלון.
 */
public class UnlockCityActivity extends BaseMenuActivity implements OnMapReadyCallback {

    private GoogleMap mMap;           // מופע של מפת גוגל המשמש למשחק
    private TextView clueText;        // מציג את הרמז עבור העיר הנוכחית
    private TextView heartsText;      // מציג את מספר הלבבות שנותרו
    private Button submitGuessBtn;    // כפתור להגשת ניחוש המפה של המשתמש

    private List<City> cityList = new ArrayList<>();   // רשימה ראשית של כל הערים הזמינות
    private List<City> shuffledCities;                // רשימה מעורבבת המשמשת במהלך המשחק
    private int currentIndex = 0;                     // עוקב אחר המיקום ברשימה המעורבבת
    private City currentCity;                         // העיר הנוכחית שהשחקן צריך לנחש

    private int correctGuessCount = 0;               // מספר הערים שנוחשו נכונה
    private int hearts = 3;                          // חיי השחקן
    private static final int TOTAL_CORRECT_TO_FINISH = 6; // מספר ניחושים נכונים נדרש לניצחון
    private static final float ALLOWED_RADIUS_METERS = 500_000; // רדיוס שגיאה מקובל (500 ק"מ)

    private long startTime;                           // עוקב אחר זמן תחילת השלב
    private long pauseStartTime;                      // עוקב אחר מתי הוצג דיאלוג ההוראות

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לשיטת ההורה

        // טעינת פריסת האקטיביטי
        setContentView(R.layout.activity_unlock_city);

        // הפעלת מוזיקת רקע עבור שלב "Unlock City"
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.unlock_city_music);
        startService(serviceIntent);

        // קישור רכיבי ממשק משתמש לתצוגות בפריסה
        clueText = findViewById(R.id.clueText);
        heartsText = findViewById(R.id.heartsText);
        submitGuessBtn = findViewById(R.id.submitGuessBtn);
        submitGuessBtn.setEnabled(false); // מושבת עד לאישור ההוראות

        // הצגת הוראות לפני תחילת המשחק
        showInstructions();

        // אתחול תצוגת ממשק המשתמש של הלבבות
        updateHeartsUI();

        // טעינה והכנה של נתוני הערים
        setupCities();
        shuffleCities();

        // אתחול רכיב מפות גוגל בצורה אסינכרונית
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // מפעיל את onMapReady כשהמפה נטענת
        }

        // טיפול בלחיצה על כפתור הגשת הניחוש
        submitGuessBtn.setOnClickListener(v -> {
            if (mMap == null || currentCity == null) return; // בדיקת בטיחות

            // שימוש במרכז המפה כמיקום הניחוש של השחקן
            LatLng guess = mMap.getCameraPosition().target;

            // חישוב המרחק בין הניחוש למיקום העיר בפועל
            float[] distanceResult = new float[1];
            Location.distanceBetween(
                    guess.latitude, guess.longitude,
                    currentCity.location.latitude, currentCity.location.longitude,
                    distanceResult);

            if (distanceResult[0] <= ALLOWED_RADIUS_METERS) {
                // ניחוש נכון
                correctGuessCount++;
                Toast.makeText(this,
                        "✅ Correct! You unlocked " + currentCity.name + "!",
                        Toast.LENGTH_LONG).show();

                if (correctGuessCount >= TOTAL_CORRECT_TO_FINISH) {
                    // הענקת הישג עבור סיום
                    ProgressStorage.awardAchievement(this, ProgressStorage.ACHIEV_WORLD_TRAVELER);

                    // סיום השלב ומעבר למסך הצלחה
                    long timeTaken = System.currentTimeMillis() - startTime;
                    Intent intent = new Intent(this, CorrectScreen9.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    startActivity(intent);
                    finish();
                } else {
                    // טעינת העיר הבאה
                    pickRandomCity();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
                }
            } else {
                // ניחוש שגוי ← איבוד לב
                hearts--;
                updateHeartsUI();

                if (hearts <= 0) {
                    // מצב סיום משחק (הפסד)
                    Toast.makeText(this, "❌ No hearts left!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Failure.class);
                    startActivity(intent);
                    finish();
                } else {
                    // אפשור ניסיון חוזר
                    Toast.makeText(this, "❌ Incorrect! " + hearts + " hearts left.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * מעדכן את תצוגת הלבבות באמצעות אימוג'י.
     */
    private void updateHeartsUI() {
        StringBuilder sb = new StringBuilder();

        // בניית מחרוזת לבבות לפי החיים שנותרו
        for (int i = 0; i < hearts; i++) {
            sb.append("❤️");
        }

        if (heartsText != null) {
            heartsText.setText(sb.toString());
        }
    }

    /**
     * מציג הוראות לפני תחילת המשחק.
     * הטיימר והאינטראקציה מופעלים רק לאחר אישור.
     */
    private void showInstructions() {

        // בדיקת מצב המשחק (רגיל או מתוזמן)
        String mode = ProgressStorage.getAppPrefs(this)
                .getString("game_mode", "casual");

        if (mode.equals("timed")) {
            // דילוג על הוראות במצב מתוזמן
            startTime = System.currentTimeMillis();
            submitGuessBtn.setEnabled(true);
            return;
        }

        pauseStartTime = System.currentTimeMillis();

        // הצגת דיאלוג הוראות
        new AlertDialog.Builder(this)
                .setTitle("Level 9: Unlock the Cities")
                .setMessage("A clue will appear for a famous city.\n\n" +
                        "1. Move the map to your guess.\n" +
                        "2. Tap 'Submit Guess'.\n" +
                        "3. Unlock " + TOTAL_CORRECT_TO_FINISH + " cities to win.\n\n" +
                        "You only have 3 hearts.")
                .setCancelable(false)
                .setPositiveButton("Start Guessing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // מעקב אחר זמן השהיה (עבור דיוק בניקוד)
                        long pausedDuration = System.currentTimeMillis() - pauseStartTime;
                        ProgressStorage.addPausedTime(UnlockCityActivity.this, pausedDuration);

                        // התחלת טיימר המשחק
                        startTime = System.currentTimeMillis();
                        submitGuessBtn.setEnabled(true);
                    }
                })
                .show();
    }

    /**
     * מאתחל את כל הערים המשמשות במשחק.
     */
    private void setupCities() {
        cityList.add(new City("Paris", new LatLng(48.8566, 2.3522), "This city is home to the Eiffel Tower."));
        cityList.add(new City("New York", new LatLng(40.7128, -74.0060), "Known as the Big Apple."));
        cityList.add(new City("Tokyo", new LatLng(35.6895, 139.6917), "Famous for its cherry blossoms and technology."));
        cityList.add(new City("London", new LatLng(51.5074, -0.1278), "The home of Big Ben."));
        cityList.add(new City("Sydney", new LatLng(-33.8688, 151.2093), "Famous for its Opera House."));
        cityList.add(new City("Rio de Janeiro", new LatLng(-22.9068, -43.1729), "Known for Christ the Redeemer."));
        cityList.add(new City("Cairo", new LatLng(30.0444, 31.2357), "Near the Great Pyramids."));
        cityList.add(new City("Moscow", new LatLng(55.7558, 37.6173), "Famous for the Kremlin."));
        cityList.add(new City("Rome", new LatLng(41.9028, 12.4964), "Known for the Colosseum."));
        cityList.add(new City("Dubai", new LatLng(25.2048, 55.2708), "Home to the tallest building."));
        cityList.add(new City("Berlin", new LatLng(52.5200, 13.4050), "Famous for the Berlin Wall."));
        cityList.add(new City("San Francisco", new LatLng(37.7749, -122.4194), "Known for the Golden Gate Bridge."));
    }

    /**
     * מערבב את סדר הערים עבור המשחק.
     */
    private void shuffleCities() {
        shuffledCities = new ArrayList<>(cityList);
        Collections.shuffle(shuffledCities);
        currentIndex = 0;
    }

    /**
     * בוחר את העיר הבאה ומעדכן את טקסט הרמז.
     */
    private void pickRandomCity() {
        if (shuffledCities == null || shuffledCities.isEmpty()) {
            shuffleCities();
        }

        if (currentIndex >= shuffledCities.size()) {
            shuffleCities();
        }

        currentCity = shuffledCities.get(currentIndex);
        currentIndex++;
        clueText.setText(currentCity.clue);
    }

    /**
     * נקרא כאשר מפת גוגל מוכנה לשימוש.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        pickRandomCity();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
    }
}