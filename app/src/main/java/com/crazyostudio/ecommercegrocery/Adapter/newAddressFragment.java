package com.crazyostudio.ecommercegrocery.Adapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.databinding.FragmentNewAddressBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class newAddressFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    FusedLocationProviderClient fusedLocationProviderClient;
    FragmentNewAddressBinding binding;

    public newAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewAddressBinding.inflate(inflater,container,false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        binding.backButton.setOnClickListener(back->requireActivity().onBackPressed());
        binding.house.setEndIconOnClickListener(currentLocation->{
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, so request it
                requestLocationPermission();
            } else {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(location -> {

                            if (location != null){
                                try {
                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        binding.address.setText("Latitude: "+addresses.get(0).getLatitude());
//                                        binding.house.setText("Longitude: "+addresses.get(0).getLongitude());
                                    binding.address.setText(addresses.get(0).getAddressLine(0));
//                                        bind.setText("City: "+addresses.get(0).getLocality());
//                                        country.setText("Country: "+addresses.get(0).getCountryName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
            }
        });


        binding.save.setOnClickListener(save->{
            binding.progressCircular.setVisibility(View.VISIBLE);
//            userinfoModels.getValue(UserinfoModels.class);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            firebaseDatabase.getReference().child("UserInfo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Data exists, create a UserInfoModel instance and populate it
                        UserinfoModels userInfo = snapshot.getValue(UserinfoModels.class);
                        ArrayList<AddressModel> adderes = new ArrayList<>();
                        AddressModel model = new AddressModel(binding.Name.getText().toString(),binding.Phone.getText().toString(),binding.address.getText().toString());
                        adderes.add(model);
                        if (snapshot.child("address").exists()) {
                            assert userInfo != null;
                            adderes.addAll(userInfo.getAddress());

                        }
                        assert userInfo != null;
                        userInfo.setAddress(adderes);
                        firebaseDatabase.getReference().child("UserInfo").child(FirebaseAuth.getInstance().getUid()).setValue(userInfo).addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                binding.progressCircular.setVisibility(View.INVISIBLE);
                                Objects.requireNonNull(getActivity()).onBackPressed();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        return binding.getRoot();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        // Stop receiving location updates
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }
    private void requestLocationPermission() {
        // Request the location permission from the user
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with location-related tasks

            } else {
                // Permission denied, handle it (e.g., show a message to the user)
            }
        }
    }


    // Other methods and code for your fragment...
}