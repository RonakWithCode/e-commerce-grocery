package com.crazyostudio.ecommercegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.crazyostudio.ecommercegrocery.Fragment.AddressFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ProductDetailsFragment;
import com.crazyostudio.ecommercegrocery.Fragment.UserAccountFragment;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityFragmentLoaderBinding;

import java.util.Objects;

public class FragmentLoader extends AppCompatActivity {
    ActivityFragmentLoaderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentLoaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String id = getIntent().getStringExtra("LoadID");
        if (id.isEmpty()) {
            finish();
        }else if (id.equals("MoreAddress")){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new AddressFragment();
            Bundle bundle = new Bundle();
            bundle.putString("LoadID",id);
            bundle.putString("BuyType","viewAddress");
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,fragment,id);
            transaction.addToBackStack(id);
            transaction.commit();
        }

        else if (id.equals("UserAccountFragment")){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new UserAccountFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("LoadID",id);
//            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,fragment,id);
            transaction.addToBackStack(id);
            transaction.commit();
        }else if (id.equals("Details")){
            Objects.requireNonNull(getSupportActionBar()).show();
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelable("productDetails", getIntent().getParcelableExtra("productDetails"));
            ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
            productDetailsFragment.setArguments(bundle);
//        productDetailsFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,productDetailsFragment,"OrderDetailsActivity");
            transaction.addToBackStack("OrderDetailsActivity");
            transaction.commit();
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            finish();

        } else {
            // If no fragments are in the back stack, finish the activity
            finish();
        }
    }
}