package com.crazyostudio.ecommercegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.crazyostudio.ecommercegrocery.Fragment.AddressFragment;
import com.crazyostudio.ecommercegrocery.Fragment.CheckoutFragment;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityOderBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class OderActivity extends AppCompatActivity {
    ActivityOderBinding binding;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        transaction = getSupportFragmentManager().beginTransaction();
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored) {
            String errorMessage = ignored.toString();
            FirebaseDatabase.getInstance().getReference().child("Error").push().setValue(errorMessage);
        }

        String BuyMode = getIntent().getStringExtra("BuyType");
        ProductModel productModel = getIntent().getParcelableExtra("productModel");
//        if (BuyMode.equals("Cart")){
//            Fragment fragment = new AddressFragment();
            Fragment fragment = new CheckoutFragment();
            transaction.replace(R.id.fragment_container,fragment,"Address");
            transaction.addToBackStack("Address");
            transaction.commit();
//        }
//        else {
//            finish();
//        }



    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof  AddressFragment) {
            // Finish the activity when YourFragment1 is displayed
            finish();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Pop the fragment from the back stack when back button is pressed
            getSupportFragmentManager().popBackStack();
        } else {
            // Handle the back button behavior for your activity (e.g., finish the activity)
            super.onBackPressed();
        }
    }
}