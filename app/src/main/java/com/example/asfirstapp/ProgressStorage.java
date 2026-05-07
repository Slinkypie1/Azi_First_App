package com.example.asfirstapp; // החבילה אליה שייכת המחלקה הזו

import android.content.Context; // דרוש עבור SharedPreferences, שירותי מערכת וכו'
import android.content.SharedPreferences;  // עבור אחסון מקומי
import android.os.Handler; // לניהול פעולות מושהות
import android.os.Looper; // לניהול לולאת ההודעות של השרשור הראשי
import android.provider.Settings;          // לקבלת מזהה מכשיר
import android.util.Log;                   // לרישום ביומן (Log)
import android.widget.Toast; // להצגת הודעות קופצות

import com.google.firebase.firestore.FieldValue; // עבור חותמות זמן של השרת
import com.google.firebase.firestore.FirebaseFirestore; // מסד הנתונים Firestore
import java.util.*; // אוספים, מפות, רשימות וכו'

/**
 * ProgressStorage מטפל באחסון וסנכרון השלב הגבוה ביותר שנפתח עבור משתמש.
 * התקדמות מקומית נשמרת ב-SharedPreferences, וניתן לסנכרן אותה אופציונלית ל-Firebase.
 */
public class ProgressStorage {

    // ----- קבועים -----
    private static final String PREF_NAME = "game_progress"; // שם קובץ ה-SharedPreferences
    private static final String KEY_HIGHEST_LEVEL = "highest_unlocked_level"; // שומר את השלב הגבוה ביותר שהושג
    private static final String KEY_GAME_START_TIME = "game_start_time"; // שומר מתי המשחק התחיל
    private static final String KEY_PAUSED_TIME = "paused_time"; // עוקב אחר משך זמן ההשהיה הכולל
    private static final String KEY_ACHIEVEMENTS = "earned_achievements"; // שומר הישגים שנפתחו
    private static final String TAG = "ProgressStorage"; // תגית לרישום ביומן (Log)

    private static boolean didHitWallThisRun = false; // עוקב אם השחקן פגע בקיר (עבור לוגיקת הישגים)

    // קבועי הישגים
    public static final String ACHIEV_SPEED_DEMON = "speed_demon";
    public static final String ACHIEV_PERFECTIONIST = "perfectionist";
    public static final String ACHIEV_WORLD_TRAVELER = "world_traveler";
    public static final String ACHIEV_RANKED = "ranked";
    public static final String ACHIEV_TOP_10 = "top_10";

    /**
     * מאפס את המעקב אחר פגיעה בקיר עבור הרצה חדשה
     */
    public static void resetPerfectionistFlag() {
        didHitWallThisRun = false;
    }

    /**
     * מציין שבוצעה פגיעה בקיר בהרצה הנוכחית
     */
    public static void recordWallHit() {
        didHitWallThisRun = true;
    }

    /**
     * מחזיר האם השחקן פגע בקיר בהרצה זו
     */
    public static boolean wasWallHit() {
        return didHitWallThisRun;
    }

