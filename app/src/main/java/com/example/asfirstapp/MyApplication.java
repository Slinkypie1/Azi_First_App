package com.example.asfirstapp;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
public class MyApplication  extends Application{
    private static boolean isAppInForeground = false;
    @Override
    public void onCreate(){
        super.onCreate();
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState){}
            @Override
            public void onActivityStarted(Activity activity){
                isAppInForeground=true;
            }
            @Override
            public void onActivityResumed(Activity activity){
                isAppInForeground = true;
            }
            @Override
            public void onActivityPaused(Activity activity){
                isAppInForeground= false;
            }
            @Override
            public void onActivityStopped(Activity activity){
                isAppInForeground= false;
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState){}
            @Override
            public void onActivityDestroyed(Activity activity){}
        });
    }
public static boolean isAppInForeground(){
    return isAppInForeground();
}
}

