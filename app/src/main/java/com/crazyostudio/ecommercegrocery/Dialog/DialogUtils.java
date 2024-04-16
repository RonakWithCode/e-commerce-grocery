package com.crazyostudio.ecommercegrocery.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

    public static void showConfirmationDialog(Context context, String message, DialogInterface.OnClickListener positiveClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton("Yes", positiveClickListener)
                .setNegativeButton("No", null)
                .show();
    }



}