    // מקבל מופע של SharedPreferences עבור התקדמות המשחק (ספציפי למשתמש)
    private static SharedPreferences getPrefs(Context context) {
        String uid = getDocumentId(context);
        String prefName = (uid != null) ? (PREF_NAME + "_" + uid) : PREF_NAME;
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    // מקבל מופע של SharedPreferences עבור הגדרות האפליקציה (ספציפי למשתמש)
    public static SharedPreferences getAppPrefs(Context context) {
        String uid = getDocumentId(context);
        String prefName = (uid != null) ? ("app_prefs_" + uid) : "app_prefs";
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    /**
     * בונה מזהה מסמך עבור Firestore באמצעות UID של משתמש Firebase
     */
    public static String getDocumentId(Context context) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            return com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    /**
     * מעניק הישג חדש אם הוא עדיין לא הושג
     */
    public static void awardAchievement(Context context, String achievementId) {
        Set<String> earned = new HashSet<>(getPrefs(context).getStringSet(KEY_ACHIEVEMENTS, new HashSet<>()));

        if (!earned.contains(achievementId)) {
            earned.add(achievementId);
            getPrefs(context).edit().putStringSet(KEY_ACHIEVEMENTS, earned).apply();

            // סנכרון הישגים מעודכנים ל-Firebase
            syncAchievementsToFirebase(context, new ArrayList<>(earned));

            Log.d(TAG, "Achievement Unlocked: " + achievementId);

            // הצגת הודעת קופצת בשרשור הראשי
            new Handler(Looper.getMainLooper()).post(() -> {
                String name = achievementId.replace("_", " ").toUpperCase();
                Toast.makeText(context, "🏆 Achievement Unlocked: " + name, Toast.LENGTH_LONG).show();
            });
        }
    }

    /**
     * מחזיר הישגים המאוחסנים מקומית
     */
    public static Set<String> getEarnedAchievements(Context context) {
        return getPrefs(context).getStringSet(KEY_ACHIEVEMENTS, new HashSet<>());
    }

    /**
     * מסנכרן את רשימת ההישגים ל-Firebase
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
     * מקבל את השלב הגבוה ביותר שנפתח (מקומי)
     */
    public static int getHighestUnlockedLevel(Context context) {
        return getPrefs(context).getInt(KEY_HIGHEST_LEVEL, 1);
    }

    /**
     * שמירת שלב מקומית בלבד (ללא סנכרון ל-Firebase)
     */
    public static void setHighestUnlockedLevelOffline(Context context, int level) {
        getPrefs(context)
                .edit()
                .putInt(KEY_HIGHEST_LEVEL, level)
                .apply();
    }

    /**
     * שמירת שלב מקומית וסנכרון ל-Firebase
     */
    public static void setHighestUnlockedLevel(Context context, int level) {
        setHighestUnlockedLevelOffline(context, level);
        syncToFirebase(context, level);
    }

    /**
     * שמירת הישגים מקומית
     */
    public static void setAchievementsOffline(Context context, List<String> achievements) {
        if (achievements == null) return;
        getPrefs(context).edit().putStringSet(KEY_ACHIEVEMENTS, new HashSet<>(achievements)).apply();
    }

    /**
     * אחסון זמן תחילת המשחק
     */
    public static void setGameStartTime(Context context, long startTime) {
        getPrefs(context).edit().putLong(KEY_GAME_START_TIME, startTime).apply();
    }

    /**
     * קבלת זמן תחילת המשחק
     */
    public static long getGameStartTime(Context context) {
        return getPrefs(context).getLong(KEY_GAME_START_TIME, 0);
    }

    /**
     * הוספת זמן השהיה למשך ההשהיה הכולל
     */
    public static void addPausedTime(Context context, long durationMillis) {
        long currentPaused = getPrefs(context).getLong(KEY_PAUSED_TIME, 0);
        getPrefs(context).edit().putLong(KEY_PAUSED_TIME, currentPaused + durationMillis).apply();
    }

    /**
     * קבלת זמן ההשהיה הכולל
     */
    public static long getTotalPausedTime(Context context) {
        return getPrefs(context).getLong(KEY_PAUSED_TIME, 0);
    }

    /**
     * איפוס טיימר למשחק חדש
     */
    public static void resetGameTimer(Context context) {
        getPrefs(context).edit()
                .putLong(KEY_GAME_START_TIME, System.currentTimeMillis())
                .putLong(KEY_PAUSED_TIME, 0)
                .apply();
    }

    /**
     * סנכרון התקדמות שלבים ל-Firebase
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
     * סנכרון הגדרות מראה ל-Firebase
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
     * סנכרון מצב משחק ל-Firebase
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
     * סנכרון מצב השתקת מוזיקה ל-Firebase
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
     * שמירת זמן סיום שלב (לוגיקת טבלת מובילים)
     */
    public static void saveLevelCompletion(Context context, int level, long timeTakenMillis) {

        // בדיקת הישג "Speed Demon"
        if (timeTakenMillis < 5000) {
            awardAchievement(context, ACHIEV_SPEED_DEMON);
        }

        SharedPreferences appPrefs = getAppPrefs(context);

        String mode = appPrefs.getString("game_mode", "casual");
        if (mode.equals("casual")) {
            return; // דילוג על טבלת מובילים במצב רגיל
        }

        String savedName = appPrefs.getString("last_name", "Anonymous");
        String uid = getDocumentId(context);

        if (uid == null) {
            Log.e(TAG, "Cannot save level completion: User not logged in");
            return; // חייב להיות מחובר כדי לשמור לטבלת מובילים
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
     * כתיבת רשומה בטבלת המובילים
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
     * שמירת זמן סיום משחק כולל
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
     * עדכון רשומה בטבלת המובילים הכללית
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
     * ממשק Callback עבור טבלת המובילים
     */
    public interface LeaderboardCallback {
        void onLeaderboardLoaded(List<Map<String, Object>> entries);
        void onError(Exception e);
    }

    /**
     * מקבל את 10 המובילים בטבלת המשחק הכללית
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
     * מקבל את 10 המובילים בטבלת המובילים לפי שלב
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