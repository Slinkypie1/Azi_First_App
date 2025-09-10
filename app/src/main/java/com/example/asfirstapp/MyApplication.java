package com.example.asfirstapp; // Package declaration

import android.app.Activity; // Import Activity class to monitor lifecycle events
import android.app.Application; // Import Application class to extend app-wide behavior
import android.os.Bundle; // Import Bundle class for saved instance state

public class MyApplication extends Application { // Custom Application class

    private static boolean isAppInForeground = false; // Flag to track if app is in foreground

    @Override
    public void onCreate() {
        super.onCreate(); // Call parent onCreate

        // Register activity lifecycle callbacks to monitor all activities
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // Called when an activity is created
                // Not used here
            }

            @Override
            public void onActivityStarted(Activity activity) {
                // Called when an activity becomes visible
                isAppInForeground = true; // App is now in foreground
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // Called when activity gains focus
                isAppInForeground = true; // Ensure app is marked as foreground
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // Called when activity loses focus
                // Not changing foreground state here to avoid false background detection
            }

            @Override
            public void onActivityStopped(Activity activity) {
                // Called when activity is no longer visible
                isAppInForeground = false; // App might be backgrounded
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                // Called before activity may be destroyed to save state
                // Not used here
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // Called when activity is destroyed
                // Not used here
            }
        });
    }

    // Static method to query if the app is currently in the foreground
    public static boolean isAppInForeground() {
        return isAppInForeground; // Return current state
    }
}
