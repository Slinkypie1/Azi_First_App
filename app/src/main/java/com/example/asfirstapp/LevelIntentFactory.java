package com.example.asfirstapp;

import android.content.Context;
import android.content.Intent;

public class LevelIntentFactory {

    public static Intent getIntent(Context c, int level) {

        switch (level) {
            case 1: return new Intent(c, Third.class);
            case 2: return new Intent(c, SecondQuestion.class);
            case 3: return new Intent(c, ThirdQuestion.class);
            case 4: return new Intent(c, Puzzle1.class);
            case 5: return new Intent(c, Puzzle2.class);
            case 6: return new Intent(c, Puzzle3.class);
            case 7: return new Intent(c, FillTheBlanks.class);
            case 8: return new Intent(c, FindTheCountry.class);
            case 9: return new Intent(c, UnlockCityActivity.class);
        }

        return new Intent(c, Second.class);
    }
}
