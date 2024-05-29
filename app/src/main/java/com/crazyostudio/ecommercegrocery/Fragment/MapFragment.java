package com.crazyostudio.ecommercegrocery.Fragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.FragmentMapBinding;
import com.mappls.sdk.geojson.Point;
import com.mappls.sdk.maps.MapView;
import com.mappls.sdk.maps.Mappls;
import com.mappls.sdk.maps.MapplsMap;
import com.mappls.sdk.maps.OnMapReadyCallback;
import com.mappls.sdk.maps.annotations.MarkerOptions;
import com.mappls.sdk.maps.camera.CameraUpdateFactory;
import com.mappls.sdk.maps.geometry.LatLng;
import com.mappls.sdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mappls.sdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mappls.sdk.plugins.places.autocomplete.ui.PlaceSelectionListener;
import com.mappls.sdk.services.account.MapplsAccountManager;
import com.mappls.sdk.services.api.autosuggest.AutoSuggestCriteria;
import com.mappls.sdk.services.api.autosuggest.model.ELocation;


public class MapFragment extends Fragment {
    private MapView mapView;
    private MapplsMap mapplsMap;

    LatLng warehouseLocation = new LatLng(27.5647, 76.6142); // Approximate coordinates for Alwar, adjust as needed for exact location
    double deliveryRadius = 30000; // 30 km expressed in meters
    FragmentMapBinding binding;

//    public void onCreate() {

//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mappls.getInstance(requireActivity());

        MapplsAccountManager.getInstance().setRestAPIKey("92c446c1026304f56fa21f15dcd41be6");
        MapplsAccountManager.getInstance().setMapSDKKey("92c446c1026304f56fa21f15dcd41be6");
        MapplsAccountManager.getInstance().setAtlasClientId("96dHZVzsAutYANkXdbNOhft5GgIBb84qcNsmoL0wucbtkBvFZVmYcduw90wbq8Ybrd-FylNUuGdF7_Hu-RZ_QQ==");
        MapplsAccountManager.getInstance().setAtlasClientSecret("lrFxI-iSEg_W-MqggUNqeFQVLm7iscYka3-W30kqywKTpU4aFnj7i9vhZ4Rzxi7kivB3KwMG0ywzhTUh9WR6kd1QSAP0_BDN");
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        viewSearch();


//        binding.searchBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewSearch();
//            }
//        });
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapplsMap map) {
                mapplsMap = map;
                mapplsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warehouseLocation, 10)); // Adjust zoom level as needed
                mapplsMap.addOnMapClickListener(latLng -> {
                    if (isWithinDeliveryRadius(latLng)) {
                        mapplsMap.clear();
//                        drawDeliveryAreaCircle(warehouseLocation, deliveryRadius);
                        mapplsMap.addMarker(new MarkerOptions().position(latLng));
                        // Proceed with delivery process if needed
                        return true;
                    } else {
                        // Notify user they are out of the delivery area
                        Toast.makeText(getContext(), "Selected location is out of delivery area.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
            }

            @Override
            public void onMapError(int i, String s) {
                // Handle map initialization errors
            }
        });
        return view;
    }

    private boolean isWithinDeliveryRadius(LatLng latLng) {
        float[] results = new float[1];
        Location.distanceBetween(warehouseLocation.getLatitude(), warehouseLocation.getLongitude(),
                latLng.getLatitude(), latLng.getLongitude(), results);
        return results[0] < deliveryRadius;
    }


    // Include lifecycle methods as earlier...

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }




