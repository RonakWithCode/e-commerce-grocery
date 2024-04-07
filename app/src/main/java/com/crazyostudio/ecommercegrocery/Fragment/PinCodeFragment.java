package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentPinCodeBinding;

import java.util.Objects;

public class PinCodeFragment extends Fragment {

    String number;
    NavController navController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getString("number");

        }
    }




    FragmentPinCodeBinding binding;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPinCodeBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);

        binding.nextBtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.pinCodeEditText.getText()).toString().equals("301001")){
                Bundle bundle = new Bundle();
                bundle.putString("number",number);
                navController.navigate(R.id.action_pinCodeFragment_to_authUserDetailsFragment);
            }else {
                binding.error.setVisibility(View.VISIBLE);
                binding.error.setText("Oops! We're not there yet... Sorry, we don't provide service in your city right now, but we will do so in the future.");

            }
        });


        return binding.getRoot();
    }
}