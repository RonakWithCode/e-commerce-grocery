package com.crazyostudio.ecommercegrocery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crazyostudio.ecommercegrocery.Fragment.HomeFragment;
import com.crazyostudio.ecommercegrocery.Fragment.MoreFragment;
import com.crazyostudio.ecommercegrocery.Fragment.ShoppingCartsFragment;
import com.crazyostudio.ecommercegrocery.databinding.ActivityMainBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loader(new HomeFragment(),"null");
//        try {
//            Objects.requireNonNull(this.getSupportActionBar()).hide();
//        }
//        // catch block to handle NullPointerException
//        catch (NullPointerException ignored) {
//            String errorMessage = ignored.toString();
//            FirebaseDatabase.getInstance().getReference().child("Error").setValue(errorMessage);
//
//        }
        binding.bottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            switch (i) {
                case 0:
                    loader(new HomeFragment(),"HomeFragment");
                    System.out.println("Item selected: " + i);
                    return (true);

                case 1:
                    loader(new ShoppingCartsFragment(),"ShoppingCartsFragment");
                    System.out.println("Item selected: " + i);
                    return (true);
                case 2:
                    loader(new MoreFragment(),"MoreFragment");
                    System.out.println("Item selected: " + i);
                    return (true);
            }
            return true;
        });
        binding.bottomBar.setOnItemReselectedListener(itemId -> {
            switch (itemId) {
                case 0:
                    loader(new HomeFragment(),"HomeFragment");
                    break;
                case 1:
                    loader(new ShoppingCartsFragment(),"ShoppingCartsFragment");
                    break;

                case 2:
                    loader(new MoreFragment(),"MoreFragment");
                    break;
            }
        });
    }



    private void loader(Fragment fragment,String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loader,fragment,tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}