package com.example.asfirstapp;

import android.content.Context; // Needed for SharedPreferences, system services, etc.
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
    private static final String KEY_HIGHEST_LEVEL = "highest_unlocked_level"; // Stores highest level reached
    private static final String KEY_GAME_START_TIME = "game_start_time"; // Stores when game started
    private static final String KEY_PAUSED_TIME = "paused_time"; // Tracks total paused duration
    private static final String KEY_ACHIEVEMENTS = "earned_achievements"; // Stores unlocked achievements
    private static final String TAG = "ProgressStorage"; // Log tag

    private static boolean didHitWallThisRun = false; // Tracks if player hit a wall (for achievement logic)

    // Achievement constants
    public static final String ACHIEV_SPEED_DEMON = "speed_demon";
    public static final String ACHIEV_PERFECTIONIST = "perfectionist";
    public static final String ACHIEV_WORLD_TRAVELER = "world_traveler";
    public static final String ACHIEV_RANKED = "ranked";
    public static final String ACHIEV_TOP_10 = "top_10";

    /**
     * Resets wall-hit tracking for a new run
     */
    public static void resetPerfectionistFlag() {
        didHitWallThisRun = false;
    }

    /**
     * Marks that a wall was hit in current run
     */
    public static void recordWallHit() {
        didHitWallThisRun = true;
    }

    /**
     * Returns whether player hit a wall this run
     */
    public static boolean wasWallHit() {
        return didHitWallThisRun;
    }

    // Gets SharedPreferences instance for game progress (user-specific)
    private static SharedPreferences getPrefs(Context context) {
        String uid = getDocumentId(context);
        String prefName = (uid != null) ? (PREF_NAME + "_" + uid) : PREF_NAME;
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    // Gets SharedPreferences instance for app settings (user-specific)
    public static SharedPreferences getAppPrefs(Context context) {
        String uid = getDocumentId(context);
        String prefName = (uid != null) ? ("app_prefs_" + uid) : "app_prefs";
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    /**
     * Builds Firestore document ID using Firebase User UID
     */
    public static String getDocumentId(Context context) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            return com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    /**
     * Adds a new achievement if not already earned
     */
    public static void awardAchievement(Context context, String achievementId) {
        Set<String> earned = new HashSet<>(getPrefs(context).getStringSet(KEY_ACHIEVEMENTS, new HashSet<>()));

        if (!earned.contains(achievementId)) {
            earned.add(achievementId);
            getPrefs(context).edit().putStringSet(KEY_ACHIEVEMENTS, earned).apply();

            // Sync updated achievements to Firebase
            syncAchievementsToFirebase(context, new ArrayList<>(earned));

            Log.d(TAG, "Achievement Unlocked: " + achievementId);

            // Show popup notification on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                String name = achievementId.replace("_", " ").toUpperCase();
                Toast.makeText(context, "🏆 Achievement Unlocked: " + name, Toast.LENGTH_LONG).show();
            });
        }
    }

    /**
     * Returns locally stored achievements
     */
    public static Set<String> getEarnedAchievements(Context context) {
        return getPrefs(context).getStringSet(KEY_ACHIEVEMENTS, new HashSet<>());
    }

    /**
     * Sync achievements list to Firebase
     */
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
     * Get highest unlocked level (local)
     */
    public static int getHighestUnlockedLevel(Context context) {
        return getPrefs(context).getInt(KEY_HIGHEST_LEVEL, 1);
    }

    /**
     * Save level locally only (no Firebase sync)
     */
    public static void setHighestUnlockedLevelOffline(Context context, int level) {
        getPrefs(context)
                .edit()
                .putInt(KEY_HIGHEST_LEVEL, level)
                .apply();
    }

    /**
     * Save level locally AND sync to Firebase
     */
    public static void setHighestUnlockedLevel(Context context, int level) {
        setHighestUnlockedLevelOffline(context, level);
        syncToFirebase(context, level);
    }

    /**
     * Save achievements locally
     */
    public static void setAchievementsOffline(Context context, List<String> achievements) {
        if (achievements == null) return;
        getPrefs(context).edit().putStringSet(KEY_ACHIEVEMENTS, new HashSet<>(achievements)).apply();
    }

    /**
     * Store game start time
     */
    public static void setGameStartTime(Context context, long startTime) {
        getPrefs(context).edit().putLong(KEY_GAME_START_TIME, startTime).apply();
    }

    /**
     * Get game start time
     */
    public static long getGameStartTime(Context context) {
        return getPrefs(context).getLong(KEY_GAME_START_TIME, 0);
    }

    /**
     * Add paused time to total pause duration
     */
    public static void addPausedTime(Context context, long durationMillis) {
        long currentPaused = getPrefs(context).getLong(KEY_PAUSED_TIME, 0);
        getPrefs(context).edit().putLong(KEY_PAUSED_TIME, currentPaused + durationMillis).apply();
    }

    /**
     * Get total paused time
     */
    public static long getTotalPausedTime(Context context) {
        return getPrefs(context).getLong(KEY_PAUSED_TIME, 0);
    }

    /**
     * Reset timer for new game
     */
    public static void resetGameTimer(Context context) {
        getPrefs(context).edit()
                .putLong(KEY_GAME_START_TIME, System.currentTimeMillis())
                .putLong(KEY_PAUSED_TIME, 0)
                .apply();
    }

    /**
     * Sync level progress to Firebase
     */
    private static void syncToFirebase(Context context, int level) {
        String documentId = getDocumentId(context);

        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(documentId)
                    .update("unlockedLevels", level)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase progress updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating firebase progress", e));
        }
    }

    /**
     * Sync appearance setting to Firebase
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
     * Sync game mode to Firebase
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
     * Sync music mute state to Firebase
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
     * Save level completion time (leaderboard logic)
     */
    public static void saveLevelCompletion(Context context, int level, long timeTakenMillis) {

        // Speed Demon achievement check
        if (timeTakenMillis < 5000) {
            awardAchievement(context, ACHIEV_SPEED_DEMON);
        }

        SharedPreferences appPrefs = getAppPrefs(context);

        String mode = appPrefs.getString("game_mode", "casual");
        if (mode.equals("casual")) {
            return; // skip leaderboard in casual mode
        }

        String savedName = appPrefs.getString("last_name", "Anonymous");
        String uid = getDocumentId(context);

        if (uid == null) {
            Log.e(TAG, "Cannot save level completion: User not logged in");
            return; // Must be logged in to save to leaderboard
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String entryId = uid + "_level" + level;

        db.collection("leaderboard").document(entryId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long existingTime = documentSnapshot.getLong("timeTakenMillis");
                        if (existingTime != null && timeTakenMillis < existingTime) {
                            updateLeaderboardEntry(db, entryId, level, savedName, timeTakenMillis, uid);
                        }
                    } else {
                        updateLeaderboardEntry(db, entryId, level, savedName, timeTakenMillis, uid);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking level leaderboard for " + entryId, e));
    }

    /**
     * Write leaderboard entry
     */
    private static void updateLeaderboardEntry(FirebaseFirestore db, String entryId, int level, String name, long time, String uid) {
        Map<String, Object> completion = new HashMap<>();
        completion.put("level", level);
        completion.put("userName", name);
        completion.put("timeTakenMillis", time);
        completion.put("timestamp", FieldValue.serverTimestamp());
        completion.put("uid", uid);

        db.collection("leaderboard").document(entryId)
                .set(completion)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Leaderboard entry updated/created"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing leaderboard", e));
    }

    /**
     * Save total game completion time
     */
    public static void saveGameCompletion(Context context, long totalTimeMillis) {
        SharedPreferences appPrefs = getAppPrefs(context);

        String savedName = appPrefs.getString("last_name", "Anonymous");
        String uid = getDocumentId(context);

        if (uid == null) {
            Log.e(TAG, "Cannot save game completion: User not logged in");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("game_leaderboard").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long existingTime = documentSnapshot.getLong("totalTimeMillis");
                        if (existingTime != null && totalTimeMillis < existingTime) {
                            updateGameLeaderboardEntry(db, uid, savedName, totalTimeMillis, uid);
                        }
                    } else {
                        updateGameLeaderboardEntry(db, uid, savedName, totalTimeMillis, uid);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking game leaderboard for UID: " + uid, e));
    }

    /**
     * Update total leaderboard entry
     */
    private static void updateGameLeaderboardEntry(FirebaseFirestore db, String entryId, String name, long time, String uid) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("userName", name);
        entry.put("totalTimeMillis", time);
        entry.put("timestamp", FieldValue.serverTimestamp());
        entry.put("uid", uid);

        db.collection("game_leaderboard").document(entryId)
                .set(entry)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game leaderboard updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating game leaderboard", e));
    }

    /**
     * Leaderboard callback interface
     */
    public interface LeaderboardCallback {
        void onLeaderboardLoaded(List<Map<String, Object>> entries);
        void onError(Exception e);
    }

    /**
     * Get top 10 total game leaderboard
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

                    String uid = getDocumentId(context);

                    if (uid != null) {
                        for (Map<String, Object> entry : results) {
                            if (uid.equals(entry.get("uid"))) {
                                awardAchievement(context, ACHIEV_TOP_10);
                                break;
                            }
                        }
                    }

                    callback.onLeaderboardLoaded(results);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Get top 10 per-level leaderboard
     */
    public static void getLeaderboard(Context context, int level, LeaderboardCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("leaderboard")
                .whereEqualTo("level", level)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Map<String, Object>> results = new ArrayList<>();

                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        results.add(doc.getData());
                    }

                    Collections.sort(results, (o1, o2) -> {
                        Long t1 = (Long) o1.get("timeTakenMillis");
                        Long t2 = (Long) o2.get("timeTakenMillis");
                        if (t1 == null) return 1;
                        if (t2 == null) return -1;
                        return t1.compareTo(t2);
                    });

                    if (results.size() > 10) {
                        results = results.subList(0, 10);
                    }

                    String uid = getDocumentId(context);

                    if (uid != null) {
                        for (Map<String, Object> entry : results) {
                            if (uid.equals(entry.get("uid"))) {
                                awardAchievement(context, ACHIEV_TOP_10);
                                break;
                            }
                        }
                    }

                    callback.onLeaderboardLoaded(results);
                })
                .addOnFailureListener(callback::onError);
    }
}