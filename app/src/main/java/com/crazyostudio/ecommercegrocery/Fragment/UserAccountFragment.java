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
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentUserAccountBinding;
import com.crazyostudio.ecommercegrocery.databinding.ImgaepickerBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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


        init();
        binding.AddAddress.setOnClickListener(address->{
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,new newAddressFragment(),"newAddress");
            transaction.addToBackStack("newAddress");
            transaction.commit();
        });
        return binding.getRoot();
    }




  private void init() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        adderes = new ArrayList<>();
        userInfo = new UserinfoModels();
        addressAdapter = new AddressAdapter(adderes,this,requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.UserAddress.setLayoutManager(layoutManager);
        binding.UserAddress.setAdapter(addressAdapter);
        new DatabaseService().getUserInfoByDocumentSnapshot(auth.getUid(), new DatabaseService.getUserInfoDocumentSnapshotCallback() {
            @Override

            public void onSuccess(DocumentSnapshot user) {
                UserinfoModels models = user.toObject(UserinfoModels.class);
                if (models != null) {
                    binding.email.setText(models.getEmailAddress());
                    binding.Name.setText(models.getUsername());
                    if (models.getAddress() != null && !models.getAddress().isEmpty()) {
                        adderes.clear();
                        adderes.addAll(models.getAddress());
                        addressAdapter.notifyDataSetChanged();
                    }
                }
                binding.progressCircular.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }


//    void back() {
//        if (basicFun.CheckField(binding.UserName) && basicFun.CheckField(binding.UserMail)) {
//            binding.progressCircular.setVisibility(View.VISIBLE);
//            long Time = System.currentTimeMillis();
//            if (IsImageUpdate) {
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference("UserImage");
//                StorageReference imageRef = storageRef.child(Time + getFileExtensionFromUri(UriDP));
//                imageRef.putFile(UriDP)
//                        .addOnSuccessListener(taskSnapshot -> // Use the download URL as needed (e.g., save it in your database)
//                                // Now, you have the download URL of the uploaded image.
//                                imageRef.getDownloadUrl()
//                                .addOnSuccessListener(this::setupUser)
//                                .addOnFailureListener(exception -> {
//                                    setupUser(Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/default%20images%2Fuser.png?alt=media&token=7c97004c-7632-42de-8649-ce674f139893"));
//                                }))
//                        .addOnFailureListener(exception -> {
//                            setupUser(Uri.parse("https://firebasestorage.googleapis.com/v0/b/e-commerce-11d7d.appspot.com/o/default%20images%2Fuser.png?alt=media&token=7c97004c-7632-42de-8649-ce674f139893"));
//                        });
//
//
//            }
//            else {
//                setupUser(Uri.parse(userInfo.getProfilePictureUrl()));
//            }
//        }
//    }
//    private void setupUser(Uri Image) {
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(binding.UserName.getText().toString()).setPhotoUri(Image)
//                .build();
//        UserinfoModels models = new UserinfoModels(userInfo.getToken(),auth.getUid(), binding.UserName.getText().toString(),binding.UserMail.getText().toString(),Image.toString(),Gander,adderes, Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber(),true);
//        Objects.requireNonNull(auth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(models).addOnCompleteListener(task1 -> {
//                    if (task1.isSuccessful()) {
//                        requireActivity().finish();
//                        binding.progressCircular.setVisibility(View.GONE);
//                    }
//                }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
//            }
//        }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
//        binding.progressCircular.setVisibility(View.GONE);
//    }
    @Override
    public void addersSelect(AddressModel adders) {}
//    TODO make the function in database class for removing user AddressModel
    @Override
    public void remove(AddressModel address, int pos) {
        binding.progressCircular.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String userId = currentUser.getUid();
            firestore.collection("UserInfo")
                    .document(userId)
                    .update("address", FieldValue.arrayRemove(address))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            binding.progressCircular.setVisibility(View.INVISIBLE);
                            addressAdapter.notifyDataSetChanged();
                        } else {
                            // Handle task failure
                            basicFun.AlertDialog(requireContext(), "Failed to remove address: " + task.getException().getMessage());
                        }
                    });
        } else {
            basicFun.AlertDialog(requireContext(), "User not authenticated.");
        }
    }


}