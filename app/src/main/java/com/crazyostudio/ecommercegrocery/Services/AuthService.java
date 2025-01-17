package com.crazyostudio.ecommercegrocery.Services;

import android.net.Uri;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthService {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public boolean IsLogin() {
        return auth.getCurrentUser() != null;
    }

    public String getUserId() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    public String getUserName() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getDisplayName() : null;
    }

    public String getUserUrl() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null && currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
    }

    public String getUserPhoneNumber() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getPhoneNumber() : null;
    }

    public String getUserEmail() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getEmail() : null;
    }

    public void setUserUrl(String url) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(url))
                    .build();
            currentUser.updateProfile(profileUpdates);
        }
    }

    public interface UpdateNameListenerCallback {
        void failureListener(Exception e);
        void Success();
    }

    public void updateName(String name, UpdateNameListenerCallback callback) {
        String userId = getUserId();
        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("UserInfo").document(userId);
            userRef.update("username", name)
                    .addOnSuccessListener(aVoid -> {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> callback.Success()).addOnFailureListener(callback::failureListener);
                        }
                    })
                    .addOnFailureListener(callback::failureListener);
        } else {
            callback.failureListener(new NullPointerException("User ID is null"));
        }
    }

    public interface LinkEmailCallback {
        void onSuccess();
        void onError(Exception errorMessage);
    }

    public void linkEmailCredential(String name, String image, String email, String password, LinkEmailCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            AuthCredential emailCredential = EmailAuthProvider.getCredential(email, password);
            currentUser.linkWithCredential(emailCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(image))
                                    .setDisplayName(name).build();
                            currentUser.updateProfile(profileUpdates);
                            sendEmailVerification(callback);
                        } else {
                            callback.onError(task.getException());
                        }
                    });
        } else {
            callback.onError(new NullPointerException("Current user is null"));
        }
    }

    public void sendEmailVerification(LinkEmailCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(task.getException());
                        }
                    });
        } else {
            callback.onError(new NullPointerException("Current user is null"));
        }
    }

    public boolean checkEmailVerificationStatus() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null && currentUser.isEmailVerified();
    }
}
