package com.ronosoft.alwarmart.utils;

import android.os.Build;
import java.time.LocalTime;
import java.util.Calendar;

public class TimeUtils {

    /**
     * Returns a greeting based on the current time.
     * Uses java.time for API 26+; otherwise falls back to Calendar.
     *
     * @return A greeting string ("Good Morning", "Good Afternoon", etc.)
     */
    public static String getTimeBasedGreeting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime now = LocalTime.now();
            int hour = now.getHour();
            if (hour < 12) {
                return "Good Morning";
            } else if (hour < 16) {
                return "Good Afternoon";
            } else if (hour < 21) {
                return "Good Evening";
            } else {
                return "Good Night";
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour < 12) {
                return "Good Morning";
            } else if (hour < 16) {
                return "Good Afternoon";
            } else if (hour < 21) {
                return "Good Evening";
            } else {
                return "Good Night";
            }
        }
    }
}
