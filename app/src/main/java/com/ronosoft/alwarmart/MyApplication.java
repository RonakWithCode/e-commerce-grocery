package com.ronosoft.alwarmart;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        // Try Play Integrity first
//        firebaseAppCheck.installAppCheckProviderFactory(
//            PlayIntegrityAppCheckProviderFactory.getInstance()
//        );
        // Fallback to SafetyNet if Play Integrity fails
//        firebaseAppCheck.installAppCheckProviderFactory(
//            SafetyNetAppCheckProviderFactory.getInstance()
//        );
    }
} 