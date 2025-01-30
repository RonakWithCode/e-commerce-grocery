package com.ronosoft.alwarmart.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.ActivityOderBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class OderActivity extends AppCompatActivity {
    ActivityOderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored) {
            String errorMessage = ignored.toString();
            FirebaseDatabase.getInstance().getReference().child("Error").push().setValue(errorMessage);
        }

//        String BuyMode = getIntent().getStringExtra("BuyType");
//        ProductModel productModel = getIntent().getParcelableExtra("productModel");
////        if (BuyMode.equals("Cart")){
////            Fragment fragment = new AddressFragment();
//            Fragment fragment = new CheckoutFragment();
//            transaction.replace(R.id.fragment_container,fragment,"Address");
//            transaction.addToBackStack("Address");
//            transaction.commit();
//        }
//        else {
//            finish();
//        }



    }
//
//    @Override
//    public void onBackPressed() {
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//        if (currentFragment instanceof  AddressFragment) {
//            // Finish the activity when YourFragment1 is displayed
//            finish();
//        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            // Pop the fragment from the back stack when back button is pressed
//            getSupportFragmentManager().popBackStack();
//        } else {
//            // Handle the back button behavior for your activity (e.g., finish the activity)
//            super.onBackPressed();
//        }
//    }
}