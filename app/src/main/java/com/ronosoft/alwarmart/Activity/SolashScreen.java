package com.ronosoft.alwarmart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ronosoft.alwarmart.MainActivity;
import com.ronosoft.alwarmart.databinding.ActivitySolashScreenBinding;

public class SolashScreen extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2000; // 2 seconds

    ActivitySolashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Hide the action bar (app bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SolashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();


        }, SPLASH_DELAY);

        // Hide the toolbar

    }
}