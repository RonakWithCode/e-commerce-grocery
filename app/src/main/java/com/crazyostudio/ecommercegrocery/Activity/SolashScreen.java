package com.crazyostudio.ecommercegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivitySolashScreenBinding;

public class SolashScreen extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2000; // 2 seconds

    ActivitySolashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolashScreenBinding.inflate(getLayoutInflater());
        // Hide the action bar (app bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hide the toolbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(binding.getRoot());


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SolashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}