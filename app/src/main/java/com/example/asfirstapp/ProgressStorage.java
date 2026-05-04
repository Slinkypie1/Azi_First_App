package com.example.asfirstapp;

import android.content.Context;
import android.content.SharedPreferences;  // For local storage
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;          // To get device ID
import android.util.Log;                   // Logging
import android.widget.Toast;

import com.google.firebase.firestore.FieldValue; // For server timestamps
import com.google.firebase.firestore.FirebaseFirestore; // Firestore database
import java.util.*; // Collections, Map, List, etc.

/**
 * ProgressStorage handles storing and syncing the highest unlocked level for a user.
 * Local progress is stored in SharedPreferences, and can optionally sync to Firebase.
 */
public class ProgressStorage {

    // ----- Constants -----
    private static final String PREF_NAME = "game_progress"; // SharedPreferences file name
    private static final String KEY_HIGHEST_LEVEL = "highest_unlocked_level"; // Key for highest level
    private static final String KEY_GAME_START_TIME = "game_start_time"; // Key for game start time
    private static final String KEY_PAUSED_TIME = "paused_time"; // Key for accumulated paused time
    private static final String KEY_ACHIEVEMENTS = "earned_achievements"; // Key for achievements string set
    private static final String TAG = "ProgressStorage"; // Tag for logging

    private static boolean didHitWallThisRun = false; // Flag for Perfectionist achievement

    // Achievement constants
    public static final String ACHIEV_SPEED_DEMON = "speed_demon";
    public static final String ACHIEV_PERFECTIONIST = "perfectionist";
    public static final String ACHIEV_WORLD_TRAVELER = "world_traveler";
    public static final String ACHIEV_RANKED = "ranked";
    public static final String ACHIEV_TOP_10 = "top_10";

    /**
     * Resets the wall-hit flag for a new perfectionist attempt.
     */
    public static void resetPerfectionistFlag() {
        didHitWallThisRun = false;
    }

    /**
     * Marks that a wall was hit during the current run.
     */
    public static void recordWallHit() {
        didHitWallThisRun = true;
    }

    /**
     * Checks if the player has hit any walls during the current run.
     */
    public static boolean wasWallHit() {
        return didHitWallThisRun;
    }

    // Helper to get SharedPreferences instance
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Gets the unique document ID for Firestore based on the user's email.
     */
    private static String getDocumentId(Context context) {
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String email = appPrefs.getString("last_email", "");
        if (email.isEmpty()) return null;
        return email.toLowerCase().replace(".", "_");
    }

    /**
     * Awards an achievement to the user.
     */
    public static void awardAchievement(Context context, String achievementId) {
        Set<String> earned = new HashSet<>(getPrefs(context).getStringSet(KEY_ACHIEVEMENTS, new HashSet<>()));
        
        if (!earned.contains(achievementId)) {
            earned.add(achievementId);
            getPrefs(context).edit().putStringSet(KEY_ACHIEVEMENTS, earned).apply();
            
            // Sync to Firebase
            syncAchievementsToFirebase(context, new ArrayList<>(earned));
            
            Log.d(TAG, "Achievement Unlocked: " + achievementId);

            // Show confirmation toast
            new Handler(Looper.getMainLooper()).post(() -> {
                String name = achievementId.replace("_", " ").toUpperCase();
                Toast.makeText(context, "🏆 Achievement Unlocked: " + name, Toast.LENGTH_LONG).show();
            });
        }
    }

    /**
     * Returns the set of earned achievement IDs.
     */
    public static Set<String> getEarnedAchievements(Context context) {
        return getPrefs(context).getStringSet(KEY_ACHIEVEMENTS, new HashSet<>());
    }

