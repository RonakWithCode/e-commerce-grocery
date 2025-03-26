package com.ronosoft.alwarmart;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // Check if Firebase is already initialized.
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
//                Log.d(TAG, "FirebaseApp initialized successfully.");
            } else {
//                Log.d(TAG, "FirebaseApp already initialized.");
            }
        } catch (Exception e) {
//            Log.e(TAG, "Error initializing FirebaseApp", e);
        }

        // Optionally, if you want to enable Firebase App Check with error handling,
        // you can uncomment and adjust the code below.
        /*
        try {
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            // Try using Play Integrity as the primary provider.
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
            );
            Log.d(TAG, "Firebase AppCheck initialized using Play Integrity.");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase AppCheck with Play Integrity, falling back to SafetyNet", e);
            try {
                firebaseAppCheck.installAppCheckProviderFactory(
                        SafetyNetAppCheckProviderFactory.getInstance()
                );
                Log.d(TAG, "Firebase AppCheck initialized using SafetyNet.");
            } catch (Exception ex) {
                Log.e(TAG, "Error initializing Firebase AppCheck with SafetyNet", ex);
            }
        }
        */
    }
}
