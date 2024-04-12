package com.crazyostudio.ecommercegrocery.Adapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.databinding.FragmentNewAddressBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
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
        binding = FragmentNewAddressBinding.inflate(inflater, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        binding.backButton.setOnClickListener(back -> requireActivity().onBackPressed());
        binding.house.setEndIconOnClickListener(currentLocation -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                requestLocationPermission();
            }else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        binding.address.setText("Latitude: "+addresses.get(0).getLatitude());
//                                        binding.house.setText("Longitude: "+addresses.get(0).getLongitude());
                                Log.i("addressesError", "onCreateView: "+ addresses.get(0).getAddressLine(0));
                                Log.i("addressesError", "addresses: "+ addresses);
                                binding.address.setText(addresses.get(0).getAddressLine(0));
//                                        bind.setText("City: "+addresses.get(0).getLocality());
//                                        country.setText("Country: "+addresses.get(0).getCountryName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }

                    });

        }});


        binding.save.setOnClickListener(save -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            String fullName = binding.Name.getText().toString();
            String mobileNumber = binding.Phone.getText().toString();
            String flatHouse = binding.flatHouse.getText().toString();
            String address = binding.address.getText().toString();
            String landmark = binding.landmark.getText().toString();
            boolean isHomeSelected = binding.home.isChecked();

            if (fullName.isEmpty()) {
                binding.Name.setError("Full name is required");
            } else if (mobileNumber.isEmpty()) {
                binding.Phone.setError("Mobile number is required");
            } else if (flatHouse.isEmpty()) {
                binding.flatHouse.setError("Flat/house number is required");
            } else if (address.isEmpty()) {
                binding.address.setError("Address is required");
            } else if (landmark.isEmpty()) {
                binding.landmark.setError("Landmark is required");
            }else {
                AddressModel addressModel = new AddressModel(fullName, mobileNumber, flatHouse, address, landmark, isHomeSelected);
                new DatabaseService().setAdders(addressModel, FirebaseAuth.getInstance().getUid(), new DatabaseService.SetAddersCallback() {
                @Override
                public void onSuccess() {
                    binding.hintView.setError("errorMessage");
                    binding.progressCircular.setVisibility(View.INVISIBLE);
//                    requireActivity().onBackPressed();
                }

                @Override
                public void onError(String errorMessage) {
                    binding.hintView.setError(errorMessage);
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    Toast.makeText(requireContext(), "Fulled to save  Address error " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
            }
        });

        return binding.getRoot();
    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestLocationPermission();

        }else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {

                        if (location != null) {
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
                getLocation();
            } else {
                // Permission denied, handle it (e.g., show a message to the user)
            }
        }
    }


    // Other methods and code for your fragment...
}