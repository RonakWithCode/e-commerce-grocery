package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentPlaceOderBinding;

public class PlaceOderFragment extends Fragment {
    FragmentPlaceOderBinding binding;
    private String PaymentMode;
    private String adders;

    public PlaceOderFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PaymentMode = getArguments().getString("PaymentMode");
            adders = getArguments().getString("adders");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaceOderBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}