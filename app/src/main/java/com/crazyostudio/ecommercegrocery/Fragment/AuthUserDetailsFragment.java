package com.crazyostudio.ecommercegrocery.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthUserDetailsBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.TokenManager;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.Task;

import java.util.Objects;


public class AuthUserDetailsFragment extends Fragment {
    FragmentAuthUserDetailsBinding binding;
    private String number;
    NavController navController;
    DatabaseService service;
    AuthService authService;
    String uid;

    public AuthUserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            number = getArguments().getString("number");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAuthUserDetailsBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        service = new DatabaseService();
        authService = new AuthService();
        uid = authService.getUserId();
//        binding.userImage.setOnClickListener(view -> ShowDialog());
        binding.nextBtn.setOnClickListener(onclick->{
            service.CheckNotificationToken(new DatabaseService.UpdateTokenCallback() {
                @Override
                public void onSuccess(String token) {
                    TokenManager.getInstance(requireContext()).saveToken(token);
                    setupUser(token);

                }

                @Override
                public void onError(String errorMessage) {
                    Log.i("onError", "onError: "+errorMessage);
                }
            });
        });
        return binding.getRoot();
    }
    void setupUser(String token){
        UserinfoModels UserinfoModels = new UserinfoModels(token,uid, Objects.requireNonNull(binding.Name.getText()).toString(),number,true);
        service.setUserInfo(UserinfoModels, new DatabaseService.SetUserInfoCallback() {
            @Override
            public void onSuccess(Task<Void> task) {
                requireActivity().finish();
            }
            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });
    }
}