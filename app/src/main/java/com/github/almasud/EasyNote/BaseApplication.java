package com.github.almasud.EasyNote;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    /**
     * Set an {@link AlertDialog} with only a positive action button.
     * @param context A {@link Context} of the application.
     * @param customView A custom {@link View} of {@link AlertDialog}.
     * @param title A {@link String} for {@link AlertDialog} title.
     * @param iconRes A {@link DrawableRes} for {@link AlertDialog} icon.
     * @param message A {@link String} for {@link AlertDialog} message.
     * @param positiveButtonAction An {@link OnSingleAction} for positive action button.
     * @param positiveButtonText A {@link String} for positive action button.
     */
    public static void setAlertDialog(
            Context context, View customView, String title, int iconRes, String message,
            OnSingleAction positiveButtonAction, String positiveButtonText) {

        // Set only required parameter to the main method
        setAlertDialog(
                context, customView, title, iconRes, message,
                positiveButtonAction, positiveButtonText,
                null, null,
                null, null
        );
    }

    /**
     * Set an {@link AlertDialog} with positive and negative action button.
     * @param context A {@link Context} of the application.
     * @param customView A custom {@link View} of {@link AlertDialog}.
     * @param title A {@link String} for {@link AlertDialog} title.
     * @param iconRes A {@link DrawableRes} for {@link AlertDialog} icon.
     * @param message A {@link String} for {@link AlertDialog} message.
     * @param positiveButtonAction An {@link OnSingleAction} for positive action button.
     * @param positiveButtonText A {@link String} for positive action button.
     * @param negativeButtonAction An {@link OnSingleAction} for negative action button.
     * @param negativeButtonText A {@link String} for negative action button.
     */
    public static void setAlertDialog(
            Context context, View customView, String title, int iconRes, String message,
            OnSingleAction positiveButtonAction, String positiveButtonText,
            OnSingleAction negativeButtonAction, String negativeButtonText) {

        // Set only required parameter to the main method
        setAlertDialog(
                context, customView, title, iconRes, message,
                positiveButtonAction, positiveButtonText,
                negativeButtonAction, negativeButtonText,
                null, null
        );
    }

    /**
     * Set an {@link AlertDialog} with a custom {@link View} and positive, negative and neutral action button.
     * @param context A {@link Context} of the application.
     * @param customView A custom {@link View} of {@link AlertDialog}.
     * @param title A {@link String} for {@link AlertDialog} title.
     * @param iconRes A {@link DrawableRes} for {@link AlertDialog} icon.
     * @param message A {@link String} for {@link AlertDialog} message.
     * @param positiveButtonAction An {@link OnSingleAction} for positive action button.
     * @param positiveButtonText A {@link String} for positive action button.
     * @param negativeButtonAction An {@link OnSingleAction} for negative action button.
     * @param negativeButtonText A {@link String} for negative action button.
     * @param neutralButtonAction An {@link OnSingleAction} for neutral action button.
     * @param neutralButtonText A {@link String} for neutral action button.
     */
    public static void setAlertDialog(
            Context context, View customView, String title, int iconRes, String message,
            OnSingleAction positiveButtonAction, String positiveButtonText,
            OnSingleAction negativeButtonAction, String negativeButtonText,
            OnSingleAction neutralButtonAction, String neutralButtonText) {

        // Create an alert dialog to show a dialog message
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCancelable(false);
        // Set a custom view
        if (customView != null)
            dialogBuilder.setView(customView);
        // Set a title
        if (title != null)
            dialogBuilder.setTitle(title);
        // Set an image resource
        if (iconRes != -1)
            dialogBuilder.setIcon(iconRes);
        // Set a message
        if (message != null)
            dialogBuilder.setMessage(message);

        // Set an action for positive button
        if (positiveButtonAction != null) {
            dialogBuilder.setPositiveButton(
                    (positiveButtonText != null)? positiveButtonText
                            : context.getResources().getString(R.string.action_yes),
                    (dialog, which) -> positiveButtonAction.onAction()
            );
        }

        // Set an action for negative button
        if (negativeButtonAction != null) {
            dialogBuilder.setNegativeButton(
                    (negativeButtonText != null)? negativeButtonText
                            : context.getResources().getString(R.string.action_no),
                    (dialog, which) -> negativeButtonAction.onAction()
            );
        }

        // Set an action for neutral button
        if (neutralButtonAction != null) {
            dialogBuilder.setNeutralButton(
                    (neutralButtonText != null)? neutralButtonText
                            : context.getResources().getString(R.string.action_not_sure),
                    (dialog, which) -> neutralButtonAction.onAction()
            );
        }

        // To avoid the block of UI (main) thread execute the task within a new thread.
        new Handler().post(() -> {
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            // This line always placed after the dialog.show() otherwise get a Null Pinter Exception.
            // Set all caps false to al alert dialog buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setAllCaps(false);
            // Change the buttons text color
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    context.getResources().getColor(R.color.colorSecondary)
            );
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    context.getResources().getColor(R.color.colorSecondary)
            );
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(
                    context.getResources().getColor(R.color.colorSecondary)
            );
        });
    }

    public static interface OnSingleAction {
        void onAction();
    }

}
