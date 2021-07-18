package com.github.almasud.NotePad;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class BaseApplication extends Application implements LifecycleObserver {
    private static final BaseApplication INSTANCE = new BaseApplication();
    private static final String TAG = "BaseApplication";
    public static final String BUNDLE = "Bundle";
    private static AppVisibilityListener sAppVisibilityListener;

    @Override
    public void onCreate() {
        super.onCreate();
        // Add observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Used to get the instance of {@link BaseApplication}.
     * @return The instance of {@link BaseApplication}.
     */
    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    /**
     * A listener for the visibility of the {@link Application}.
     */
    public interface AppVisibilityListener {
        /**
         * Used to determine the visibility of the {@link Application}.
         * @param isBackground true if the {@link Application} is in background otherwise false.
         */
        void onAppVisibility(boolean isBackground);
    }

    /**
     * Used to set whether the app is in background or not.
     */
    private void setAppInBackground(boolean isBackground) {
        if (sAppVisibilityListener != null)
            sAppVisibilityListener.onAppVisibility(isBackground);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d(TAG, "onEnterForeground: The app is in foreground.");
        setAppInBackground(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d(TAG, "onEnterBackground: The app is in background.");
        setAppInBackground(true);
    }

    /**
     * Used to set an {@link AppVisibilityListener}.
     * @param listener An {@link AppVisibilityListener}.
     */
    public void setOnAppVisibilityListener(AppVisibilityListener listener) {
        sAppVisibilityListener = listener;
    }

    /**
     * Start an activity with a new and clear task.
     * @param activity An instance of the {@link Activity} where start from.
     * @param destination A {@link Class} of an {@link Activity} to be started.
     * @param bundle The bundle to be send with the {@link Intent}.
     */
    public void startNewActivityWithClearTask(
            @NonNull Activity activity, @NonNull Class destination, @Nullable Bundle bundle) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (bundle != null)
            intent.putExtra(BUNDLE, bundle);
        activity.startActivity(intent);
    }

    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled <code>true</code> to enable, <code>false</code> to disable
     * the views.
     */
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

}
