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
import com.crazyostudio.ecommercegrocery.Services.FileService;
import com.crazyostudio.ecommercegrocery.Services.StorageDatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthUserDetailsBinding;
import com.crazyostudio.ecommercegrocery.databinding.ImgaepickerBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.Task;


public class AuthUserDetailsFragment extends Fragment {
    FragmentAuthUserDetailsBinding binding;
    private String number;
    NavController navController;
    private String token;
    private final static int IMAGE_REQUEST_CODE = 123;
    private Uri userImage;
    private boolean IsImageSelect;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
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
        binding.userImage.setOnClickListener(view -> ShowDialog());
        binding.signup.setOnClickListener(onclick->{
            if (basicFun.CheckField(binding.Mail)&& basicFun.CheckField(binding.Name)) {
                long time = System.currentTimeMillis();
                binding.ProgressBar.setVisibility(View.VISIBLE);
                if (!IsImageSelect) {
                    setupUser("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/default%20images%2Fuser.png?alt=media&token=7c97004c-7632-42de-8649-ce674f139893");
                }
                else {
                    String Child = time + "."+ new FileService().filletExtension(userImage.toString(),requireContext());
                    new StorageDatabaseService().UploadImage(uid, Child, userImage, new StorageDatabaseService.UploadImageCallback() {
                        @Override
                        public void onSuccess(Uri Images) {
                            setupUser(Images.toString());
                        }

                        @Override
                        public void onError(Exception errorMessage) {
                            basicFun.AlertDialog(requireContext(),errorMessage.toString());
                            binding.ProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        return binding.getRoot();
    }

    void setupUser(String imageUrl){
        UserinfoModels UserinfoModels = new UserinfoModels(token,uid, binding.Name.getText().toString(),binding.Mail.getText().toString(),imageUrl,number,true);
        service.setUserInfo(UserinfoModels, new DatabaseService.SetUserInfoCallback() {
            @Override
            public void onSuccess(Task<Void> task) {
                authService.linkEmailCredential(UserinfoModels.getUsername(),UserinfoModels.getProfilePictureUrl(), UserinfoModels.getEmailAddress(), binding.password.getText().toString(), new AuthService.LinkEmailCallback() {
                    @Override
                    public void onSuccess() {
//                        ProgressBar/
                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                        progressDialog.setTitle("Verify Email");
                        progressDialog.setMessage("Check the Email box \n if it take a long time close the app then open ");
                        progressDialog.setCancelable(false);
                        progressDialog.setButton("Verified", (dialog, which) -> {
                            // Retry sending email verification when the button is clicked
                            if (authService.checkEmailVerificationStatus()){
                                progressDialog.dismiss();
                                requireActivity().finish();
                            }else {
                                Toast.makeText(requireContext(), "Verify the email.", Toast.LENGTH_SHORT).show();
                                if (!progressDialog.isShowing()) {
                                    progressDialog.show();
                                }
                            }
                        });

                        binding.ProgressBar.setVisibility(View.GONE);
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

    private void ShowDialog() {
        ImgaepickerBinding imgaepickerBinding = ImgaepickerBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(getContext());
// Set the layout parameters to center the layout
        dialog.setContentView(imgaepickerBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.newcategoryboxbg);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
        dialog.show();
//        imgaepickerBinding.camera.setOnClickListener(view -> {
//            // Check if the camera permission is already granted.
//            if (isCameraPermissionGranted()) {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
//                }
//            } else {
//                // Camera permission is not granted. Request permission from the user.
//                requestCameraPermission();
//            }
//
//        });
        imgaepickerBinding.photo.setOnClickListener(view -> {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            startActivityForResult(intent, IMAGE_REQUEST_CODE);
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            dialog.dismiss();
        });
        imgaepickerBinding.cancel.setOnClickListener(view -> dialog.dismiss());

    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    userImage = uri;
                    Glide.with(requireContext()).load(userImage).into(binding.userImage);
                    IsImageSelect = true;

                } else {
                    IsImageSelect = false;
                }
            });
    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted. You can use the camera here.
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
                }
                } else {
                // Camera permission denied. Handle it accordingly (e.g., show a message to the user).
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
////            userImage = data.getData();
////            Glide.with(requireContext()).load(userImage).into(binding.userImage);
////            IsImageSelect = true;
////        }
//    }

}