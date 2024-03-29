package com.crazyostudio.ecommercegrocery;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crazyostudio.ecommercegrocery.Activity.OrderDetailsActivity;
import com.crazyostudio.ecommercegrocery.Fragment.HomeFragment;
import com.crazyostudio.ecommercegrocery.Fragment.MoreFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ShoppingCartsFragment;
import com.crazyostudio.ecommercegrocery.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    // Initialize the permission launcher
    private ActivityResultLauncher<String> pushNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, you can show notifications
//                    showDummyNotification();
                } else {
                    // Permission denied, handle accordingly
                    // You can show a message to the user or request the permission again
                }
            });


//    private void showDummyNotification() {
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getIntent() != null && getIntent().hasExtra("LoadID")) {
            if (getIntent().getStringExtra("LoadID").equals("OrderView")) {
                Intent i = new Intent(MainActivity.this, OrderDetailsActivity.class);
                i.putExtra("Type","seeOrderNotification");
                i.putExtra("orderId",getIntent().getStringExtra("orderId"));
                startActivity(i);
            }
        }
        loader(new HomeFragment(),"null");
        requestNotificationPermission();
        binding.bottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            switch (i) {
                case 0:
                    loader(new HomeFragment(),"HomeFragment");
                    return (true);

                case 1:
                    loader(new ShoppingCartsFragment(),"ShoppingCartsFragment");
                    return (true);
                case 2:
                    loader(new MoreFragment(),"MoreFragment");
                    return (true);
            }
            return true;
        });
        binding.bottomBar.setOnItemReselectedListener(itemId -> {
            switch (itemId) {
                case 0:
                    loader(new HomeFragment(),"HomeFragment");
                    break;
                case 1:
                    loader(new ShoppingCartsFragment(),"ShoppingCartsFragment");
                    break;

                case 2:
                    loader(new MoreFragment(),"MoreFragment");
                    break;
            }
        });
    }







    private void requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, you can show notifications
//            showDummyNotification();
        } else {
            // Permission is not granted, request it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }





    private void loader(Fragment fragment,String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader,fragment,tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}