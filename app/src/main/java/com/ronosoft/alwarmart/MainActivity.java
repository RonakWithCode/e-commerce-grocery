package com.ronosoft.alwarmart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.ronosoft.alwarmart.Activity.OrderDetailsActivity;
import com.ronosoft.alwarmart.DAO.ShoppingCartFirebaseModelDAO;
import com.ronosoft.alwarmart.Fragment.HomeFragment;
import com.ronosoft.alwarmart.Fragment.MoreFragment;
import com.ronosoft.alwarmart.Fragment.ProductFilterByQueryFragment;
import com.ronosoft.alwarmart.Fragment.ProductWithSlideCategoryFragment;
import com.ronosoft.alwarmart.Fragment.SearchFragment;
import com.ronosoft.alwarmart.Fragment.ShoppingCartsFragment;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String ORDER_ID = "orderId";
    private static final String OFFERID = "offerId";
    private static final String NOTIFICATION_TYPE = "notification_type";
    private ProductManager productManager;

    // Permission launcher for runtime permissions
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize ProductManager
        productManager = new ProductManager(this);

        initializePermissionLauncher();
        setupUI();
        handleNotificationIntent(getIntent());
        setupBottomNavigation();
        requestRequiredPermissions();

        binding.fabCart.setVisibility(View.GONE);
        binding.cartBadge.setVisibility(View.GONE);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            setupFabCart();
        }
    }

    private void initializePermissionLauncher() {
        multiplePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> result.forEach((permission, granted) -> {
                    if (!granted && shouldShowRequestPermissionRationale(permission)) {
                        // Optionally, show a dialog to explain why this permission is needed.
                    }
                })
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
        String[] permissionsToRequest = Arrays.stream(permissions)
                .filter(permission -> ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);

        if (permissionsToRequest.length > 0) {
            multiplePermissionLauncher.launch(permissionsToRequest);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void setupUI() {
        loadInitialFragment();
    }

    private void loadInitialFragment() {
        loadFragment(new HomeFragment(), "HomeFragment", false);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(NOTIFICATION_TYPE)) return;

        String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);
        if ("order_status".equals(notificationType)) {
            String orderId = intent.getStringExtra(ORDER_ID);
            if (orderId != null && !orderId.isEmpty()) {
                openOrderDetails(orderId);
            }
        } else if ("offer".equals(notificationType)) {
            String offerId = intent.getStringExtra(OFFERID);
            String filterName = intent.getStringExtra("filterName");
            if (offerId != null && !offerId.isEmpty()) {
                loadOfferFragment(offerId, filterName);
            }
        }
    }

    private void loadOfferFragment(String offerId, String filterName) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", offerId);
        bundle.putString("filterName", filterName);
        ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, "ProductFilterByQueryFragment", true);
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
        String tag = "";
        switch (itemId) {
            case R.id.homeBtn:
                fragment = new HomeFragment();
                tag = "HomeFragment";
                break;
            case R.id.GoCategory:
                fragment = new ProductWithSlideCategoryFragment();
                tag = "ProductWithSlideCategoryFragment";
                break;
            case R.id.shoppingCartsBtn:
                fragment = new ShoppingCartsFragment();
                tag = "ShoppingCartsFragment";
                break;
            case R.id.moreBtn:
                fragment = new MoreFragment();
                tag = "MoreFragment";
                break;
        }
        if (fragment != null) {
            loadFragment(fragment, tag, false);
        }
    }

    private void setupFabCart() {
        // Initially hide the FAB and badge

        binding.fabCart.setVisibility(View.GONE);
        binding.cartBadge.setVisibility(View.GONE);

        // Sync cart from Firebase to ensure local database is up-to-date1626
        productManager.syncCartFromFirebase(new ProductManager.SyncCartCallback() {
            @Override
            public void onSyncSuccess() {
                checkCartAndUpdateFab();
            }

            @Override
            public void onSyncFailure(String error) {
                // Hide FAB and badge on sync failure
                binding.fabCart.setVisibility(View.GONE);
                binding.cartBadge.setVisibility(View.GONE);
            }
        });

        // Set FAB click listener to open ShoppingCartsFragment
        binding.fabCart.setOnClickListener(view -> {
            loadFragment(new ShoppingCartsFragment(), "ShoppingCartsFragment", true);
        });
    }

    private void checkCartAndUpdateFab() {
        // Observe all cart items using LiveData
        LiveData<List<ShoppingCartFirebaseModelDAO>> cartItemsLiveData = productManager.getAllCartItems();
        cartItemsLiveData.observe(this, cartItems -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                // Calculate total quantity
                int totalQuantity = cartItems.size();
                binding.fabCart.setVisibility(View.VISIBLE);
                binding.cartBadge.setVisibility(View.VISIBLE);
                binding.cartBadge.setText(String.valueOf(totalQuantity));
            } else {
                binding.fabCart.setVisibility(View.GONE);
                binding.cartBadge.setVisibility(View.GONE);
            }
        });
    }

    public void loadFragment(Fragment fragment, String tag, boolean addToBackStack) {
        if (fragment == null || getSupportFragmentManager().isStateSaved()) return;
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
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
        loadFragment(new SearchFragment(), "SearchFragment", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
}