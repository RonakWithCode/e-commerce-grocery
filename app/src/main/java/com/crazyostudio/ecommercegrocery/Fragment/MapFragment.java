package com.crazyostudio.ecommercegrocery.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.crazyostudio.ecommercegrocery.databinding.FragmentMapBinding;
import com.mappls.sdk.maps.MapView;
import com.mappls.sdk.maps.Mappls;
import com.mappls.sdk.maps.MapplsMap;
import com.mappls.sdk.maps.OnMapReadyCallback;
import com.mappls.sdk.maps.annotations.MarkerOptions;
import com.mappls.sdk.maps.camera.CameraUpdateFactory;
import com.mappls.sdk.maps.geometry.LatLng;
import com.mappls.sdk.services.account.MapplsAccountManager;


public class MapFragment extends Fragment {
    private MapView mapView;
    private MapplsMap mapplsMap;
    private LatLng warehouseLocation = new LatLng(27.5647, 76.6142); // Approximate coordinates for Alwar, adjust as needed for exact location
    private double deliveryRadius = 30000; // 30 km expressed in meters


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
        FragmentMapBinding binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapplsMap map) {
                mapplsMap = map;
                mapplsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warehouseLocation, 10)); // Adjust zoom level as needed

//                drawDeliveryAreaCircle(warehouseLocation, deliveryRadius);

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

//    private void drawDeliveryAreaCircle(LatLng center, double radius) {
//        CircleOptions circleOptions = new CircleOptions()
//                .center(center)
//                .radius(radius)
//                .strokeColor(Color.RED)   // Color of the border of the circle
//                .fillColor(0x30ff0000)    // Color of the fill of the circle
//                .strokeWidth(2.0f);       // Width of the border
//        mapplsMap.addCircle(circleOptions);


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
}
