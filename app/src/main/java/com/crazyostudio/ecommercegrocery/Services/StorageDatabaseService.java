package com.crazyostudio.ecommercegrocery.Services;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class StorageDatabaseService {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public interface UploadImageCallback{
        void onSuccess(Uri Images);

        void onError(Exception errorMessage);
    }

   public void UploadImage(String UidPath,String child , Uri image,UploadImageCallback callback){
        StorageReference storageRef = storage.getReference(UidPath);
        StorageReference imageRef = storageRef.child(child);
        imageRef.putFile(image)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl()
                        .addOnSuccessListener(callback::onSuccess)
                        .addOnFailureListener(callback::onError))
                .addOnFailureListener(callback::onError);
    }


}
