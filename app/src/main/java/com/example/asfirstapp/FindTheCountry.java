package com.example.asfirstapp; // הגדרת החבילה אליה שייכת המחלקה הזו

import android.content.Intent; // דרוש למעבר בין אקטיביטיז (מסכים)
import android.os.Bundle; // דרוש עבור שיטות מחזור החיים של האקטיביטי כמו onCreate
import android.widget.TextView; // משמש להצגת השאלה הנוכחית ומשוב
import android.widget.Toast; // דרוש להצגת משוב עבור ניסיונות שגויים

import androidx.appcompat.app.AppCompatActivity; // מחלקת בסיס לאקטיביטיז עם תמיכה ב-AppCompat
import androidx.recyclerview.widget.GridLayoutManager; // מנהל פריסה עבור RecyclerView לסידור פריטים ברשת
import androidx.recyclerview.widget.RecyclerView; // RecyclerView להצגת אפשרויות המדינות כפריטים לבחירה

import java.util.*; // מייבא את כלי העזר List, ArrayList, Arrays, Collections

// אקטיביטי עבור פאזל "Find the Country" (מצא את המדינה)
public class FindTheCountry extends BaseMenuActivity {

    // רשימה של כל המדינות הזמינות עבור הפאזל
    String[] allCountries = {"India", "USA", "France", "Germany", "Japan", "Brazil", "Canada", "Italy", "China"};

    int correctCount = 0; // מספר הבחירות הנכונות שהמשתמש ביצע
    int wrongCount = 0;   // מספר הבחירות השגויות שהמשתמש ביצע

    TextView questionText; // מציג את השאלה "Find: Country"
    RecyclerView recyclerView; // מציג את המדינות לבחירה ברשת

    List<String> usedCountries = new ArrayList<>(); // עוקב אחר מדינות שכבר נעשה בהן שימוש בסשן הנוכחי

    private long startTime; // מתעד מתי הפאזל התחיל כדי למדוד את זמן הסיום

    // נקרא כאשר האקטיביטי נוצרת לראשונה
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה להגדרת ההורה
        setContentView(R.layout.activity_find_the_country); // טעינת קובץ ה-XML של הפריסה עבור אקטיביטי זו

        // הפעלת מוזיקת רקע עבור "Find The Country"
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("MUSIC_RES_ID", R.raw.find_the_country_music);
        startService(serviceIntent);

        startTime = System.currentTimeMillis(); // תיעוד זמן ההתחלה

        questionText = findViewById(R.id.questionText); // מציאת ה-TextView בפריסה
        recyclerView = findViewById(R.id.recyclerView); // מציאת ה-RecyclerView בפריסה

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // הגדרת ה-RecyclerView להצגת פריטים ברשת של 3 עמודות

        loadPuzzle(); // טעינת שאלת הפאזל הראשונה
    }

    // שיטה לטעינת פאזל מדינה חדש
    void loadPuzzle() {
        // התחלה עם כל המדינות והסרת אלו שכבר היו בשימוש
        List<String> availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        availableCountries.removeAll(usedCountries);

        // איפוס מדינות בשימוש אם כל המדינות כבר נוצלו
        if (availableCountries.isEmpty()) {
            usedCountries.clear();
            availableCountries = new ArrayList<>(Arrays.asList(allCountries));
        }

        Collections.shuffle(availableCountries); // ערבוב אקראי של סדר המדינות

        String answer = availableCountries.get(0); // בחירת המדינה הראשונה כתשובה הנכונה

        questionText.setText("Find: " + answer); // הצגת השאלה
        usedCountries.add(answer); // סימון מדינה זו ככזו שהייתה בשימוש

        // הכנת אפשרויות להצגה ב-RecyclerView
        List<String> optionsForDisplay = new ArrayList<>(Arrays.asList(allCountries));
        optionsForDisplay.remove(answer); // הסרת התשובה הנכונה מהאפשרויות השגויות

        Collections.shuffle(optionsForDisplay); // ערבוב האפשרויות השגויות

        List<String> finalOptions = new ArrayList<>();
        finalOptions.add(answer); // הוספת התשובה הנכונה ראשונה

        for (int i = 0; i < 5; i++) {
            finalOptions.add(optionsForDisplay.get(i)); // הוספת 5 אפשרויות שגויות
        }

        Collections.shuffle(finalOptions); // ערבוב האפשרויות הסופיות כך שמיקום התשובה יהיה אקראי

        // יצירת אובייקטי Region עבור המתאם של ה-RecyclerView
        List<Region> options = new ArrayList<>();
        for (String country : finalOptions) {
            String outlineDrawableId = "outline_" + country.toLowerCase(); // שם משאב התמונה עבור קווי המתאר של המדינה

            boolean isCorrect = country.equals(answer); // בדיקה האם אפשרות זו היא הנכונה

            options.add(new Region(country, outlineDrawableId, isCorrect));
        }

        // הגדרת המתאם של ה-RecyclerView עם טיפול בלחיצות
        recyclerView.setAdapter(new MapAdapter(this, options, isCorrect -> {
            if (isCorrect) {
                // נבחרה תשובה נכונה
                correctCount++; // העלאת מונה התשובות הנכונות

                TextView textView = findViewById(R.id.winOrLose);
                if (textView != null) textView.setText("Correct"); // הצגת משוב

                if (correctCount == 5) {
                    // הפאזל הושלם
                    long timeTaken = System.currentTimeMillis() - startTime;

                    Intent intent = new Intent(this, CorrectScreen8.class);
                    intent.putExtra("TIME_TAKEN", timeTaken);
                    startActivity(intent); // מעבר למסך הבא

                    finish(); // סגירת האקטיביטי
                } else {
                    loadPuzzle(); // טעינת השאלה הבאה
                }
            } else {
                // נבחרה תשובה שגויה
                wrongCount++; // העלאת מונה התשובות השגויות

                if (wrongCount >= 3) {
                    // יותר מדי תשובות שגויות
                    startActivity(new Intent(this, Failure.class));
                    finish();
                } else {
                    // הצגת מספר הניסיונות שנותרו
                    int remaining = 3 - wrongCount;
                    Toast.makeText(this, "Incorrect! " + remaining + " tries remaining.", Toast.LENGTH_SHORT).show();

                    // טעינה מחדש של הפאזל לאחר טעות
                    loadPuzzle();
                }
            }
        }));
    }
}