package com.crazyostudio.ecommercegrocery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crazyostudio.ecommercegrocery.Activity.OrderDetailsActivity;
import com.crazyostudio.ecommercegrocery.Fragment.CategoryFragment;
import com.crazyostudio.ecommercegrocery.Fragment.HomeFragment;
import com.crazyostudio.ecommercegrocery.Fragment.MapFragment;
import com.crazyostudio.ecommercegrocery.Fragment.MoreFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ProductWithSlideCategoryFragment;
import com.crazyostudio.ecommercegrocery.Fragment.SearchFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ShoppingCartsFragment;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.mappls.sdk.maps.Mappls;
import com.mappls.sdk.services.account.MapplsAccountManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private final ActivityResultLauncher<String> pushNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, you can show notifications
//                    showDummyNotification();
                } else {
                    // Permission denied, handle accordingly
                    // You can show a message to the user or request the permission again
                }
            });

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//
        if (getIntent() != null && getIntent().hasExtra("LoadID")) {
            if (getIntent().getStringExtra("LoadID").equals("OrderView")) {
                Intent i = new Intent(MainActivity.this, OrderDetailsActivity.class);
                i.putExtra("Type","seeOrderNotification");
                i.putExtra("orderID",getIntent().getStringExtra("orderId"));
                startActivity(i);
            }
        }

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green_primary)));


//        Log.i("MAIN.TAG", "onCreate: "+ FirebaseAuth.getInstance().token);


//        CheckNotificationToken();
//        loader(new PinCodeFragment(),"null");
        loader(new HomeFragment(),"null");
        requestNotificationPermission();





        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(itemId -> {
            switch (itemId.getItemId()) {
                case R.id.homeBtn:
                    loader(new MapFragment(),"HomeFragment");
                    break;
                case R.id.GoCategory:
                    loader(new ProductWithSlideCategoryFragment(),"CategoryFragment");
                    break;
                case R.id.shoppingCartsBtn:
                    loader(new ShoppingCartsFragment(),"ShoppingCartsFragment");
                    break;

                case R.id.moreBtn:
                    loader(new MoreFragment(),"MoreFragment");
                    break;
            }
            return true;
        });
    }
    public void ActionBarShow(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !actionBar.isShowing()) {
            actionBar.show();
        }

        //        // Change Status Bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green_primary_variant));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolba_rmenu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_search) {
            openSearchFragment();
            // Perform action for search menu item
            return true;
            // Add cases for other menu items if needed
        }
        return super.onOptionsItemSelected(item);
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

    private void openSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loader, new SearchFragment())
                .addToBackStack("HomeFragment")
                .commit();
    }

    public void loader(Fragment fragment,String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader,fragment,tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        ActionBarShow();
    }





}