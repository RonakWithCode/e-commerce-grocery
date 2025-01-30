package com.ronosoft.alwarmart.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ronosoft.alwarmart.R;

public class CustomErrorDialog extends Dialog {

    private TextView titleTextView;
    private TextView messageTextView;
    private Button closeButton;

    public CustomErrorDialog(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_error_dialog_layout);

        // Set the dialog window dimensions
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        // Initialize dialog views
        titleTextView = findViewById(R.id.title_text_view);
        messageTextView = findViewById(R.id.message_text_view);
        closeButton = findViewById(R.id.close_button);

        closeButton.setOnClickListener(view -> dismiss());
    }

    public void setTitle(String title) {
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    public void setMessage(String message) {
        if (messageTextView != null) {
            messageTextView.setText(message);
        }
    }
}