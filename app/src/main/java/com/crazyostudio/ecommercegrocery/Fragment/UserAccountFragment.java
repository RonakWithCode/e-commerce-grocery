package com.crazyostudio.ecommercegrocery.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Adapter.AddressAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.newAddressFragment;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentUserAccountBinding;
import com.crazyostudio.ecommercegrocery.databinding.ImgaepickerBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class UserAccountFragment extends Fragment implements AddressInterface {
    private FragmentUserAccountBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private AddressAdapter addressAdapter;
    private StorageReference reference;
    private Uri UriDP;
    private UserinfoModels userInfo;
    private ArrayList<AddressModel> adderes;
    private FirebaseAuth auth;
    private String Gander = "Male";
    private final int IMAGE_REQUEST_CODE = 123;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    public UserAccountFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference("DP");
        binding.userImage.setOnClickListener(view ->{
            ShowDialog();
//            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
        });
        binding.radiogrp.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i==R.id.radiomale){
                Gander = "Male";
            }
            else if (i==R.id.radiofemale){
                Gander = "Female";
            }
        });
        binding.backButton.setOnClickListener(back-> back());
        init();
        binding.AddAddress.setOnClickListener(address->{
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,new newAddressFragment(),"newAddress");
            transaction.addToBackStack("newAddress");
            transaction.commit();
        });
        return binding.getRoot();
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

    private void init() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        adderes = new ArrayList<>();
        userInfo = new UserinfoModels();
        addressAdapter = new AddressAdapter(adderes,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.UserAddress.setLayoutManager(layoutManager);
        binding.UserAddress.setAdapter(addressAdapter);
        firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userInfo = snapshot.getValue(UserinfoModels.class);
                    assert userInfo != null;
                    if (Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName()).isEmpty()){
                        binding.UserName.setText(userInfo.getUsername());
                    }
                    else {
                        binding.UserName.setText(auth.getCurrentUser().getDisplayName());
                    }
                    Glide.with(requireContext()).load(auth.getCurrentUser().getPhotoUrl()).into(binding.userImage);
                    UriDP = auth.getCurrentUser().getPhotoUrl();
                    binding.Usernumber.setText(""+userInfo.getPhoneNumber());
                    binding.UserMail.setText(userInfo.getEmailAddress());
                    if (snapshot.child("gender").exists())
                    {
                        if (userInfo.getGender().equals("male")){
                            binding.radiomale.setChecked(true);
                        }else {
                            binding.radiofemale.setChecked(true);
                        }
                    }else {
                        binding.radiomale.setChecked(true);
                    }
                    binding.progressCircular.setVisibility(View.INVISIBLE);

                    if (snapshot.child("address").exists()) {
                        adderes.addAll(userInfo.getAddress());
                        binding.NotFoundText.setVisibility(View.INVISIBLE);
                        binding.UserAddress.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.NotFoundText.setVisibility(View.VISIBLE);
                        binding.UserAddress.setVisibility(View.INVISIBLE);
                    }
                }
                addressAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button press here
                back();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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


    void back(){
        if (basicFun.CheckField(binding.UserName)&&basicFun.CheckField(binding.UserMail)){
            binding.progressCircular.setVisibility(View.VISIBLE);
            long Time = System.currentTimeMillis();
            StorageReference file = reference.child(Time + "." + getFileExtensionFromUri(UriDP));
            file.putFile(UriDP).addOnSuccessListener(taskSnapshot -> file.getDownloadUrl().addOnSuccessListener(uri -> UriDP = uri)).addOnFailureListener(e ->
                    UriDP = Uri.parse(userInfo.getProfilePictureUrl()));
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(binding.UserName.getText().toString()).setPhotoUri(UriDP)
                    .build();
            UserinfoModels models = new UserinfoModels(userInfo.getToken(),auth.getUid(), binding.UserName.getText().toString(),binding.UserMail.getText().toString(),UriDP.toString(),Gander,adderes, Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber(),true);
            Objects.requireNonNull(auth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(models).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            requireActivity().finish();
                            binding.progressCircular.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
                }
            }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
        }
        binding.progressCircular.setVisibility(View.GONE);
    }
    @Override
    public void addersSelect(AddressModel adders) {}
    @Override
    public void remove(AddressModel adders, int pos) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        adderes.remove(pos);
        userInfo.setAddress(adderes);
        firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                }
                addressAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Glide.with(requireContext()).load(selectedImage).into(binding.userImage);
            UriDP = selectedImage;
        }
    }
    private String getFileExtensionFromUri(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Get the MIME type of the file
        String mimeType = contentResolver.getType(uri);

        if (mimeType != null) {
            // If the MIME type provides an extension, return it
            return mimeType;
        } else {
            // If the MIME type doesn't provide an extension, try to extract from the Uri's path
            String path = uri.getPath();
            if (path != null) {
                int extensionStartIndex = path.lastIndexOf('.');
                if (extensionStartIndex != -1) {
                    // Get the extension from the path
                    String extension = path.substring(extensionStartIndex + 1);
                    // Return the MIME type based on the extension
                    return mimeTypeMap.getMimeTypeFromExtension(extension);
                }
            }
        }

        // If no MIME type can be determined, return a default value or handle it as needed.
        return "application/octet-stream"; // Change this to the default MIME type you prefer
    }

}