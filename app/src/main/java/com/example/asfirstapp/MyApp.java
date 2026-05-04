package com.example.asfirstapp;

// Imports for tracking app state and saving persistent data
import android.app.Activity;            // For tracking individual activity events
import android.app.Application;         // Base class for global app-level logic
import android.content.Context;         // Provides access to system resources
import android.content.Intent;          // Added to start services
import android.content.SharedPreferences; // For simple persistent storage
import android.os.Bundle;               // Used in activity lifecycle callbacks
import android.util.Log;                // For logging debug information

/**
 * MyApp extends Application to track app-wide state.
 * It monitors whether the app is in the foreground or background.
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp"; // Log tag
    private static final String APP_STATE_PREF = "AppStatePref"; // SharedPreferences file name
    private static final String IS_APP_RUNNING = "IsAppRunning"; // Key for foreground state

    private int activityCount = 0; // Tracks how many activities are currently visible

    @Override
    public void onCreate() {
        super.onCreate();

        // Register lifecycle callbacks to monitor all activities globally
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // Called when an activity is created. Not used here.
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activityCount == 0) {
                    // App moved to foreground from background
                    Intent serviceIntent = new Intent(activity, MusicService.class);
                    serviceIntent.setAction("ACTION_RESUME");
                    activity.startService(serviceIntent);
                    // Resume music when app returns to foreground

                    Log.d(TAG, "App moved to foreground.");
                }

                activityCount++; // Increment when activity becomes visible
                updateAppState(true); // App is in foreground
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--; // Decrement when activity is no longer visible

                if (activityCount == 0) {
                    // No visible activities → app is in background
                    Intent serviceIntent = new Intent(activity, MusicService.class);
                    serviceIntent.setAction("ACTION_PAUSE");
                    activity.startService(serviceIntent);
                    // Pause music when app goes to background

                    updateAppState(false);
                    Log.d(TAG, "App moved to background.");
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activityCount == 0) {
                    // Optional: app fully closed if last activity destroyed
                    Log.d(TAG, "App is fully closed.");
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // Called when activity resumes; not used here
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // Called when activity pauses; not used here
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                // Called when activity state is saved; not used here
            }
        });
    }

    /**
     * Updates the stored app state in SharedPreferences.
     *
     * @param isRunning true if app is in foreground, false if background
     */
    private void updateAppState(boolean isRunning) {
        SharedPreferences prefs = getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        prefs.edit().putBoolean(IS_APP_RUNNING, isRunning).apply();
        // Persist whether the app is currently active
    }

    /**
     * Checks whether the app is currently running in the foreground.
     *
     * @param context any context (activity/service/application)
     * @return true if the app is in the foreground, false otherwise
     */
    public static boolean isAppRunning(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        return prefs.getBoolean(IS_APP_RUNNING, false);
        // Reads stored foreground/background state
    }
}