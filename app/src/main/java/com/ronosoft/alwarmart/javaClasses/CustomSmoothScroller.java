package com.ronosoft.alwarmart.javaClasses;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearSmoothScroller;

public class CustomSmoothScroller extends LinearSmoothScroller {

    public CustomSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        // Adjust the scrolling speed as needed
        return 100f / displayMetrics.densityDpi;
    }
}
