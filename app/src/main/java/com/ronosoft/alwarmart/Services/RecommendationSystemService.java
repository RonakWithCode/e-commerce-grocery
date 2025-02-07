package com.ronosoft.alwarmart.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ronosoft.alwarmart.Model.AdsProductModels;
import com.ronosoft.alwarmart.Model.ProductModel;

import java.util.ArrayList;

public class RecommendationSystemService {

    private static final String TAG = "RecommendationSystemService";
    private static final int MAX_RECOMMENDATIONS = 10;
    private  FirebaseFirestore db;
    Context context;



    public RecommendationSystemService(Context context) {
        this.context =  context;
        db = FirebaseFirestore.getInstance();
    }


//Category Matching: Recommend products from the same category as items in the cart.
//Subcategory Matching: Prioritize subcategories (e.g., if the cart contains "Dairy - Milk," suggest "Dairy - Cheese").
//Frequently Bought Together (Static Data): Manually link commonly bought-together items (e.g., Bread → Butter).
//Discount-Based Recommendations: Show discounted items from the cart's category to increase conversions.


    public interface addCategoryListener{
        void onSuccess(ArrayList<ProductModel> productModels);
        void onError(Exception errorMessage);
    }

    public void getByCategoryMatching(ArrayList<String> cart,addCategoryListener listener){
        db.collection("Product").whereIn("category", cart).limit(MAX_RECOMMENDATIONS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                ArrayList<ProductModel> productModels = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()){
                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                    Log.i(TAG, "getByCategoryMatching: "+productModel.getProductName());
                    productModels.add(productModel);
                }
                listener.onSuccess(productModels);

            }
        }).addOnFailureListener(listener::onError);
    }


    public void getByAdsProductsForSearch(addCategoryListener listener){
        db.collection("AdsProduct").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<AdsProductModels> productModels = new ArrayList<>();
                    ArrayList<String> productId = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        AdsProductModels adsProductModels = documentSnapshot.toObject(AdsProductModels.class);
                        if (adsProductModels != null) {
                            productModels.add(adsProductModels);
                            productId.add(adsProductModels.getProductId());
                        }
                    }
                    loadProducts(productId,listener);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void loadProducts(ArrayList<String> productId,addCategoryListener listener) {
        db.collection("Product").whereIn("productId",productId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<ProductModel> productModelList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                        if (productModel != null) {

                            productModelList.add(productModel);

                        }
                    }
                    listener.onSuccess(productModelList);
                } else {
                    listener.onError(task.getException());
                }
            }
        });
    }


}
