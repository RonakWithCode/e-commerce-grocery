package com.ronosoft.alwarmart.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ronosoft.alwarmart.Model.BrandModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrandService {
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "BrandService";

    public BrandService(Context context) {
        this.context = context;
    }

    public interface addBrandsListener{
        void onFailure(FirebaseFirestoreException error);
        void onSuccess(ArrayList<BrandModel> brandModel);
    }
  public interface addBrandsByIdListener{
        void onFailure(Exception error);
        void onSuccess(BrandModel brandModel);
    }



    public void getAllBrandWithIcons(addBrandsListener callback){
        db.collection("brand").addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle the case where an error occurred while listening for updates
                callback.onFailure(error);
                return;
            }

            // Create a list to hold category tags
            ArrayList<BrandModel> brandModels = new ArrayList<>();
            // Iterate through the documents in the snapshot
            assert value != null;
            for (QueryDocumentSnapshot document : value) {
                // Convert each document to a ProductCategoryModel object
                BrandModel brandObj = document.toObject(BrandModel.class);
                // Add the category tag to the list
                brandModels.add(brandObj);
            }
            // Invoke the onSuccess method of the callback with the updated list of categories
            callback.onSuccess(brandModels);
        });


    }


    public void getAllBrandWithIconsById(String id, addBrandsByIdListener callback) {
        db.collection("brand").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "onComplete task: "+task);
                    DocumentSnapshot document = task.getResult();
                    Log.i(TAG, "onComplete brandId document: "+document.getId());
                    Log.i(TAG, "onComplete brandName document: "+document.get("brandName"));
                    Log.i(TAG, "onComplete brandIcon document: "+document.get("brandIcon"));

                    if (document.exists()) {
                        BrandModel brandModel = document.toObject(BrandModel.class);
                        Log.i(TAG, "onComplete brand icon: "+brandModel.getBrandIcon());

                        callback.onSuccess(brandModel);
                    } else {
                        callback.onFailure(new Exception("No such document"));
                    }
                } else {
                    callback.onFailure(task.getException());
                }
            }
        }).addOnFailureListener(callback::onFailure);
    }



}
