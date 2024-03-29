package com.crazyostudio.ecommercegrocery.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthUserDetailsBinding;
import com.crazyostudio.ecommercegrocery.databinding.ImgaepickerBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.Task;


public class AuthUserDetailsFragment extends Fragment {
    FragmentAuthUserDetailsBinding binding;
    private String number;
    private String token;
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
            token = getArguments().getString("token");
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
            setupUser();
        });
        return binding.getRoot();
    }

    void setupUser(){
        UserinfoModels UserinfoModels = new UserinfoModels(token,uid, binding.Name.getText().toString(),binding.email.getText().toString(), "https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/default%20images%2Fuser.png?alt=media&token=7c97004c-7632-42de-8649-ce674f139893",number,true);
        service.setUserInfo(UserinfoModels, new DatabaseService.SetUserInfoCallback() {
            @Override
            public void onSuccess(Task<Void> task) {
                authService.linkEmailCredential(UserinfoModels.getUsername(),UserinfoModels.getProfilePictureUrl(), UserinfoModels.getEmailAddress(), binding.password.getText().toString(), new AuthService.LinkEmailCallback() {
                    @Override
                    public void onSuccess() {
                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                        progressDialog.setTitle("Verify Email");
                        progressDialog.setMessage("Check the Email box \n if it take a long time close the app then open ");
                        progressDialog.setCancelable(false);
                        progressDialog.setButton("Verified", (dialog, which) -> {
                            // Retry sending email verification when the button is clicked
                            if (authService.checkEmailVerificationStatus()){
                                progressDialog.dismiss();
                                requireActivity().finish();
                                Toast.makeText(requireContext(), "successful in Verifying the email.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(requireContext(), "failed to Verify the email.", Toast.LENGTH_SHORT).show();
                                if (!progressDialog.isShowing()) {
                                    progressDialog.show();
                                }
                            }
                        });
                        progressDialog.show();
                    }

                    @Override
                    public void onError(Exception errorMessage) {
                        basicFun.AlertDialog(requireContext(),errorMessage.toString());
                    }
                });
            }
            @Override
            public void onError(String errorMessage) {
                basicFun.AlertDialog(requireContext(),errorMessage);
            }
        });
    }





}