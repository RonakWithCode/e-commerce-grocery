package com.crazyostudio.ecommercegrocery.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Activity.AllOrderActivity;
import com.crazyostudio.ecommercegrocery.Activity.AuthMangerActivity;
import com.crazyostudio.ecommercegrocery.Activity.FragmentLoader;
import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentMoreBinding;
import com.google.firebase.auth.FirebaseAuth;
public class MoreFragment extends Fragment {
    FragmentMoreBinding binding;
    FirebaseAuth auth;



    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(inflater,container,false);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()==null) {
            binding.relativeNotAuth.setVisibility(View.VISIBLE);
        }else {
            binding.mainLayout.setVisibility(View.VISIBLE);
            binding.Username.setText(auth.getCurrentUser().getDisplayName());
            if (auth.getCurrentUser().getPhotoUrl()!=null) {
                Glide.with(requireContext()).load(auth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.placeholder).into(binding.userImage);
            }
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
        binding.address.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), FragmentLoader.class);
            intent.putExtra("LoadID","MoreAddress");
//            intent.putExtra("LoadID","MoreAddress");
            startActivity(intent);
        });
        binding.continueShipping.setOnClickListener(view -> {
            requireActivity().finish();
            startActivity(new Intent(requireContext(), MainActivity.class));

        });
        binding.setPickup.setOnClickListener(view -> {});
        binding.settings.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), FragmentLoader.class);
            intent.putExtra("LoadID","SettingsFragment");
//            intent.putExtra("LoadID","MoreAddress");
            startActivity(intent);


        });
        binding.siginUp.setOnClickListener(view -> startActivity(new Intent(requireContext(),AuthMangerActivity.class)));

        return binding.getRoot();
    }
}