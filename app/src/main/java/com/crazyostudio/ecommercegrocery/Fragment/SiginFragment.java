package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentSiginBinding;

public class SiginFragment extends Fragment {
    FragmentSiginBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSiginBinding.inflate(inflater, container, false);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        binding.button.setOnClickListener(v -> {
            if (binding.Number.getText() != null) {
                String phoneNumber = binding.Number.getText().toString().trim();
                if (validateInput(phoneNumber)) {
                    if (binding.checkBox.isChecked()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("number", phoneNumber);
                        navController.navigate(R.id.action_siginFragment_to_authOTP, bundle);
                    } else {
                        binding.checkBox.setError("Please accept the terms and conditions");
                        Toast.makeText(requireContext(), "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                binding.phoneInputLayout.setError("Phone number is required");
            }
        });

//        binding.changeLanguage.setOnClickListener(view -> {
//            SelectLanguageFragment dialogFragment = new SelectLanguageFragment();
//            dialogFragment.show(requireActivity().getSupportFragmentManager(), "selectLanguageDialog");
//
////            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
////            transaction.replace(R.id.loader, new SelectLanguageFragment(), "CategoryFragment");
////            transaction.addToBackStack("SelectLanguageFragment");
////            transaction.commit();
//        });

        return binding.getRoot();
    }

    private boolean validateInput(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.phoneInputLayout.setError("Phone number is required");
            return false;
        } else if (phoneNumber.length() != 10) {
            binding.phoneInputLayout.setError("Enter a valid 10-digit phone number");
            return false;
        } else {
            binding.phoneInputLayout.setError(null);
            return true;
        }
    }


}
