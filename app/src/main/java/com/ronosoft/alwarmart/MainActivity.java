package com.ronosoft.alwarmart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;
import com.ronosoft.alwarmart.Activity.OrderDetailsActivity;
import com.ronosoft.alwarmart.Fragment.HomeFragment;
import com.ronosoft.alwarmart.Fragment.MoreFragment;
import com.ronosoft.alwarmart.Fragment.ProductFilterByQueryFragment;
import com.ronosoft.alwarmart.Fragment.ProductWithSlideCategoryFragment;
import com.ronosoft.alwarmart.Fragment.SearchFragment;
import com.ronosoft.alwarmart.Fragment.ShoppingCartsFragment;
import com.ronosoft.alwarmart.databinding.ActivityMainBinding;
import com.ronosoft.alwarmart.javaClasses.TokenManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String ORDER_ID = "orderId";
    private static final String OFFERID = "offerId";
    private static final String NOTIFICATION_TYPE = "notification_type";

    // Permission Launchers
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;
    private final Map<String, Boolean> permissionStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Explicitly disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize token (commented out, kept as is)
        /*
        try {
            String token = TokenManager.getInstance(this).getToken();
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
        */

        // Initialize permission launcher
        initializePermissionLauncher();

        setupUI();
        handleNotificationIntent(getIntent());
        setupBottomNavigation();
        requestRequiredPermissions();
    }

    private void initializePermissionLauncher() {
        multiplePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    permissionStatus.clear();
                    permissionStatus.putAll(result);

                    // Handle permission results
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        String permission = entry.getKey();
                        boolean granted = entry.getValue();
                        if (!granted) {
                            handlePermissionDenied(permission);
                        }
                    }
                }
        );
    }

    private void requestRequiredPermissions() {
        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }

        // Filter out already granted permissions
        String[] permissionsToRequest = filterUngrantedPermissions(permissions);

        if (permissionsToRequest.length > 0) {
            multiplePermissionLauncher.launch(permissionsToRequest);
        }
    }

    private String[] filterUngrantedPermissions(String[] permissions) {
        return java.util.Arrays.stream(permissions)
                .filter(permission -> ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);
    }

    private void handlePermissionDenied(String permission) {
        switch (permission) {
            case Manifest.permission.POST_NOTIFICATIONS:
                // Notification permission denied - Push notifications won't work
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                // Location permission denied - Location features will be limited
                break;
        }

        // Optionally show rationale if permission was denied
        if (shouldShowRequestPermissionRationale(permission)) {
            // You can show a dialog explaining why the permission is needed
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void setupUI() {
        try {
            loadInitialFragment();
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
    }

    private void loadInitialFragment() {
        loadFragment(new HomeFragment(), "HomeFragment");
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(NOTIFICATION_TYPE)) return;

        String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);

        switch (notificationType) {
            case "order_status":
                String orderId = intent.getStringExtra(ORDER_ID);
                if (orderId != null && !orderId.isEmpty()) {
                    openOrderDetails(orderId);
                }
                break;
            case "offer":
                String offerId = intent.getStringExtra(OFFERID);
                String filterName = intent.getStringExtra("filterName");
                if (offerId != null && !offerId.isEmpty()) {
                    loadOfferFragment(offerId, filterName);
                }
                break;
        }
    }

    private void loadOfferFragment(String offerId, String filterName) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("filter", offerId);
            bundle.putString("filterName", filterName);
            ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
    }

    private void openOrderDetails(String orderId) {
        try {
            Intent intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("Type", "seeOrderNotification");
            intent.putExtra(ORDER_ID, orderId);
            startActivity(intent);
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigation(item.getItemId());
            return true;
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void handleBottomNavigation(int itemId) {
        Fragment fragment = null;
        try {
            switch (itemId) {
                case R.id.homeBtn:
                    fragment = new HomeFragment();
                    break;
                case R.id.GoCategory:
                    fragment = new ProductWithSlideCategoryFragment();
                    break;
                case R.id.shoppingCartsBtn:
                    fragment = new ShoppingCartsFragment();
                    break;
                case R.id.moreBtn:
                    fragment = new MoreFragment();
                    break;
            }
            if (fragment != null) {
                loadFragment(fragment, fragment.getClass().getSimpleName());
            }
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
    }

    public void loadFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.toolba_rmenu_main, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_search) {
                openSearchFragment();
                return true;
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            return false;
        }
    }

    private void openSearchFragment() {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loader, new SearchFragment())
                    .addToBackStack("HomeFragment")
                    .commit();
        } catch (Exception e) {
            // Silently handled, Crashlytics will catch if critical
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    // Public method to check if a specific permission is granted
    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
}