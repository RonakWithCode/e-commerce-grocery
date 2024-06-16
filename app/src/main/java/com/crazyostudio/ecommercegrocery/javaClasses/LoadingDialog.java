package com.crazyostudio.ecommercegrocery.javaClasses;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import com.crazyostudio.ecommercegrocery.R;


public class LoadingDialog {

    private Activity activity;
    private Dialog dialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        dialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_box, null);
        dialog.setContentView(view);
        dialog.setCancelable(false); // Optional: disable cancel by clicking outside
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
