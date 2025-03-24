package com.ronosoft.alwarmart.javaClasses;

import android.content.Context;
import android.content.SharedPreferences;

public class DeliveryManagement {
    public int getSavedDeliveryTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("delivery_prefs", Context.MODE_PRIVATE);
        // Returns the saved delivery time or a default of 10 minutes if not set.
        return prefs.getInt("delivery_time", 10);
    }
}
