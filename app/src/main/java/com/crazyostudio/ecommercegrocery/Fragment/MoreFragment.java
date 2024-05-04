package com.crazyostudio.ecommercegrocery.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Activity.AllOrderActivity;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Activity.FragmentLoader;
import com.crazyostudio.ecommercegrocery.Activity.SettingsActivity;
import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentMoreBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MoreFragment extends Fragment {
    FragmentMoreBinding binding;
    AuthService authService;
    public MoreFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(inflater,container,false);





        authService = new AuthService();
        if (authService.IsLogin()) {
            binding.relativeNotAuth.setVisibility(View.VISIBLE);
        }else {
            binding.mainLayout.setVisibility(View.VISIBLE);
            binding.Username.setText(authService.getUserName());
//            binding.Email.setText(authService.getUserEmail());

//            if (authService.getUserUrl()!=null) {

                Glide.with(requireContext()).load(R.drawable.adduser).into(binding.userImage);
//            }
        }


        binding.account.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), FragmentLoader.class);
            intent.putExtra("LoadID","UserAccountFragment");
//            intent.putExtra("LoadID","MoreAddress");
            startActivity(intent);

        });
        binding.order.setOnClickListener(view -> {
//                  OrderFragment Load
            Intent intent = new Intent(requireContext(), AllOrderActivity.class);
            startActivity(intent);
        });
//        binding.address.setOnClickListener(view -> {
//            Intent intent = new Intent(requireContext(), FragmentLoader.class);
//            intent.putExtra("LoadID","MoreAddress");
////            intent.putExtra("LoadID","MoreAddress");
//            startActivity(intent);
//        });
        binding.continueShipping.setOnClickListener(view -> {
            requireActivity().finish();
            startActivity(new Intent(requireContext(), MainActivity.class));

        });
        binding.language.setOnClickListener(view -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, new SelectLanguageFragment(), "CategoryFragment");
            transaction.addToBackStack("SelectLanguageFragment");
            transaction.commit();


        });
        binding.settings.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
//            intent.putExtra("LoadID","SettingsFragment");
//            intent.putExtra("LoadID","MoreAddress");
            startActivity(intent);


        });
        binding.siginUp.setOnClickListener(view -> startActivity(new Intent(requireContext(),AuthMangerActivity.class)));

        return binding.getRoot();
    }
}