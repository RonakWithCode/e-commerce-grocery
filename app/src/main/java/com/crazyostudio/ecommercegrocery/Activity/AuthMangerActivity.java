package com.crazyostudio.ecommercegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.databinding.ActivityAuthMangerBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AuthMangerActivity extends AppCompatActivity {
    ActivityAuthMangerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthMangerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}