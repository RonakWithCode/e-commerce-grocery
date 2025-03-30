package com.ronosoft.alwarmart;

import android.app.Application;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Check Google Play Services availability
        if (!checkGooglePlayServices()) {
            Log.e(TAG, "Google Play Services unavailable. Some features may not work.");
            return; // Optionally, proceed with limited functionality
        }

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase", e);
            return; // Stop if Firebase fails
        }

        // Configure Firestore with offline persistence
        try {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.setFirestoreSettings(settings);
            Log.d(TAG, "Firestore configured with offline persistence");
        } catch (Exception e) {
            Log.e(TAG, "Failed to configure Firestore", e);
        }

        // Enable Crashlytics
        try {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
            Log.d(TAG, "Crashlytics enabled");
        } catch (Exception e) {
            Log.e(TAG, "Failed to enable Crashlytics", e);
        }

        // Enable App Check with Play Integrity
        try {
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
            Log.d(TAG, "App Check with Play Integrity initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize App Check with Play Integrity", e);
        }
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Services error: " + resultCode);
            if (googleApi.isUserResolvableError(resultCode)) {
                // Optionally show dialog to update Play Services
                Log.w(TAG, "User can resolve Play Services issue");
            }
            return false;
        }
        Log.d(TAG, "Google Play Services is available");
        return true;
    }
}