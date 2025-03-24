package com.ronosoft.alwarmart.javaClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.ronosoft.alwarmart.Model.AddressModel;

public class AddressDeliveryService {

    // Example warehouse location (adjust as needed)
    private static final LatLng WAREHOUSE_LOCATION = new LatLng(27.528968, 76.604573);

    /**
     * Calculate delivery time in minutes (base 10 min + 2 min per km)
     */
    public int calculateDeliveryTime(AddressModel address) {
        if (address == null) return 10;
        float[] distance = new float[1];
        Location.distanceBetween(
                WAREHOUSE_LOCATION.latitude, WAREHOUSE_LOCATION.longitude,
                address.getLatitude(), address.getLongitude(),
                distance);
        float distanceKm = distance[0] / 1000f;
        return (int) Math.max(10, distanceKm * 2);
    }

    /**
     * Saves the provided default address in SharedPreferences.
     */
    public void saveDefaultAddress(Context context, AddressModel address) {
        if (address == null) return;
        SharedPreferences prefs = context.getSharedPreferences("default_address", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fullName", address.getFullName());
        editor.putString("mobileNumber", address.getMobileNumber());
        editor.putString("flatHouse", address.getFlatHouse());
        editor.putString("address", address.getAddress());
        editor.putString("landmark", address.getLandmark());
        editor.putBoolean("isHomeSelected", address.isHomeSelected());
        editor.putFloat("latitude", (float) address.getLatitude());
        editor.putFloat("longitude", (float) address.getLongitude());
        editor.apply();
    }

    /**
     * Retrieves the default address from SharedPreferences.
     */
    public AddressModel getDefaultAddress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("default_address", Context.MODE_PRIVATE);
        if (!prefs.contains("latitude") || !prefs.contains("longitude")) {
            return null;
        }
        String fullName = prefs.getString("fullName", "");
        String mobileNumber = prefs.getString("mobileNumber", "");
        String flatHouse = prefs.getString("flatHouse", "");
        String address = prefs.getString("address", "");
        String landmark = prefs.getString("landmark", "");
        boolean isHomeSelected = prefs.getBoolean("isHomeSelected", true);
        float lat = prefs.getFloat("latitude", 0f);
        float lng = prefs.getFloat("longitude", 0f);
        return new AddressModel(fullName, mobileNumber, flatHouse, address, landmark, isHomeSelected, lat, lng);
    }

    /**
     * Clears the default address.
     */
    public void clearDefaultAddress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("default_address", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
