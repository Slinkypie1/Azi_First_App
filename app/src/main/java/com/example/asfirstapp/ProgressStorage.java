package com.example.asfirstapp;

import android.content.Context;
import android.content.SharedPreferences;

public class ProgressStorage {

    private static final String PREF_NAME = "game_progress";
    private static final String KEY_HIGHEST_LEVEL = "highest_unlocked_level";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static int getHighestUnlockedLevel(Context context) {
        return getPrefs(context).getInt(KEY_HIGHEST_LEVEL, 1);
    }

    public static void setHighestUnlockedLevel(Context context, int level) {
        getPrefs(context)
                .edit()
                .putInt(KEY_HIGHEST_LEVEL, level)
                .apply();
    }
}
