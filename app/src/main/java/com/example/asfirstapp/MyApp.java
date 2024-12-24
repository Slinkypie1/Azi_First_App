package com.example.asfirstapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MyApp extends Application {
    private static final String TAG = "MyApp";
    private static final String APP_STATE_PREF = "AppStatePref";
    private static final String IS_APP_RUNNING = "IsAppRunning";

    private int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                updateAppState(true); // App is in the foreground
                Log.d(TAG, "App moved to foreground.");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    updateAppState(false); // App is in the background
                    Log.d(TAG, "App moved to background.");
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activityCount == 0) {
                    Log.d(TAG, "App is fully closed.");
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        });
    }

    private void updateAppState(boolean isRunning) {
        SharedPreferences prefs = getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        prefs.edit().putBoolean(IS_APP_RUNNING, isRunning).apply();
    }

    public static boolean isAppRunning(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_STATE_PREF, MODE_PRIVATE);
        return prefs.getBoolean(IS_APP_RUNNING, false);
    }
}