    private static void syncAchievementsToFirebase(Context context, List<String> achievements) {
        String documentId = getDocumentId(context);

        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(documentId)
                    .update("achievements", achievements)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase achievements updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase achievements", e));
        }
    }

    /**
     * Returns the highest unlocked level stored locally.
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
                .putInt(KEY_HIGHEST_LEVEL, level) // Save new level
                .apply(); // Apply changes asynchronously
    }

    /**
     * Updates the local progress AND syncs it to Firebase for cloud persistence.
     */
    public static void setHighestUnlockedLevel(Context context, int level) {
        // Update local progress first
        setHighestUnlockedLevelOffline(context, level);

        // Then sync to Firebase
        syncToFirebase(context, level);
    }

    /**
     * Updates the local achievements from a list.
     */
    public static void setAchievementsOffline(Context context, List<String> achievements) {
        if (achievements == null) return;
        getPrefs(context).edit().putStringSet(KEY_ACHIEVEMENTS, new HashSet<>(achievements)).apply();
    }

    /**
     * Sets the game start time.
     */
    public static void setGameStartTime(Context context, long startTime) {
        getPrefs(context).edit().putLong(KEY_GAME_START_TIME, startTime).apply();
    }

    /**
     * Gets the game start time. Returns 0 if not set.
     */
    public static long getGameStartTime(Context context) {
        return getPrefs(context).getLong(KEY_GAME_START_TIME, 0);
    }

    /**
     * Adds time to the total paused duration.
     * This time will be subtracted from the final total score.
     */
    public static void addPausedTime(Context context, long durationMillis) {
        long currentPaused = getPrefs(context).getLong(KEY_PAUSED_TIME, 0);
        getPrefs(context).edit().putLong(KEY_PAUSED_TIME, currentPaused + durationMillis).apply();
    }

    /**
     * Gets the total accumulated paused time.
     */
    public static long getTotalPausedTime(Context context) {
        return getPrefs(context).getLong(KEY_PAUSED_TIME, 0);
    }

    /**
     * Resets both start time and paused time for a new game.
     */
    public static void resetGameTimer(Context context) {
        getPrefs(context).edit()
                .putLong(KEY_GAME_START_TIME, System.currentTimeMillis())
                .putLong(KEY_PAUSED_TIME, 0)
                .apply();
    }

    /**
     * Sends the current progress to Firebase for the user identified by deviceId + last_name.
     */
    private static void syncToFirebase(Context context, int level) {
        String documentId = getDocumentId(context);

        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(documentId)
                    .update("unlockedLevels", level) // Update the field in Firestore
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase progress updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase progress", e));
        }
    }

    /**
     * Syncs the appearance (bg_color) to Firebase.
     */
    public static void syncAppearanceToFirebase(Context context, String bgColor) {
        String documentId = getDocumentId(context);

        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(documentId)
                    .update("bg_color", bgColor)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase appearance updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase appearance", e));
        }
    }

    /**
     * Syncs the game mode to Firebase.
     */
    public static void syncGameModeToFirebase(Context context, String gameMode) {
        String documentId = getDocumentId(context);

        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(documentId)
                    .update("game_mode", gameMode)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase game mode updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase game mode", e));
        }
    }

    /**
     * Syncs the music state (muted/unmuted) to Firebase.
     */
    public static void syncMusicToFirebase(Context context, boolean isMuted) {
        String documentId = getDocumentId(context);

        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(documentId)
                    .update("music_muted", isMuted)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase music state updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase music state", e));
        }
    }

    /**
     * Saves level completion time to Firebase if it's the player's best time.
     */
    public static void saveLevelCompletion(Context context, int level, long timeTakenMillis) {
        // Achievement: Speed Demon (under 5 seconds)
        if (timeTakenMillis < 5000) {
            awardAchievement(context, ACHIEV_SPEED_DEMON);
        }

        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        
        // If in casual mode, skip saving to the leaderboard
        String mode = appPrefs.getString("game_mode", "casual");
        if (mode.equals("casual")) {
            return;
        }

        String savedName = appPrefs.getString("last_name", "Anonymous");
        String savedEmail = appPrefs.getString("last_email", "");
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Use email for unique leaderboard entry if available, otherwise fallback to deviceId+name
        String identifier = !savedEmail.isEmpty() ? savedEmail.toLowerCase().replace(".", "_") : (deviceId + "_" + savedName);
        String entryId = identifier + "_level" + level;

        db.collection("leaderboard").document(entryId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long existingTime = documentSnapshot.getLong("timeTakenMillis");
                        if (existingTime != null && timeTakenMillis < existingTime) {
                            // New time is better (lower), update the entry
                            updateLeaderboardEntry(db, entryId, level, savedName, timeTakenMillis, deviceId);
                        } else {
                            Log.d(TAG, "New time (" + timeTakenMillis + ") is not better than existing best (" + existingTime + ")");
                        }
                    } else {
                        // No entry exists yet, create it
                        updateLeaderboardEntry(db, entryId, level, savedName, timeTakenMillis, deviceId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking leaderboard for best score", e));
    }

    /**
     * Helper to write a leaderboard entry to Firestore.
     */
    private static void updateLeaderboardEntry(FirebaseFirestore db, String entryId, int level, String name, long time, String deviceId) {
        Map<String, Object> completion = new HashMap<>();
        completion.put("level", level);                       // Level number
        completion.put("userName", name);                     // Player name
        completion.put("timeTakenMillis", time);             // Completion time in ms
        completion.put("timestamp", FieldValue.serverTimestamp()); // Server time
        completion.put("deviceId", deviceId);                // Player device ID

        db.collection("leaderboard").document(entryId)
                .set(completion)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Leaderboard entry updated/created for level " + level))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing leaderboard entry", e));
    }

    /**
     * Saves total game completion time to Firebase if it's the player's best total time.
     */
    public static void saveGameCompletion(Context context, long totalTimeMillis) {
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String savedName = appPrefs.getString("last_name", "Anonymous");
        String savedEmail = appPrefs.getString("last_email", "");
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        String identifier = !savedEmail.isEmpty() ? savedEmail.toLowerCase().replace(".", "_") : (deviceId + "_" + savedName);
        String entryId = identifier + "_total";

        db.collection("game_leaderboard").document(entryId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long existingTime = documentSnapshot.getLong("totalTimeMillis");
                        if (existingTime != null && totalTimeMillis < existingTime) {
                            updateGameLeaderboardEntry(db, entryId, savedName, totalTimeMillis, deviceId);
                        }
                    } else {
                        updateGameLeaderboardEntry(db, entryId, savedName, totalTimeMillis, deviceId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking game leaderboard", e));
    }

    private static void updateGameLeaderboardEntry(FirebaseFirestore db, String entryId, String name, long time, String deviceId) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("userName", name);
        entry.put("totalTimeMillis", time);
        entry.put("timestamp", FieldValue.serverTimestamp());
        entry.put("deviceId", deviceId);

        db.collection("game_leaderboard").document(entryId)
                .set(entry)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game leaderboard entry updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating game leaderboard", e));
    }

    /**
     * Interface for leaderboard results.
     */
    public interface LeaderboardCallback {
        void onLeaderboardLoaded(List<Map<String, Object>> entries); // Success
        void onError(Exception e);                                   // Failure
    }

    /**
     * Fetches top 10 completions for the entire game.
     */
    public static void getGameLeaderboard(Context context, LeaderboardCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("game_leaderboard")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> results = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        results.add(doc.getData());
                    }

                    Collections.sort(results, (o1, o2) -> {
                        Long t1 = (Long) o1.get("totalTimeMillis");
                        Long t2 = (Long) o2.get("totalTimeMillis");
                        if (t1 == null) return 1;
                        if (t2 == null) return -1;
                        return t1.compareTo(t2);
                    });

                    if (results.size() > 10) {
                        results = results.subList(0, 10);
                    }

                    // Achievement: Top 10
                    String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    for (Map<String, Object> entry : results) {
                        if (deviceId.equals(entry.get("deviceId"))) {
                            awardAchievement(context, ACHIEV_TOP_10);
                            break;
                        }
                    }

                    callback.onLeaderboardLoaded(results);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Fetches top 10 completions for a specific level.
     * Modified to sort locally to avoid Firestore index requirements.
     */
    public static void getLeaderboard(Context context, int level, LeaderboardCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("leaderboard")
                .whereEqualTo("level", level) // Filter by level
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> results = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        results.add(doc.getData()); // Add each document's data
                    }

                    // Sort locally by timeTakenMillis ascending (best times first)
                    Collections.sort(results, (o1, o2) -> {
                        Long t1 = (Long) o1.get("timeTakenMillis");
                        Long t2 = (Long) o2.get("timeTakenMillis");
                        if (t1 == null) return 1;
                        if (t2 == null) return -1;
                        return t1.compareTo(t2);
                    });

                    // Limit to top 10 entries
                    if (results.size() > 10) {
                        results = results.subList(0, 10);
                    }

                    // Achievement: Top 10
                    String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    for (Map<String, Object> entry : results) {
                        if (deviceId.equals(entry.get("deviceId"))) {
                            awardAchievement(context, ACHIEV_TOP_10);
                            break;
                        }
                    }

                    callback.onLeaderboardLoaded(results); // Return results
                })
                .addOnFailureListener(callback::onError);
    }
}
