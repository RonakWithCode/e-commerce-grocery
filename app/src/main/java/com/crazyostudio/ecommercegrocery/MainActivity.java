package com.crazyostudio.ecommercegrocery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.crazyostudio.ecommercegrocery.DAO.CartDAOHelper;
import com.crazyostudio.ecommercegrocery.DAO.ShoppingCartFirebaseModelDAO;
import com.crazyostudio.ecommercegrocery.Fragment.HomeFragment;
import com.crazyostudio.ecommercegrocery.Fragment.MoreFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ProductWithSlideCategoryFragment;
import com.crazyostudio.ecommercegrocery.Fragment.SearchFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ShoppingCartsFragment;
import com.crazyostudio.ecommercegrocery.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final ActivityResultLauncher<String> pushNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, you can show notifications
                } else {
                    // Permission denied, handle accordingly
                }
            });

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        applySavedLanguage(); // Ensure language is applied before anything else
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            CartManger(user.getUid());
        }

        if (getIntent() != null && getIntent().hasExtra("LoadID")) {
            if ("OrderView".equals(getIntent().getStringExtra("LoadID"))) {
                Intent i = new Intent(MainActivity.this, OrderDetailsActivity.class);
                i.putExtra("Type", "seeOrderNotification");
                i.putExtra("orderID", getIntent().getStringExtra("orderId"));
                startActivity(i);
            }
        }

        loader(new HomeFragment(), "null");
        requestNotificationPermission();

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeBtn:
                    loader(new HomeFragment(), "HomeFragment");
                    break;
                case R.id.GoCategory:
                    loader(new ProductWithSlideCategoryFragment(), "CategoryFragment");
                    break;
                case R.id.shoppingCartsBtn:
                    loader(new ShoppingCartsFragment(), "ShoppingCartsFragment");
                    break;
                case R.id.moreBtn:
                    loader(new MoreFragment(), "MoreFragment");
                    break;
            }
            return true;
        });
    }

//    private void applySavedLanguage() {
//        SharedPreferences preferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
//        String languageCode = preferences.getString("languageCode", "en");
//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration(getResources().getConfiguration());
//        config.setLocale(locale);
//        Resources resources = getResources();
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//    }

    private void CartManger(String id) {
        CartDAOHelper databaseHelper = CartDAOHelper.getDB(this);
        List<ShoppingCartFirebaseModelDAO> data = databaseHelper.ModelDAO().getAllModel();

        for (ShoppingCartFirebaseModelDAO item : data) {
            Log.i("MAIN", "CartManger: " + item.getProductId() + " QTY " + item.getProductSelectQuantity());
        }
    }

    public void ActionBarShow() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !actionBar.isShowing()) {
            actionBar.show();
        }

        // Change Status Bar color
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            openSearchFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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

    public void loader(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        ActionBarShow();
    }
}
