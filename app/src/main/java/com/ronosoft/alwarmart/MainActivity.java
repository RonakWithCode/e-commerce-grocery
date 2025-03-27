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
import com.ronosoft.alwarmart.Fragment.ProductFilterByQueryFragment;
import com.ronosoft.alwarmart.Fragment.ProductFilterFragment;
import com.ronosoft.alwarmart.Fragment.ProductWithSlideCategoryFragment;
import com.ronosoft.alwarmart.Fragment.SearchFragment;
import com.ronosoft.alwarmart.Fragment.ShoppingCartsFragment;
import com.ronosoft.alwarmart.Model.BannerModels;
import com.ronosoft.alwarmart.databinding.ActivityMainBinding;
import com.ronosoft.alwarmart.javaClasses.TokenManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String ORDER_ID = "orderId";
    private static final String OFFERID = "offerId";
    private static final String LOAD_ID = "LoadID";
    private static final String NOTIFICATION_TYPE = "notification_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Initialize Firebase safely
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            Log.e(TAG, "FirebaseApp initialization failed", e);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Log the current token for debugging
        try {
            String token = TokenManager.getInstance(this).getToken();
            Log.i(TAG, "onCreate: token=" + token);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving token", e);
        }

        setupUI();
        handleNotificationIntent(getIntent());
        setupBottomNavigation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    /**
     * Sets up the UI by loading the initial fragment and requesting permissions if needed.
     */
    private void setupUI() {
        try {
            loadInitialFragment();
        } catch (Exception e) {
            Log.e(TAG, "Error loading initial fragment", e);
        }
        requestNotificationPermission();
    }

    /**
     * Loads the HomeFragment as the initial fragment.
     */
    private void loadInitialFragment() {
        loadFragment(new HomeFragment(), "HomeFragment");
    }

    /**
     * Handles notifications passed via the Intent.
     */
    private void handleNotificationIntent(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(NOTIFICATION_TYPE)) {
            String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);
            Log.i(TAG, "handleNotificationIntent: notificationType=" + notificationType);
            if ("order_status".equals(notificationType)) {
                String orderId = intent.getStringExtra(ORDER_ID);
                if (orderId != null && !orderId.isEmpty()) {
                    openOrderDetails(orderId);
                } else {
                    Log.w(TAG, "Order ID missing in notification intent.");
                }
            }
            else if ("offer".equals(notificationType)) {
                String OfferID = intent.getStringExtra(OFFERID);
                String filterName = intent.getStringExtra("filterName");
                if (OfferID != null && !OfferID.isEmpty()) {

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("filter", OfferID);
                    bundle.putString("filterName", filterName);
                    ProductFilterByQueryFragment fragment = new ProductFilterByQueryFragment();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.loader, fragment, "ProductFilterByQueryFragment");
                    transaction.addToBackStack(null);
                    transaction.commit();

//                    openOrderDetails(orderId);
                } else {
//                    Log.w(TAG, "Order ID missing in notification intent.");
                }
            }
        }
    }

    /**
     * Opens the OrderDetailsActivity.
     */
    private void openOrderDetails(String orderId) {
        try {
            Intent intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("Type", "seeOrderNotification");
            intent.putExtra(ORDER_ID, orderId);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening OrderDetailsActivity", e);
        }
    }

    /**
     * Sets up the bottom navigation and handles selection events.
     */
    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigation(item.getItemId());
            return true;
        });
    }

    /**
     * Handles bottom navigation item selection.
     */
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
                default:
                    Log.w(TAG, "Unknown navigation item selected");
                    break;
            }
            if (fragment != null) {
                loadFragment(fragment, fragment.getClass().getSimpleName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling bottom navigation", e);
        }
    }

    /**
     * Loads a fragment into the container with error handling.
     */
    public void loadFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + tag, e);
        }
    }

    /**
     * Request notification permission for Android 13+.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Here you could use the new ActivityResultLauncher to request permissions.
                // For example: requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                Log.i(TAG, "POST_NOTIFICATIONS permission not granted.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.toolba_rmenu_main, menu);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating options menu", e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_search) {
                openSearchFragment();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in options item selected", e);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the search fragment.
     */
    private void openSearchFragment() {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loader, new SearchFragment())
                    .addToBackStack("HomeFragment")
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Error opening search fragment", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
