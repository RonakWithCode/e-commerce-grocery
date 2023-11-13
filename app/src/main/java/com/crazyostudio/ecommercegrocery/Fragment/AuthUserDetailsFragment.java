package com.crazyostudio.ecommercegrocery.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentAuthUserDetailsBinding;
import com.crazyostudio.ecommercegrocery.databinding.ImgaepickerBinding;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;


public class AuthUserDetailsFragment extends Fragment {
    FragmentAuthUserDetailsBinding binding;
    FirebaseDatabase db;
    private String number;
    NavController navController;
    private Uri userImage;
    private String token;
    private final int IMAGE_REQUEST_CODE = 123;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

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
        db  = FirebaseDatabase.getInstance();
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        binding.number.setText(number);



        binding.userImage.setOnClickListener(view -> ShowDialog());





        binding.signup.setOnClickListener(onclick->{
            if (basicFun.CheckField(binding.Mail)&& basicFun.CheckField(binding.Name)) {
                binding.ProgressBar.setVisibility(View.VISIBLE);
                if (userImage == null) {
                    userImage = Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/UserImage%2Fperson.png?alt=media&token=599cbe56-3620-4d52-9e51-58e57b936596");
                    setupUser();
                } else {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference("UserImage");
                    StorageReference imageRef = storageRef.child(System.currentTimeMillis() + filletExtension(userImage.toString()));
                    Uri imageUri = userImage;
                    imageRef.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        // URI of the uploaded image
                                        userImage = uri;
                                        setupUser();
                                        // Use the download URL as needed (e.g., save it in your database)
                                        // Now, you have the download URL of the uploaded image.
                                    })
                                    .addOnFailureListener(exception -> {
                                        userImage = Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/UserImage%2Fperson.png?alt=media&token=599cbe56-3620-4d52-9e51-58e57b936596");
                                        setupUser();
                                    }))
                            .addOnFailureListener(exception -> {
                                userImage = Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/UserImage%2Fperson.png?alt=media&token=599cbe56-3620-4d52-9e51-58e57b936596");
                                setupUser();
                            });
                }
            }
        });
        return binding.getRoot();
    }

    void setupUser(){
        UserinfoModels UserinfoModels = new UserinfoModels(token,FirebaseAuth.getInstance().getUid(), binding.Name.getText().toString(),binding.Mail.getText().toString(),String.valueOf(userImage),number,true);
        db.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(UserinfoModels).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(userImage)
                        .setDisplayName(binding.Name.getText().toString()).build();
                assert user != null;
                user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        binding.ProgressBar.setVisibility(View.GONE);
                        requireActivity().finish();
                    }else {
                        basicFun.AlertDialog(requireContext(),"Error in Setup Profile");
                    }
                }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));


            }else {
                basicFun.AlertDialog(requireContext(),task.toString());
            }

        }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
    }


    private void ShowDialog() {

        ImgaepickerBinding imgaepickerBinding = ImgaepickerBinding.inflate(getLayoutInflater());
        Dialog imgaepickerBox = new Dialog(getContext());
        imgaepickerBox.setContentView(imgaepickerBinding.getRoot());

// Set the layout parameters to center the layout
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(imgaepickerBox.getWindow().getAttributes());
        layoutParams.gravity = Gravity.CENTER; // Center the dialog
        imgaepickerBox.getWindow().setAttributes(layoutParams);

        imgaepickerBox.getWindow().setBackgroundDrawableResource(R.drawable.createsubjectsboxbg);
        imgaepickerBox.setCancelable(true);
        imgaepickerBox.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
        imgaepickerBox.show();
        imgaepickerBinding.camera.setOnClickListener(view -> {
            // Check if the camera permission is already granted.
            if (isCameraPermissionGranted()) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
                }
            } else {
                // Camera permission is not granted. Request permission from the user.
                requestCameraPermission();
            }

        });
        imgaepickerBinding.photo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
            imgaepickerBox.dismiss();
        });
        imgaepickerBinding.CANCEL.setOnClickListener(view -> imgaepickerBox.dismiss());

    }


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
            } else {
                // Camera permission denied. Handle it accordingly (e.g., show a message to the user).
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    private String filletExtension(String uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Get the file extension based on the Uri's MIME type
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(Uri.parse(uri)));

        if (extension == null) {
            // If the MIME type doesn't provide an extension, try to extract from the Uri's path

            if (uri != null) {
                int extensionStartIndex = uri.lastIndexOf('.');
                if (extensionStartIndex != -1) {
                    extension = uri.substring(extensionStartIndex + 1);
                }
            }
        }

        return extension;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Glide.with(requireContext()).load(selectedImage).into(binding.userImage);
            userImage = selectedImage;
        }
    }

}