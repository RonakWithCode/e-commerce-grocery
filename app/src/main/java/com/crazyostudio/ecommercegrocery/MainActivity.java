package com.crazyostudio.ecommercegrocery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crazyostudio.ecommercegrocery.Activity.OrderDetailsActivity;
import com.crazyostudio.ecommercegrocery.Adapter.SearchAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.SuggestionsAdapter;
import com.crazyostudio.ecommercegrocery.Fragment.HomeFragment;
import com.crazyostudio.ecommercegrocery.Fragment.MoreFragment;
import com.crazyostudio.ecommercegrocery.Fragment.SearchFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ShoppingCartsFragment;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.databinding.ActivityMainBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("Product");
    private ArrayList<ProductModel> suggestionsList = new ArrayList<>();

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





        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(itemId -> {
            switch (itemId.getItemId()) {
                case R.id.homeBtn:
                    loader(new HomeFragment(),"HomeFragment");
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

    private void searchProduct(String query) {
//         Perform a search in Firestore and update suggestionsList with the results
//         For example:
         productRef.whereEqualTo("productName", query)
                 .get()
                 .addOnSuccessListener(queryDocumentSnapshots -> {
                     suggestionsList.clear();
                     for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                         ProductModel product = documentSnapshot.toObject(ProductModel.class);
                         suggestionsList.add(product);
                     }
//                     binding.searchBar.updateLastSuggestions(suggestionsList);
                 })
                 .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error searching for products", Toast.LENGTH_SHORT).show());
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

    private void loader(Fragment fragment,String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader,fragment,tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        if (currentFragment instanceof SearchFragment) {
//            // Handle popping the backstack to show the main toolbar
//            getSupportFragmentManager().popBackStack();
//            getSupportActionBar().show();
//        } else {
//            super.onBackPressed();
//        }

    }
}