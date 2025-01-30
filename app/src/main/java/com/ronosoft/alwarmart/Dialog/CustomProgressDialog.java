package com.ronosoft.alwarmart.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ronosoft.alwarmart.R;

public class CustomProgressDialog extends Dialog {

    private ProgressBar progressBar;
    private TextView progressText;

    public CustomProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress_dialog);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        setCancelable(true); // Prevent dismiss on touch outside
    }

    public void setMessage(String message) {
        progressText.setText(message);
    }
    public void setCancelable_(boolean b){
        setCancelable(b);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

}
