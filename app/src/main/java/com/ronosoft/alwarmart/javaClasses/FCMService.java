package com.ronosoft.alwarmart.javaClasses;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ronosoft.alwarmart.MainActivity;
import com.ronosoft.alwarmart.R;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String PREFS_NAME = "account_shipping_notification";
    private static final String PREF_NOTIFICATION_BOOL = "account_shipping_notificationBool";
    private static final String CHANNEL_ID = "com.ronosoft.alwarmart.Shipping";
    private static final String CHANNEL_NAME = "Shipping Notification";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // Save token locally using TokenManager.
        TokenManager tokenManager = TokenManager.getInstance(getApplicationContext());
        boolean saved = tokenManager.saveToken(token);
        if (saved) {
            Log.d(TAG, "Token saved locally.");
        } else {
            Log.e(TAG, "Failed to save token locally.");
        }

        // Update token in Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            DocumentReference userRef = db.collection("UserInfo").document(mAuth.getUid());
            userRef.update("token", token)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Token updated in Firestore successfully."))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update token in Firestore", e);
                        Toast.makeText(getApplicationContext(), "Failed to update token", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.w(TAG, "No authenticated user found; token not updated in Firestore.");
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean shouldShowNotification = sharedPreferences.getBoolean(PREF_NOTIFICATION_BOOL, true);
        if (!shouldShowNotification) {
            Log.d(TAG, "Notifications are disabled by user preferences.");
            return;
        }

        Map<String, String> data = remoteMessage.getData();
        if (data == null || data.isEmpty()) {
            Log.d(TAG, "No data payload found in the message.");
            return;
        }

        displayOrderStatusNotification(data);
    }

    /**
     * Displays order status notifications using data payload.
     */
    private void displayOrderStatusNotification(Map<String, String> data) {
        String orderId = data.get("orderId");
        String loadID = data.get("LoadID");
        String newStatus = data.get("newStatus");
        String orderItemCount = data.get("orderItemCount");
        String message = data.get("message");
        String orderStatus = data.get("orderStatus");

        String notificationTitle = (newStatus != null && orderItemCount != null)
                ? newStatus + " ORDER OF " + orderItemCount + " ITEM(s)"
                : "Order Update";
        String notificationBody = (message != null && orderStatus != null)
                ? message + ". Status: " + orderStatus
                : "Your order status has been updated.";

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification_type", "order_status");
        if (orderId != null) {
            intent.putExtra("orderId", orderId);
        }
        if (loadID != null) {
            intent.putExtra("LoadID", loadID);
        }

        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, pendingIntentFlags);

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_green)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(notificationTitle)
                        .bigText(notificationBody))
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted; cannot display notification.");
                return;
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
        Log.d(TAG, "Order status notification displayed with ID: " + notificationId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
                if (channel == null) {
                    channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    channel.enableVibration(true);
                    channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                    notificationManager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created: " + CHANNEL_ID);
                }
            } else {
                Log.w(TAG, "NotificationManager is null; cannot create notification channel.");
            }
        }
    }
}
