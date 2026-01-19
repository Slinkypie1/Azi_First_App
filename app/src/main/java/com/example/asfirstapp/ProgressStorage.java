package com.example.asfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProgressStorage {

    private static final String PREF_NAME = "game_progress";
    private static final String KEY_HIGHEST_LEVEL = "highest_unlocked_level";
    private static final String TAG = "ProgressStorage";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static int getHighestUnlockedLevel(Context context) {
        return getPrefs(context).getInt(KEY_HIGHEST_LEVEL, 1);
    }

    /**
     * Updates local progress ONLY. Used during login to prevent double-syncing.
     */
    public static void setHighestUnlockedLevelOffline(Context context, int level) {
        getPrefs(context)
                .edit()
                .putInt(KEY_HIGHEST_LEVEL, level)
                .apply();
    }

    public static void setHighestUnlockedLevel(Context context, int level) {
        // Save locally first
        setHighestUnlockedLevelOffline(context, level);

        // Sync to Firebase
        syncToFirebase(context, level);
    }

    private static void syncToFirebase(Context context, int level) {
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String savedName = appPrefs.getString("last_name", "");
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (!savedName.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String documentId = deviceId + "_" + savedName; // Use the same unique ID format

            db.collection("users").document(documentId)
                    .update("unlockedLevels", level)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase progress updated for " + savedName))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase", e));
        }
    }
}
