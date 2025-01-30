package com.ronosoft.alwarmart.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.ronosoft.alwarmart.Fragment.ProductDetailsFragment;
import com.ronosoft.alwarmart.Fragment.SearchFragment;
import com.ronosoft.alwarmart.Fragment.UserAccountFragment;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.ActivityFragmentLoaderBinding;

import java.util.Objects;

public class FragmentLoader extends AppCompatActivity {
    ActivityFragmentLoaderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentLoaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String id = getIntent().getStringExtra("LoadID");
        if (id == null || id.isEmpty()) {
            finish();
            return;
        }


        switch (id) {
            case "search": {
//                if (getSupportActionBar() != null) {
//                    getSupportActionBar().hide();
//                }

//                Objects.requireNonNull(getSupportActionBar()).hide();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = new SearchFragment();

//                ArrayList<ProductModel> homeProductModel = getIntent().getParcelableArrayListExtra("model"); // Corrected line
//
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("model", homeProductModel);
//                fragment.setArguments(bundle);

                transaction.replace(R.id.fragment_container, fragment, id);
                transaction.addToBackStack(id);
                transaction.commit();

                break;
            }
            case "UserAccountFragment": {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = new UserAccountFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("LoadID",id);
//            fragment.setArguments(bundle);
                transaction.replace(R.id.fragment_container, fragment, id);
                transaction.addToBackStack(id);
                transaction.commit();
                break;
            }
            case "Details": {
                Objects.requireNonNull(getSupportActionBar()).show();
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("productId", getIntent().getStringExtra("productId"));

                ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                productDetailsFragment.setArguments(bundle);
//        productDetailsFragment.setArguments(bundle);
                transaction.replace(R.id.fragment_container, productDetailsFragment, "OrderDetailsActivity");
                transaction.addToBackStack("OrderDetailsActivity");
                transaction.commit();
                break;
            }
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