//    void viewSearch(){
//        Dialog MapSheetDialog =  new Dialog(requireActivity());
//        MapdialogsearchBinding ViewDialogBinding = MapdialogsearchBinding.inflate(getLayoutInflater());
//
//
//        MapSheetDialog.setContentView(ViewDialogBinding.getRoot());
//
//        MapSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        MapSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        MapSheetDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_dialogAnimation;
//        MapSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//
//        MapSheetDialog.show();
//
//        ViewDialogBinding.closeButton.setOnClickListener(v -> MapSheetDialog.dismiss());
//
//        PlaceAutocompleteFragment placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance();
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .add(ViewDialogBinding.autocompleteFragmentContainer.getId(), placeAutocompleteFragment, "PlaceAutocompleteFragmentTag")
//
//                .commit();
//        placeAutocompleteFragment.setSuggestedSearchSelectionListener(new SuggestedSearchSelectionListener() {
//            @Override
//            public void onSuggestedSearchSelected(SuggestedSearchAtlas suggestedSearchAtlas) {
//                Log.i("MAPMapdialogsearchBinding", "onSuggestedSearchSelected: "+suggestedSearchAtlas.getLocation());
//                Log.i("MAPMapdialogsearchBinding", "onSuggestedSearchSelected--: "+suggestedSearchAtlas.getKeyword());
//
//            }
//        });
//        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(ELocation eLocation) {
//                LatLng check = new LatLng(eLocation.latitude, eLocation.longitude); // Approximate coordinates for Alwar, adjust as needed for exact location
//                if (isWithinDeliveryRadius(check)) {
//                    mapplsMap.clear();
////                        drawDeliveryAreaCircle(warehouseLocation, deliveryRadius);
//                    mapplsMap.addMarker(new MarkerOptions().position(check));
//                    binding.tvAddressTitle.setText(eLocation.placeAddress);
//                    MapSheetDialog.dismiss();
//                    // Proceed with delivery process if needed
//                } else {
//                    // Notify user they are out of the delivery area
//                    Toast.makeText(getContext(), "Selected location is out of delivery area.", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
//
//
//
//
//
//    }

    void viewSearch() {
        // Hide the map view and show the search interface
        binding.closeButton.setOnClickListener(v -> {
            binding.MapViewr.setVisibility(View.VISIBLE);
            binding.ViewSerach.setVisibility(View.GONE);

        });
        binding.MapViewr.setVisibility(View.GONE);
        binding.ViewSerach.setVisibility(View.VISIBLE);
        Point point = Point.fromLngLat(76.6142,27.56477);
        PlaceOptions placeOptions = PlaceOptions.builder()
                // Coordinates for Alwar
                .location(point)
                .filter("bounds: 27.5500, 76.6000; 27.5800, 76.6300") // Bounded area for search
                .historyCount(3) // Maximum number of history results
                .zoom(14.0) // Zoom level
                .saveHistory(true) // Save search history
                .pod(AutoSuggestCriteria.POD_CITY) // Limit results to cities
                .tokenizeAddress(true) // Structured address response
                .backgroundColor(Color.WHITE) // Background color of the widget
                .toolbarColor(Color.BLUE) // Toolbar color
                .hint("Search for a new area, locality...") // Hint text in search view
                .attributionHorizontalAlignment(PlaceOptions.GRAVITY_BOTTOM) // Attribution horizontal alignment
                .attributionVerticalAlignment(PlaceOptions.GRAVITY_RIGHT) // Attribution vertical alignment
                .logoSize(PlaceOptions.SIZE_MEDIUM) // Size of the logo
                .bridge(true) // Enable bridge for nearby API searches
                .build();

        // Instantiate and add the PlaceAutocompleteFragment
        PlaceAutocompleteFragment autocompleteFragment = PlaceAutocompleteFragment.newInstance(placeOptions);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, autocompleteFragment)
                .commit();

        // Set up the listener for place selection
//        Point alwarLocation = Point.fromLngLat(76.6142, 27.5647);

//        new LatLng(76.6142,27.56477)


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(ELocation eLocation) {
                if (eLocation != null && eLocation.latitude != null && eLocation.longitude != null) {
                    LatLng selectedLocation = new LatLng(eLocation.latitude, eLocation.longitude);

                    // Show the map view and hide the search interface
                    binding.MapViewr.setVisibility(View.VISIBLE);
                    binding.ViewSerach.setVisibility(View.GONE);

                    // Move the map to the selected location and add a marker
                    mapplsMap.clear();
                    mapplsMap.addMarker(new MarkerOptions().position(selectedLocation));
                    mapplsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 14));

                    // Update the address title and subtitle if the fragment is still added to its activity
                    if (isAdded()) {
                        binding.tvAddressTitle.setText(eLocation.placeAddress);
                        binding.tvAddressSubtitle.setText(eLocation.placeName);
                    }
                }
                else {
                    // This block should be outside the main if statement
                    Toast.makeText(getContext(), "Invalid location data received.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {
                // Optional: handle cancellation if necessary
            }
        });
    }








//    void viewSearch() {
//        Dialog MapSheetDialog = new Dialog(requireActivity());
//        MapdialogsearchBinding ViewDialogBinding = MapdialogsearchBinding.inflate(getLayoutInflater());
//
//        MapSheetDialog.setContentView(ViewDialogBinding.getRoot());
//        MapSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        MapSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////        MapSheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        MapSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//        MapSheetDialog.show();
//
//        ViewDialogBinding.closeButton.setOnClickListener(v -> MapSheetDialog.dismiss());
//
//        PlaceAutocompleteFragment placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance();
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .add(MapSheetDialog.findViewById(R.id.autocomplete_fragment_container), placeAutocompleteFragment, "PlaceAutocompleteFragmentTag")
//                .commit();
//
//        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(ELocation eLocation) {
//                LatLng check = new LatLng(eLocation.latitude, eLocation.longitude); // Approximate coordinates for Alwar, adjust as needed for exact location
//                if (isWithinDeliveryRadius(check)) {
//                    mapplsMap.clear();
////                        drawDeliveryAreaCircle(warehouseLocation, deliveryRadius);
//                    mapplsMap.addMarker(new MarkerOptions().position(check));
//                    binding.tvAddressTitle.setText(eLocation.placeAddress);
//                    MapSheetDialog.dismiss();
//                    // Proceed with delivery process if needed
//                } else {
//                    // Notify user they are out of the delivery area
//                    Toast.makeText(getContext(), "Selected location is out of delivery area.", Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
//    }


}
