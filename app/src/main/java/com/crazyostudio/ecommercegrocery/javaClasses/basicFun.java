package com.crazyostudio.ecommercegrocery.javaClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class basicFun {
    public static boolean CheckField(@NonNull EditText editText){
        boolean isCheck = true;
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Fill this");
            isCheck = false;
        }
        return isCheck;
    }
    public static void AlertDialog(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
