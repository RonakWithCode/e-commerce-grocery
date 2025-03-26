package com.ronosoft.alwarmart;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;
import com.ronosoft.alwarmart.Activity.OrderDetailsActivity;
import com.ronosoft.alwarmart.Fragment.HomeFragment;
import com.ronosoft.alwarmart.Fragment.MoreFragment;
import com.ronosoft.alwarmart.Fragment.ProductWithSlideCategoryFragment;
import com.ronosoft.alwarmart.Fragment.SearchFragment;
import com.ronosoft.alwarmart.Fragment.ShoppingCartsFragment;
import com.ronosoft.alwarmart.databinding.ActivityMainBinding;
import com.ronosoft.alwarmart.javaClasses.TokenManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String ORDER_ID = "orderId";
    private static final String LOAD_ID = "LoadID";
    private static final String NOTIFICATION_TYPE = "notification_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Log current token for debugging
        String token = TokenManager.getInstance(this).getToken();
        Log.i(TAG, "onCreate: token=" + token);

        setupUI();
        // Handle notification intent if the activity was launched from a notification.
        handleNotificationIntent(getIntent());
        setupBottomNavigation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void setupUI() {
        loadInitialFragment();
        requestNotificationPermission();
    }

    private void loadInitialFragment() {
        loadFragment(new HomeFragment(), "HomeFragment");
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(NOTIFICATION_TYPE)) {
            String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);
            Log.i(TAG, "handleNotificationIntent: notificationType=" + notificationType);
            if ("order_status".equals(notificationType)) {
                String orderId = intent.getStringExtra(ORDER_ID);
                if (orderId != null) {
                    openOrderDetails(orderId);
                } else {
                    Log.w(TAG, "Order ID missing in notification intent.");
                }
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

    private void handleBottomNavigation(int itemId) {
        Fragment fragment = null;
        if (itemId == R.id.homeBtn) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.GoCategory) {
            fragment = new ProductWithSlideCategoryFragment();
        } else if (itemId == R.id.shoppingCartsBtn) {
            fragment = new ShoppingCartsFragment();
        } else if (itemId == R.id.moreBtn) {
            fragment = new MoreFragment();
        }
        if (fragment != null) {
            loadFragment(fragment, fragment.getClass().getSimpleName());
        }
    }

    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request permission as needed.
        }
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

    private void openSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loader, new SearchFragment())
                .addToBackStack("HomeFragment")
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
