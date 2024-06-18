package com.crazyostudio.ecommercegrocery.javaClasses;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.R;

import java.lang.ref.WeakReference;

public class LoadingDialog {

    private final WeakReference<Activity> activityRef;
    private Dialog dialog;

    public LoadingDialog(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    public void startLoadingDialog() {
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        dialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_box, (ViewGroup) activity.findViewById(android.R.id.content), false);
        dialog.setContentView(view);
        dialog.setCancelable(false); // Optional: disable cancel by clicking outside
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            Activity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            dialog.dismiss();
        }
    }
}

