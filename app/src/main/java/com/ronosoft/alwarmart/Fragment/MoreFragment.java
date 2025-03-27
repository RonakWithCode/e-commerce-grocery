package com.ronosoft.alwarmart.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Task;
import com.ronosoft.alwarmart.Activity.AuthMangerActivity;
import com.ronosoft.alwarmart.Activity.AllOrderActivity;
import com.ronosoft.alwarmart.Activity.FragmentLoader;
import com.ronosoft.alwarmart.Activity.SettingsActivity;
import com.ronosoft.alwarmart.MainActivity;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.javaClasses.AddressDeliveryService;
import com.ronosoft.alwarmart.databinding.FragmentMoreBinding;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;
    private AuthService authService;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        authService = new AuthService();

        // Check if the user is logged in
        if (authService.IsLogin()) {
            binding.mainLayout.setVisibility(View.VISIBLE);
            binding.relativeNotAuth.setVisibility(View.GONE);
            loadUserProfile();
        } else {
            binding.relativeNotAuth.setVisibility(View.VISIBLE);
            binding.mainLayout.setVisibility(View.GONE);
        }

        // Set up click listeners
        binding.account.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), FragmentLoader.class);
            intent.putExtra("LoadID", "UserAccountFragment");
            startActivity(intent);
        });
        binding.order.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), AllOrderActivity.class))
        );
        binding.continueShipping.setOnClickListener(view -> {
            requireActivity().finish();
            startActivity(new Intent(requireContext(), MainActivity.class));
        });
        binding.settings.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), SettingsActivity.class))
        );
        binding.siginUp.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), AuthMangerActivity.class))
        );

        return binding.getRoot();
    }

    /**
     * Loads the user profile details if the user is logged in.
     */
    private void loadUserProfile() {
        // Use AddressDeliveryService to get default address details
        AddressDeliveryService service = new AddressDeliveryService();
        AddressModel model = service.getDefaultAddress(requireContext());
        if (model != null) {
            binding.Username.setText(model.getFullName());
            binding.userEmail.setText(model.getMobileNumber());
        } else {
            // Fallback values if no address is set
            binding.Username.setText(authService.getUserName());
//            Task phone = authService.getUserPhoneNumber();
            binding.userEmail.setText("");
        }
        // Load the profile image with Glide
        try {
            Glide.with(requireContext())
                    .load(R.drawable.ic_profile)
                    .placeholder(R.drawable.product_image_shimmee_effect)
                    .error(R.drawable.ic_profile)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .centerCrop()
                    .into(binding.userImage);
        } catch (Exception e) {
            e.printStackTrace();
            binding.userImage.setImageResource(R.drawable.ic_profile);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up binding reference to avoid memory leaks
        binding = null;
    }
}
