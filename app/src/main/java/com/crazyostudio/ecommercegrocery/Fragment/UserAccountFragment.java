package com.crazyostudio.ecommercegrocery.Fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazyostudio.ecommercegrocery.Adapter.AddressAdapter;
import com.crazyostudio.ecommercegrocery.Adapter.newAddressFragment;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentUserAccountBinding;
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
    FragmentUserAccountBinding binding;
    FirebaseDatabase firebaseDatabase;
    AddressAdapter addressAdapter;
    String id;
    private StorageReference reference;
    Uri ImageUri,UriDP;
    UserinfoModels userInfo;
    ArrayList<AddressModel> adderes;
    FirebaseAuth auth;
    String Gander = "Male";
    boolean IsUseImage = false;

    public UserAccountFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(inflater,container,false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference("DP");
        binding.userImage.setOnClickListener(view ->
                ImagePicker.with(requireActivity())
                .crop()
                .compress(1024)
                .maxResultSize(800, 800)
                .start(456));

        binding.radiogrp.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i==R.id.radiomale){
                Gander = "Male";
            }
            else if (i==R.id.radiofemale){
                Gander = "Female";
            }
        });
        binding.backButton.setOnClickListener(back->{
            if (basicFun.CheckField(binding.UserName)&&basicFun.CheckField(binding.UserMail)){
                long Time = System.currentTimeMillis();
                if (IsUseImage){
                    StorageReference file = reference.child(Time + "." + getFileExtensionFromUri(ImageUri));
                    file.putFile(ImageUri).addOnSuccessListener(taskSnapshot -> file.getDownloadUrl().addOnSuccessListener(uri -> {
                        UriDP = uri;
                    })).addOnFailureListener(e ->
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                }else {
                    UriDP = Uri.parse(userInfo.getProfilePictureUrl());
                }
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.UserName.getText().toString()).setPhotoUri(UriDP)
                        .build();
                UserinfoModels models = new UserinfoModels(auth.getUid(), binding.UserName.getText().toString(),binding.UserMail.getText().toString(),UriDP.toString(),Gander,adderes, Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber(),true);
                Objects.requireNonNull(auth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(models).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                requireActivity().finish();
                            }
                        }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));
                    }
                }).addOnFailureListener(e -> basicFun.AlertDialog(requireContext(),e.toString()));

            }
        });
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
//                    Glide.with(requireContext()).load(auth.getCurrentUser().getPhotoUrl()).into(binding.userImage);
                    ImageUri = auth.getCurrentUser().getPhotoUrl();
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
    public void addersSelect(AddressModel adders) {
//        if (!id.equals("MoreAddress")){
//            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//            Fragment fragment = new PaymentScreenFragment();
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("adders",adders);
//            bundle.putParcelable("user",userInfo);
//            fragment.setArguments(bundle);
//            transaction.replace(R.id.fragment_container,fragment,"PaymentScreen");
//            transaction.addToBackStack("PaymentScreen");
//            transaction.commit();
//        }
    }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (data.getData() != null && requestCode == 456) {
            binding.userImage.setImageURI(data.getData());
            ImageUri = data.getData();
            IsUseImage = true;
        }


    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        assert data != null;
//
//    }




    private String getFileExtensionFromUri(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Get the MIME type of the file
        String mimeType = contentResolver.getType(uri);

        if (mimeType != null) {
            // If the MIME type provides an extension, return it
            return mimeTypeMap.getExtensionFromMimeType(mimeType);
        } else {
            // If the MIME type doesn't provide an extension, try to extract from the Uri's path
            String path = uri.getPath();
            if (path != null) {
                int extensionStartIndex = path.lastIndexOf('.');
                if (extensionStartIndex != -1) {
                    return path.substring(extensionStartIndex + 1);
                }
            }
        }

        // If no extension can be determined, return an empty string or handle it as needed.
        return "";
    }

}