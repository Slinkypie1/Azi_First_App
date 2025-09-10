package com.example.asfirstapp;
// Defines the package of this class. It must match your app's package structure.

import android.app.Activity;
// Imports Activity class to track individual activity events.

import android.app.Application;
// Imports Application class to create a global app-level class.

import android.content.Context;
// Imports Context for accessing system resources and SharedPreferences.

import android.content.SharedPreferences;
// Imports SharedPreferences for saving simple persistent data.

import android.os.Bundle;
// Imports Bundle used in activity lifecycle callbacks.

import android.util.Log;
// Imports Log for debugging purposes.

public class MyApp extends Application {
    // Extends Application to run code at the app level, not just per activity.

    private static final String TAG = "MyApp";
    // Tag used for logging debug messages.

    private static final String APP_STATE_PREF = "AppStatePref";
    // Name of SharedPreferences file to store app state.

    private static final String IS_APP_RUNNING = "IsAppRunning";
    // Key used in SharedPreferences to store whether the app is in the foreground.

    private int activityCount = 0;
    // Counter to track how many activities are currently started (foreground).

    @Override
    public void onCreate() {
        super.onCreate();
        // Called when the application is created.

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            // Registers callbacks to monitor activity lifecycle events globally.

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            // Called when an activity is created. Empty here.

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                // Increment count whenever an activity starts (app moves to foreground).

                updateAppState(true);
                // Update SharedPreferences to mark the app as running.

                Log.d(TAG, "App moved to foreground.");
                // Debug log for foreground transition.
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                // Decrement count when an activity stops.

                if (activityCount == 0) {
                    // If no activities are visible, app is in background.
                    updateAppState(false);
                    Log.d(TAG, "App moved to background.");
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activityCount == 0) {
                    // Optional: app is fully closed when last activity is destroyed.
                    Log.d(TAG, "App is fully closed.");
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {}
            // Called when an activity resumes. Empty here.

            @Override
            public void onActivityPaused(Activity activity) {}
            // Called when an activity pauses. Empty here.

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            // Called when activity state is saved. Empty here.
        });
    }

    private void updateAppState(boolean isRunning) {
        SharedPreferences prefs = getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        // Opens SharedPreferences for writing app state.

        prefs.edit().putBoolean(IS_APP_RUNNING, isRunning).apply();
        // Stores boolean indicating if app is in foreground.
    }

    public static boolean isAppRunning(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        // Access SharedPreferences to read app state.

        return prefs.getBoolean(IS_APP_RUNNING, false);
        // Returns true if app is running, false otherwise.
    }
}
