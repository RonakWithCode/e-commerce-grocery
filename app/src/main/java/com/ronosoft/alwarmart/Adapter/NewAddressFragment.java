package com.ronosoft.alwarmart.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.R;
import com.ronosoft.alwarmart.Services.DatabaseService;
import com.ronosoft.alwarmart.databinding.FragmentNewAddressBinding;
import com.ronosoft.alwarmart.javaClasses.LoadingDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewAddressFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    // Updated warehouse location for Alwar, Rajasthan (from Google Maps)
    private static final LatLng WAREHOUSE_LOCATION = new LatLng(27.528968, 76.604573);
    // Set new delivery radius (in km) for Alwar area – adjust as needed.
    private static final float DELIVERY_RADIUS_KM = 20f;

    private FragmentNewAddressBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LoadingDialog loadingDialog;
    private Circle deliveryRadiusCircle;
    private Marker selectedLocationMarker;
    private LatLng selectedLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewAddressBinding.inflate(inflater, container, false);
        initializeComponents();
        setupLocationCallback();
        setupClickListeners();
        return binding.getRoot();
    }

    private void initializeComponents() {
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(requireContext());
        loadingDialog = new LoadingDialog(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    handleLocationUpdate(currentLatLng);
                }
            }
        };
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Start location updates when the end icon (location icon) is tapped.
        binding.house.setEndIconOnClickListener(v -> {
            if (checkLocationPermission()) {
                startLocationUpdates();
            } else {
                requestLocationPermission();
            }
        });

        binding.save.setOnClickListener(v -> saveAddress());
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        loadingDialog.startLoadingDialog();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);

        if (checkLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            handleLocationUpdate(currentLatLng);
                        }
                        loadingDialog.dismissDialog();
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismissDialog();
                        Toast.makeText(requireContext(),
                                getString(R.string.failed_to_get_location) + ": " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void handleLocationUpdate(LatLng location) {
        if (isLocationInDeliveryArea(location)) {
            updateSelectedLocation(location);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.out_of_delivery_area),
                    Toast.LENGTH_SHORT).show();
            // Optionally, recenter the map to the warehouse location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(WAREHOUSE_LOCATION, 12f));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();

        // Allow the user to update location by tapping on the map.
        mMap.setOnMapClickListener(this::handleMapClick);
    }

    private void setupMap() {
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Draw the delivery area circle based on the new radius.
        deliveryRadiusCircle = mMap.addCircle(new CircleOptions()
                .center(WAREHOUSE_LOCATION)
                .radius(DELIVERY_RADIUS_KM * 1000) // converting km to meters
                .strokeColor(Color.GREEN)
                .strokeWidth(2f)
                .fillColor(Color.argb(70, 0, 255, 0)));

        // Move the camera to the warehouse location.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WAREHOUSE_LOCATION, 12f));
    }

    private void handleMapClick(LatLng latLng) {
        if (isLocationInDeliveryArea(latLng)) {
            updateSelectedLocation(latLng);
        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.out_of_delivery_area),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Check if the selected location is within the allowed delivery area (Alwar)
    private boolean isLocationInDeliveryArea(LatLng location) {
        float[] distance = new float[1];
        Location.distanceBetween(
                WAREHOUSE_LOCATION.latitude, WAREHOUSE_LOCATION.longitude,
                location.latitude, location.longitude,
                distance
        );
        return distance[0] <= DELIVERY_RADIUS_KM * 1000;
    }

    // Update marker, address field, and calculate delivery time
    private void updateSelectedLocation(LatLng location) {
        selectedLocation = location;

        if (selectedLocationMarker != null) {
            selectedLocationMarker.remove();
        }

        selectedLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(getString(R.string.delivery_location)));

        updateAddressFromLocation(location);
        updateAndSaveDeliveryTime(location);
    }

    private void updateAddressFromLocation(LatLng location) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                binding.address.setText(fullAddress);
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    getString(R.string.could_not_get_address),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Calculate estimated delivery time based on distance and save in SharedPreferences
    private void updateAndSaveDeliveryTime(LatLng location) {
        float[] distance = new float[1];
        Location.distanceBetween(
                WAREHOUSE_LOCATION.latitude, WAREHOUSE_LOCATION.longitude,
                location.latitude, location.longitude,
                distance
        );
        // Convert distance from meters to kilometers
        float distanceKm = distance[0] / 1000;
        // Simple formula: minimum 10 minutes plus 2 minutes per km
        int estimatedTime = (int) Math.max(10, distanceKm * 2);

        SharedPreferences prefs = requireContext().getSharedPreferences("delivery_prefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("delivery_time", estimatedTime).apply();

        // Optionally, update a TextView in the UI with the delivery time.
    }

    private void saveAddress() {
        if (!validateInputs()) {
            return;
        }

        AddressModel addressModel = new AddressModel(
                binding.Name.getText().toString(),
                binding.Phone.getText().toString(),
                binding.flatHouse.getText().toString(),
                binding.address.getText().toString(),
                binding.landmark.getText().toString(),
                binding.home.isChecked(),
                selectedLocation.latitude,
                selectedLocation.longitude
        );

        saveAddressToDatabase(addressModel);
    }

    private boolean validateInputs() {
        if (selectedLocation == null) {
            Toast.makeText(requireContext(),
                    getString(R.string.select_delivery_location),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.Name.getText().toString().isEmpty()) {
            binding.Name.setError(getString(R.string.full_name_required));
            return false;
        }
        if (binding.Phone.getText().toString().isEmpty()) {
            binding.Phone.setError(getString(R.string.mobile_number_required));
            return false;
        }
        if (binding.flatHouse.getText().toString().isEmpty()) {
            binding.flatHouse.setError(getString(R.string.flat_house_required));
            return false;
        }
        if (binding.address.getText().toString().isEmpty()) {
            binding.address.setError(getString(R.string.address_required));
            return false;
        }
        if (binding.landmark.getText().toString().isEmpty()) {
            binding.landmark.setError(getString(R.string.landmark_required));
            return false;
        }
        return true;
    }

    private void saveAddressToDatabase(AddressModel addressModel) {
        loadingDialog.startLoadingDialog();
        new DatabaseService().setAdders(
                addressModel,
                FirebaseAuth.getInstance().getUid(),
                new DatabaseService.SetAddersCallback() {
                    @Override
                    public void onSuccess() {
                        loadingDialog.dismissDialog();
                        Toast.makeText(requireContext(),
                                getString(R.string.address_saved_successfully),
                                Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(requireContext(),
                                getString(R.string.failed_to_save_address) + ": " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null && checkLocationPermission()) {
                    mMap.setMyLocationEnabled(true);
                    startLocationUpdates();
                }
            } else {
                Toast.makeText(requireContext(),
                        getString(R.string.location_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
