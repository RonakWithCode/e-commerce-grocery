package com.crazyostudio.ecommercegrocery.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentUserAccountBinding;
import com.crazyostudio.ecommercegrocery.databinding.ImgaepickerBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.github.dhaval2404.imagepicker.ImagePicker;
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
    private Uri UriDP;
    private UserinfoModels userInfo;
    private ArrayList<AddressModel> adderes;
    private FirebaseAuth auth;
    private String Gander = "Male";
    private final int IMAGE_REQUEST_CODE = 123;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private boolean IsImageUpdate = false;
    public UserAccountFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
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
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(imgaepickerBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.newcategoryboxbg);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Animationboy;
        dialog.show();

        imgaepickerBinding.cancel.setOnClickListener(view -> dialog.dismiss());
//
//            <ImageView
//        android:layout_below="@id/view_text"
//        android:layout_width="@dimen/_100sdp"
//        android:id="@+id/camera"
//        android:src="@drawable/ic_baseline_camera_enhance_24"
//        android:layout_marginStart="@dimen/_35sdp"
//        android:layout_height="@dimen/_100sdp"/>
//        imgaepickerBinding.camera.setOnClickListener(view -> {
////            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////            if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
////                startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
////            }
//
//
//
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//// Ensure that there is a camera activity to handle the intent
//            if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
//                // Start the camera intent
//                startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
//            }
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

    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
//            binding.userImage.setImageURI(selectedImage);
                    Glide.with(requireContext()).load(uri).into(binding.userImage);
                    UriDP = uri;
//                    Toast.makeText(requireContext(), "Image was get", Toast.LENGTH_SHORT).show();
                    IsImageUpdate = true;
                } else {
                    IsImageUpdate = false;
                }
            });
    private void init() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        adderes = new ArrayList<>();
        userInfo = new UserinfoModels();
        addressAdapter = new AddressAdapter(adderes,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.UserAddress.setLayoutManager(layoutManager);
        binding.UserAddress.setAdapter(addressAdapter);
        firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "ClickableViewAccessibility"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userInfo = snapshot.getValue(UserinfoModels.class);
                    binding.UserName.setText(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());
                    Glide.with(requireContext()).load(auth.getCurrentUser().getPhotoUrl()).into(binding.userImage);
                    UriDP = auth.getCurrentUser().getPhotoUrl();
                    binding.Usernumber.setText(""+userInfo.getPhoneNumber());
                    binding.UserMail.setText(userInfo.getEmailAddress());
                    int checkDrawable;
                    boolean IsVerify;
                    AuthService authService = new AuthService();
                    if (authService.checkEmailVerificationStatus()) {
                        checkDrawable = R.drawable.check;
                        IsVerify = true;
                    }
                    else {
                        IsVerify = false;
                        checkDrawable = R.drawable.worng;
                    }

                    binding.emailStable.setImageResource(checkDrawable);
                    binding.emailStable.setOnClickListener(view->{
                        if (IsVerify) {
                            Toast.makeText(requireContext(), "email is verify" , Toast.LENGTH_SHORT).show();
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                            builder.setTitle("Verify Email");
                            builder.setMessage("Do you want to verify this email \n "+ binding.UserMail.getText().toString());

                            // Positive button (Yes)
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                binding.progressCircular.setVisibility(View.VISIBLE);

                                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                        progressDialog.setTitle("Verify Email");
                                        progressDialog.setMessage("Check the Email box \n if it take a long time close the app then open ");
                                        progressDialog.setCancelable(false);
                                        progressDialog.setButton("Verified", (w, a) -> {
                                            // Retry sending email verification when the button is clicked
                                            if (authService.checkEmailVerificationStatus()){
                                                progressDialog.dismiss();
                                                requireActivity().finish();
                                            }else {
                                                Toast.makeText(requireContext(), "Verify not email.", Toast.LENGTH_SHORT).show();
                                                if (!progressDialog.isShowing()) {
                                                    progressDialog.show();
                                                }
                                            }
                                        });
                                        binding.progressCircular.setVisibility(View.GONE);
                                        progressDialog.show();
                            });
                            // Negative button (No)
                            builder.setNegativeButton("No", (dialog, which) -> {});
                            // Create and show the AlertDialog
                            AlertDialog dialog = builder.create();

                            dialog.show();
                        }
                    });


