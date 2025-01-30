package com.ronosoft.alwarmart.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.ronosoft.alwarmart.Activity.AllOrderActivity;
import com.ronosoft.alwarmart.Activity.AuthMangerActivity;
import com.ronosoft.alwarmart.Activity.FragmentLoader;
import com.ronosoft.alwarmart.Activity.SettingsActivity;
import com.ronosoft.alwarmart.MainActivity;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.AuthService;
import com.ronosoft.alwarmart.databinding.FragmentMoreBinding;


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
            binding.mainLayout.setVisibility(View.VISIBLE);
            binding.relativeNotAuth.setVisibility(View.GONE);
            binding.Username.setText(authService.getUserName());

            Glide.with(requireContext())
                    .load(R.drawable.ic_profile)
                    .placeholder(R.drawable.product_image_shimmee_effect)
                    .error(R.drawable.ic_profile)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .centerCrop()
                    .into(binding.userImage);
        } else {
            binding.relativeNotAuth.setVisibility(View.VISIBLE);
            binding.mainLayout.setVisibility(View.GONE);
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
  /*             <Button style="@style/MoreButtonLight"
        android:id="@+id/language"
        android:padding="@dimen/_1sdp"
        android:text="@string/language"
                />
        binding.language.setOnClickListener(view -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loader, new SelectLanguageFragment(), "CategoryFragment");
            transaction.addToBackStack("SelectLanguageFragment");
            transaction.commit();
        });*/
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