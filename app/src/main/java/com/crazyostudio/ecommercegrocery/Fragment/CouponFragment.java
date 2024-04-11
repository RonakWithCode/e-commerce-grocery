package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentCouponBinding;

public class CouponFragment extends Fragment {
    FragmentCouponBinding binding;
    public CouponFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCouponBinding.inflate(inflater,container,false);

        binding.applyButton.setOnClickListener(v -> {
            boolean IsValid = validateCoupon(binding.couponInput.getText().toString());
            if (IsValid){
                redirectToCheckoutFragment("500","10",IsValid);
            }else {
                redirectToCheckoutFragment(null,null,IsValid);
            }


        });


        return binding.getRoot();
    }


    private void redirectToCheckoutFragment(String couponPrice, String couponTotal, boolean isValid) {
        Bundle bundle = new Bundle();
        bundle.putString("couponPrice", couponPrice);
        bundle.putString("couponTotal", couponTotal);
        bundle.putBoolean("isValidCoupon", isValid);

        // Hide the current fragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().hide(this).commit();

        // Show the CheckoutFragment if it exists
        CheckoutFragment checkoutFragment = (CheckoutFragment) fragmentManager.findFragmentByTag("CheckoutFragment");
        if (checkoutFragment != null) {
            checkoutFragment.setArguments(bundle);
            fragmentManager.beginTransaction().show(checkoutFragment).commit();
        } else {
            // If CheckoutFragment doesn't exist, create and add it
            checkoutFragment = new CheckoutFragment();
            checkoutFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, checkoutFragment, "CheckoutFragment")
                    .commit();
        }
    }

    private boolean validateCoupon(String couponCode) {
        return false;
    }
}