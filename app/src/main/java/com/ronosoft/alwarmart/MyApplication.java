package com.ronosoft.alwarmart;

import android.app.Application;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Check Google Play Services availability
        if (!checkGooglePlayServices()) {
            return; // Optionally, proceed with limited functionality
        }

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            return; // Stop if Firebase fails
        }

        // Configure Firestore with offline persistence
        try {
            FirebaseFirestore.setLoggingEnabled(false);
            FirebaseDatabase.getInstance().setLogLevel(com.google.firebase.database.Logger.Level.DEBUG);
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.setFirestoreSettings(settings);
        } catch (Exception e) {
            // Silent fail, Crashlytics will catch if critical
        }

        // Enable Crashlytics
        try {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        } catch (Exception e) {
            // Silent fail
        }

        // Enable App Check with Play Integrity
        try {
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
        } catch (Exception e) {
            // Silent fail
        }
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        return resultCode == ConnectionResult.SUCCESS;
    }
}