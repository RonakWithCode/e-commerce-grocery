package com.ronosoft.alwarmart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ronosoft.alwarmart.Activity.OrderDetailsActivity;
import com.ronosoft.alwarmart.Fragment.HomeFragment;
import com.ronosoft.alwarmart.Fragment.MoreFragment;
import com.ronosoft.alwarmart.Fragment.ProductWithSlideCategoryFragment;
import com.ronosoft.alwarmart.Fragment.SearchFragment;
import com.ronosoft.alwarmart.Fragment.ShoppingCartsFragment;
import com.ronosoft.alwarmart.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.appcheck.FirebaseAppCheck;
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
//import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String ORDER_VIEW = "OrderView";
    private static final String ORDER_ID = "orderId";
    private static final String LOAD_ID = "LoadID";

    private final ActivityResultLauncher<String> pushNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::handleNotificationPermissionResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeApp();
        setupUI();
        handleIntent();
        setupBottomNavigation();
    }

    private void initializeApp() {
        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void setupUI() {
        loadInitialFragment();
        requestNotificationPermission();
        binding.bottomNavigationView.setBackground(null);
    }

    private void loadInitialFragment() {
        loadFragment(new HomeFragment(), "HomeFragment");
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(LOAD_ID)) {
            if (ORDER_VIEW.equals(intent.getStringExtra(LOAD_ID))) {
                openOrderDetails(intent.getStringExtra(ORDER_ID));
            }
        }
    }

    private void openOrderDetails(String orderId) {
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("Type", "seeOrderNotification");
        intent.putExtra(ORDER_ID, orderId);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigation(item.getItemId());
            return true;
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void handleBottomNavigation(int itemId) {
        if (itemId == R.id.homeBtn) {
            loadFragment(new HomeFragment(), "HomeFragment");
        } else if (itemId == R.id.GoCategory) {
            loadFragment(new ProductWithSlideCategoryFragment(), "CategoryFragment");
        } else if (itemId == R.id.shoppingCartsBtn) {
            loadFragment(new ShoppingCartsFragment(), "ShoppingCartsFragment");
        } else if (itemId == R.id.moreBtn) {
            loadFragment(new MoreFragment(), "MoreFragment");
        }
    }



    public void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !actionBar.isShowing()) {
            actionBar.show();
        }
        updateStatusBarColor();
    }

    private void updateStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.OrderYellowColor));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolba_rmenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            openSearchFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) 
            != PackageManager.PERMISSION_GRANTED) {
            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void handleNotificationPermissionResult(boolean isGranted) {
        // Handle permission result if needed
    }

    private void openSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loader, new SearchFragment())
                .addToBackStack("HomeFragment")
                .commit();
    }

    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        showActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
