package com.crazyostudio.ecommercegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.crazyostudio.ecommercegrocery.Fragment.AddressFragment;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.ActivityOderBinding;

public class OderActivity extends AppCompatActivity {
    ActivityOderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOderBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,new AddressFragment(),"Address");
        transaction.addToBackStack("Address");
        transaction.commit();

    }
}