package com.crazyostudio.ecommercegrocery.javaClasses;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crazyostudio.ecommercegrocery.R;

import java.lang.ref.WeakReference;

public class LoadingDialog {

    private final WeakReference<Activity> activityRef;
    private Dialog dialog;
    private TextView messageText;
    private boolean isShowing = false;

    public LoadingDialog(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    public void startLoadingDialog() {
        startLoadingDialog("Loading...");
    }

    public void startLoadingDialog(String message) {
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing() || isShowing) {
            return;
        }

        activity.runOnUiThread(() -> {
            dialog = new Dialog(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.loading_box, null);
            messageText = view.findViewById(R.id.loading_message);
            messageText.setText(message);
            
            dialog.setContentView(view);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            
            if (!activity.isFinishing()) {
                dialog.show();
                isShowing = true;
            }
        });
    }

    public void updateMessage(String message) {
        if (messageText != null) {
            Activity activity = activityRef.get();
            if (activity != null) {
                activity.runOnUiThread(() -> messageText.setText(message));
            }
        }
    }

    public void dismissDialog() {
        Activity activity = activityRef.get();
        if (activity == null || !isShowing) {
            return;
        }

        activity.runOnUiThread(() -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                isShowing = false;
            }
        });
    }

    public boolean isShowing() {
        return isShowing;
    }
}

