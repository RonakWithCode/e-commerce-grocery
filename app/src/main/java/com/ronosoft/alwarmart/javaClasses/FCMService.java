package com.ronosoft.alwarmart.javaClasses;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ronosoft.alwarmart.MainActivity;
import com.ronosoft.alwarmart.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    @SuppressLint("MissingPermission")
    @Override
    public void onMessageReceived( RemoteMessage remoteMessage) {
        // Check if the preference is set to true (show notifications)
        super.onMessageReceived(remoteMessage);
        SharedPreferences sharedPreferences = getSharedPreferences("account_shipping_notification", Context.MODE_PRIVATE);
        boolean shouldShowNotification = sharedPreferences.getBoolean("account_shipping_notificationBool", true);

        if (shouldShowNotification) {
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();
            String CHANNEL_ID = "com.ronosoft.alwarmart" + "Shipping";
            Log.d("CHANNEL_ID_name", "onMessageReceived: "+CHANNEL_ID);
            // Create an intent to open an activity when the notification is clicked
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Shipping Notification", importance);
//                channel.setDescription("Shipping Notification");

                channel.enableVibration(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                // Register the channel with the system.
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }


            // Build the notification with a unique ID for each notification
            int notificationId = (int) System.currentTimeMillis();
            Notification notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.adduser)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(notificationTitle).bigText(notificationBody))
                    .setAutoCancel(true).build();
            NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder);
            // Show the notification



        }

    }
}