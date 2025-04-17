package com.ronosoft.alwarmart.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.databinding.FragmentSiginBinding;

public class SiginFragment extends Fragment {
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String TERMS_URL = "https://www.alwarmart.in/terms-and-conditions";
    private FragmentSiginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSiginBinding.inflate(inflater, container, false);
        setupViews();
        restoreState(savedInstanceState);
        return binding.getRoot();
    }

    private void setupViews() {
        // Continue button
        binding.button.setOnClickListener(v -> proceedToOtp());

        // Terms and conditions link
        binding.link.setOnClickListener(v -> openTermsAndConditions());


        // Clear errors on input change
        binding.Number.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.phoneInputLayout.setError(null);
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkBox.setError(null);
            }
        });
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String phoneNumber = savedInstanceState.getString(KEY_PHONE_NUMBER);
            if (!TextUtils.isEmpty(phoneNumber)) {
                binding.Number.setText(phoneNumber);
            }
        }
    }

    private void proceedToOtp() {
        if (!isAdded() || binding == null) return;

        String phoneNumber = binding.Number.getText() != null ? binding.Number.getText().toString().trim() : "";
        if (!validateInput(phoneNumber)) return;

        if (!binding.checkBox.isChecked()) {
            binding.checkBox.setError(getString(R.string.accept_terms_error));
            showToast(getString(R.string.accept_terms_error));
            return;
        }

        try {
            NavController navController = Navigation.findNavController(requireView());
            Bundle bundle = new Bundle();
            bundle.putString("number", phoneNumber);
            navController.navigate(R.id.action_siginFragment_to_authOTP, bundle);
        } catch (IllegalArgumentException e) {
            showToast(getString(R.string.navigation_error));
        }
    }

    private boolean validateInput(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.phoneInputLayout.setError(getString(R.string.phone_required_error));
            return false;
        }
        if (phoneNumber.length() != 10) {
            binding.phoneInputLayout.setError(getString(R.string.phone_invalid_error));
            return false;
        }
        if (!phoneNumber.matches("\\d+")) {
            binding.phoneInputLayout.setError(getString(R.string.phone_digits_only_error));
            return false;
        }
        binding.phoneInputLayout.setError(null);
        return true;
    }

    private void openTermsAndConditions() {
        if (!isAdded()) return;

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL));
            startActivity(intent);
        } catch (Exception e) {
            showToast(getString(R.string.terms_open_error));
        }
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding != null && binding.Number.getText() != null) {
            outState.putString(KEY_PHONE_NUMBER, binding.Number.getText().toString().trim());
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}