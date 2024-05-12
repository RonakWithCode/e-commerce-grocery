package com.crazyostudio.ecommercegrocery.javaClasses;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class MyNotificationListenerService extends NotificationListenerService {

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        // Your service is now connected and can access notifications.

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Handle new notifications here
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Handle removed notifications here
    }
}
