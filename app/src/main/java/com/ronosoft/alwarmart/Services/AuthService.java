package com.ronosoft.alwarmart.Services;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ronosoft.alwarmart.DAO.CartDAO;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.UserinfoModels;
import com.ronosoft.alwarmart.javaClasses.AddressDeliveryService;
import com.ronosoft.alwarmart.javaClasses.TokenManager;

import java.util.Objects;


public class AuthService {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

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

    public Task<String> getUserPhoneNumber() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }

        return currentUser.getIdToken(true)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    GetTokenResult tokenResult = task.getResult();
                    if (tokenResult == null) {
                        throw new Exception("Token claims not available");
                    }

                    Object phoneObj = tokenResult.getClaims().get("phone_number");
                    if (phoneObj != null) {
                        return phoneObj.toString();
                    }

                    // Fallback to user profile phone number
                    String profilePhone = currentUser.getPhoneNumber();
                    if (profilePhone != null) {
                        return profilePhone;
                    }

                    // If no phone number found, get it from DatabaseService
                    return Tasks.call(() -> {
                        TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

                        new DatabaseService().getUserInfo(getUserId(), new DatabaseService.getUserInfoCallback() {
                            @Override
                            public void onSuccess(UserinfoModels user) {
                                String phone = user.getPhoneNumber();
                                if (phone != null) {
                                    tcs.setResult(phone);
                                } else {
                                    tcs.setException(new Exception("Phone number not found in database"));
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                tcs.setException(new Exception(errorMessage));
                            }
                        });

                        return Tasks.await(tcs.getTask());
                    }).getResult();
                });
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



    public void signOut(Context context){
        TokenManager.getInstance(context).clearToken();
        new AddressDeliveryService().clearDefaultAddress(context);
        new ProductManager(context).removeCartProducts();

        auth.signOut();
    }
    public void ClearData(Context context){
        TokenManager.getInstance(context).clearToken();
        new AddressDeliveryService().clearDefaultAddress(context);
        new ProductManager(context).removeCartProducts();

//        auth.signOut();
    }




}