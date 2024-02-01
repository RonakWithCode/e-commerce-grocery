package com.crazyostudio.ecommercegrocery.Services;

import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class AuthService {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public boolean IsLogin(){
        return auth.getCurrentUser() == null;
    }

    public String getUserId(){
        return auth.getUid();
    }
    public String getUserName(){
        return auth.getCurrentUser().getDisplayName();
    }
    public String getUserUrl(){
        return Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).toString();
    }
    public String getUserPhoneNumber(){
        return auth.getCurrentUser().getPhoneNumber();
    }
    public String getUserEmail(){
        return auth.getCurrentUser().getEmail();
    }



    public void setUserUrl(String url){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();
        Objects.requireNonNull(auth.getCurrentUser()).updateProfile(profileUpdates);

    }





    // ...

    // Step 4: Implement the method to link the email credential

    public interface LinkEmailCallback{
        void onSuccess();

        void onError(Exception errorMessage);
    }
    public void linkEmailCredential(String name,String image ,String email, String password , LinkEmailCallback callback) {
        if (user != null) {
            AuthCredential emailCredential = EmailAuthProvider.getCredential(email, password);
            user.linkWithCredential(emailCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email credential successfully linked
//                            Log.d("TAG", "Email credential linked.");
                            // Step 5: Send email verification
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(image))
                                    .setDisplayName(name).build();
                            user.updateProfile(profileUpdates);
                            sendEmailVerification(callback);
                        } else {
                            // If the link fails, display a message to the user.
                            callback.onError(task.getException());
//                            Log.w("TAG", "Linking email credential failed", task.getException());
                        }
                    });
        }
    }

    // Step 6: Implement the method to send email verification
    public void sendEmailVerification(LinkEmailCallback callback) {

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email verification sent
//                            Log.d("TAG", "Email verification sent.");
                            callback.onSuccess();
                            // You can add your own logic here, such as showing a message to the user.
                        } else {
                            // If the email verification fails, display a message to the user.
//                            Log.w("TAG", "Email verification failed to send", task.getException());
                            callback.onError(task.getException());
                        }
                    });
        }
    }

    public boolean checkEmailVerificationStatus() {
        return user.isEmailVerified();
    }





}
