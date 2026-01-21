package com.example.asfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * ProgressStorage handles storing and syncing the highest unlocked level for a user.
 * Local progress is stored in SharedPreferences, and can optionally sync to Firebase.
 */
public class ProgressStorage {

    private static final String PREF_NAME = "game_progress"; // SharedPreferences file name
    private static final String KEY_HIGHEST_LEVEL = "highest_unlocked_level"; // Key for highest level
    private static final String TAG = "ProgressStorage"; // Tag for logging

    // Helper to get SharedPreferences instance
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the highest unlocked level locally stored.
     * Defaults to 1 if no progress has been saved yet.
     */
    public static int getHighestUnlockedLevel(Context context) {
        return getPrefs(context).getInt(KEY_HIGHEST_LEVEL, 1);
    }

    /**
     * Updates only the local progress.
     * Used during login to prevent double-syncing with Firebase.
     */
    public static void setHighestUnlockedLevelOffline(Context context, int level) {
        getPrefs(context)
                .edit()
                .putInt(KEY_HIGHEST_LEVEL, level)
                .apply(); // Apply changes asynchronously
    }

    /**
     * Updates the local progress and syncs it to Firebase for cloud persistence.
     */
    public static void setHighestUnlockedLevel(Context context, int level) {
        // Update local progress first
        setHighestUnlockedLevelOffline(context, level);

        // Then sync to Firebase
        syncToFirebase(context, level);
    }

    /**
     * Sends the current progress to Firebase for the user identified by deviceId + last_name.
     */
    private static void syncToFirebase(Context context, int level) {
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String savedName = appPrefs.getString("last_name", ""); // Last entered name
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (!savedName.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String documentId = deviceId + "_" + savedName; // Unique document ID per device + name

            db.collection("users").document(documentId)
                    .update("unlockedLevels", level) // Update the field in Firestore
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase progress updated for " + savedName))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase", e));
        }
    }
}
