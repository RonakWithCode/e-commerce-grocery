package com.crazyostudio.ecommercegrocery.javaClasses;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.crazyostudio.ecommercegrocery.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ErrorBox {

    private Activity activity;
    private Dialog dialog;

    public ErrorBox(Activity activity) {
        this.activity = activity;
    }

    public void showErrorDialog(String title, String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.error_box, null);

        TextView errorTitle = view.findViewById(R.id.errorTitle);
        TextView errorMessage = view.findViewById(R.id.errorMessage);
        MaterialButton errorButton = view.findViewById(R.id.errorButton);

        errorTitle.setText(title);
        errorMessage.setText(message);

        builder.setView(view);
        dialog = builder.create();

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