//                    binding.UserMail.setOnClickListener(view->{
//                        Toast.makeText(requireContext(), ToastOFUserMail[0], Toast.LENGTH_SHORT).show();
//                        if (ToastOFUserMail[0].equals("email is not verify.")){
//                            authService.sendEmailVerification(new AuthService.LinkEmailCallback() {
//                                @Override
//                                public void onSuccess() {
//                                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
//                                    progressDialog.setTitle("Verify Email");
//                                    progressDialog.setMessage("Check the Email box \n if it take a long time close the app then open ");
//                                    progressDialog.setCancelable(false);
//                                    progressDialog.setButton("Verified", (dialog, which) -> {
//                                        // Retry sending email verification when the button is clicked
//                                        if (authService.checkEmailVerificationStatus()){
//                                            progressDialog.dismiss();
//                                            ToastOFUserMail[0] = "email is verify.";
//                                        }else {
//                                            Toast.makeText(requireContext(), "Verify the email.", Toast.LENGTH_SHORT).show();
//                                            if (!progressDialog.isShowing()) {
//                                                progressDialog.show();
//                                            }
//                                        }
//                                    });
//
////                            binding.ProgressBar.setVisibility(View.GONE);
//                                    progressDialog.show();
//
//                                }
//                                @Override
//                                public void onError(Exception errorMessage) {
//
//                                }
//                            });
//
//                        }
//                    });

                    if (snapshot.child("gender").exists())
                    {
                        if (userInfo.getGender().equals("Male")){
                            binding.radiomale.setChecked(true);
                        }
                        else {
                            binding.radiofemale.setChecked(true);
                        }
                    }
                    else {
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
                basicFun.AlertDialog(requireContext(),error.toString());
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
//
    void back() {
        if (basicFun.CheckField(binding.UserName) && basicFun.CheckField(binding.UserMail)) {
            binding.progressCircular.setVisibility(View.VISIBLE);
            long Time = System.currentTimeMillis();
            if (IsImageUpdate) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference("UserImage");
                StorageReference imageRef = storageRef.child(Time + getFileExtensionFromUri(UriDP));
                imageRef.putFile(UriDP)
                        .addOnSuccessListener(taskSnapshot -> // Use the download URL as needed (e.g., save it in your database)
                                // Now, you have the download URL of the uploaded image.
                                imageRef.getDownloadUrl()
                                .addOnSuccessListener(this::setupUser)
                                .addOnFailureListener(exception -> {
                                    setupUser(Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/default%20images%2Fuser.png?alt=media&token=7c97004c-7632-42de-8649-ce674f139893"));
                                }))
                        .addOnFailureListener(exception -> {
                            setupUser(Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/default%20images%2Fuser.png?alt=media&token=7c97004c-7632-42de-8649-ce674f139893"));
                        });


            }
            else {
                setupUser(Uri.parse(userInfo.getProfilePictureUrl()));
            }
        }
    }
    private void setupUser(Uri Image) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(binding.UserName.getText().toString()).setPhotoUri(Image)
                .build();
        UserinfoModels models = new UserinfoModels(userInfo.getToken(),auth.getUid(), binding.UserName.getText().toString(),binding.UserMail.getText().toString(),Image.toString(),Gander,adderes, Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber(),true);
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
//            Uri selectedImage = data.getData();
////            binding.userImage.setImageURI(selectedImage);
//            Glide.with(requireContext()).load(selectedImage).into(binding.userImage);
//            UriDP = selectedImage;
//            Toast.makeText(requireContext(), "Image was get", Toast.LENGTH_SHORT).show();
//            IsImageUpdate = true;
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