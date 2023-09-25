package com.crazyostudio.ecommercegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.crazyostudio.ecommercegrocery.Fragment.AddressFragment;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityOderBinding;
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
            FirebaseDatabase.getInstance().getReference().child("Error").setValue(errorMessage);
        }
        String BuyMode = getIntent().getStringExtra("BuyType");
        if (!BuyMode.isEmpty()){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new AddressFragment();
            Bundle bundle = new Bundle();
            bundle.putString("BuyType",BuyMode);
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,fragment,"Address");
            transaction.addToBackStack("Address");
            transaction.commit();
        }else {
            finish();
        }



    }
}