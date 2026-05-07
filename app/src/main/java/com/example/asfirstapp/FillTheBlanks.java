package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // משמש לניווט בין אקטיביטיז (מסכים)
import android.os.Bundle; // משמש לנתוני מחזור החיים של האקטיביטי
import android.view.View; // מחלקת הבסיס לאינטראקציות ממשק משתמש
import android.widget.*; // מייבא יישומוני ממשק משתמש כמו Spinner, Button, Toast

import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטי (לא בשימוש ישיר כי BaseMenuActivity מרחיבה אותה)

// אקטיביטי עבור חידון "Fill the Blanks" (השלם את החסר)
public class FillTheBlanks extends BaseMenuActivity {

    // כל האפשרויות האפשריות שיוצגו בתפריטים הנפתחים
    String[] choices = {"choose here", "jumping", "rising", "falling", "happiness", "glory", "walking"};

    // התשובות הנכונות לפי הסדר עבור כל מקום חסר
    String[] correctAnswers = {"glory", "falling", "rising"};

    // מערך המחזיק את 3 רכיבי ה-Spinner (תפריטים נפתחים) בממשק המשתמש
    Spinner[] spinners = new Spinner[3];

    private long startTime; // שומר מתי החידון התחיל (לצורך תזמון)
    private int attempts = 0; // עוקב אחר ניסיונות שגויים

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // קריאה להגדרת ההורה
        setContentView(R.layout.activity_fill_the_blanks); // טעינת קובץ ה-XML של הפריסה

        // הפעלת מוזיקת רקע עבור שלב זה
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.fill_the_blanks_music);
        startService(serviceIntent);

        startTime = System.currentTimeMillis(); // תיעוד זמן ההתחלה

        // קישור רכיבי ה-Spinner מה-XML
        spinners[0] = findViewById(R.id.spinner1);
        spinners[1] = findViewById(R.id.spinner2);
        spinners[2] = findViewById(R.id.spinner3);

        // קישור כפתור ההגשה
        Button submitButton = findViewById(R.id.submitButton);

        // יצירת מתאם (Adapter) להצגת אפשרויות התפריט הנפתח
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                choices
        );

        // חיבור המתאם לכל Spinner
        for (Spinner spinner : spinners) {
            spinner.setAdapter(adapter);
        }

        // טיפול בלחיצה על כפתור ההגשה
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean allCorrect = true; // מעקב האם כל התשובות נכונות

                // בדיקת כל תשובה ב-Spinner
                for (int i = 0; i < spinners.length; i++) {

                    String selectedAnswer = spinners[i].getSelectedItem().toString();
                    // קבלת הערך שנבחר מה-Spinner

                    if (!selectedAnswer.equals(correctAnswers[i])) {
                        // אם התשובה שגויה
                        allCorrect = false;
                        break; // הפסקת הבדיקה מוקדם
                    }
                }

                // אם כל התשובות נכונות ← הצלחה
                if (allCorrect) {

                    long timeTaken = System.currentTimeMillis() - startTime;
                    // חישוב זמן הסיום

                    Intent intent = new Intent(FillTheBlanks.this, CorrectScreen7.class);
                    // מעבר למסך ההצלחה

                    intent.putExtra("TIME_TAKEN", timeTaken);
                    // העברת הזמן למסך הבא

                    startActivity(intent);
                    finish(); // סגירת אקטיביטי זו
                }

                // אם התשובות שגויות
                else {
                    attempts++; // העלאת מונה הכישלונות

                    if (attempts >= 2) {
                        // יותר מדי ניסיונות ← מסך כישלון
                        Intent intent = new Intent(FillTheBlanks.this, Failure.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // טעות ראשונה ← הצגת אזהרה
                        Toast.makeText(FillTheBlanks.this,
                                "Incorrect! 1 try remaining.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}