package com.crazyostudio.ecommercegrocery.javaClasses;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.crazyostudio.ecommercegrocery.MainActivity;
import com.crazyostudio.ecommercegrocery.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Check if the preference is set to true (show notifications)
        SharedPreferences sharedPreferences = getSharedPreferences("account_shipping_notification", Context.MODE_PRIVATE);
        boolean shouldShowNotification = sharedPreferences.getBoolean("account_shipping_notificationBool", false);

        if (shouldShowNotification) {
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();

            // Create an intent to open an activity when the notification is clicked
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build the notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Shipping")
                    .setSmallIcon(R.drawable.ic_baseline_delete_24)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            // Get the NotificationManager
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Show the notification
            if (notificationManager != null) {
                notificationManager.notify(0, notificationBuilder.build());
            }
        }
    }
}