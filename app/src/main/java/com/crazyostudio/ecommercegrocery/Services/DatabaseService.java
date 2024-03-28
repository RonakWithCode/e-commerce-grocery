package com.crazyostudio.ecommercegrocery.Services;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

public class DatabaseService {
//    StorageDatabaseService storage = FirebaseStorage.getInstance();

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private final Context context;
//
//    public DatabaseService(Context context) {
//        this.context = context;
//    }TODO:  CHANGING This INFO this Type case

    public interface GetAllProductsCallback {
        void onSuccess(ArrayList<ProductModel> products);

        void onError(String errorMessage);
    }

    public interface GetAllCategoryCallback {
        void onSuccess(ArrayList<ProductCategoryModel> category);

        void onError(String errorMessage);
    }

    public interface GetUserCartByIdCallback {
        void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels);

        void onError(String errorMessage);
    }
    public interface SetUserInfoCallback {
        void onSuccess(Task<Void> suc);

        void onError(String errorMessage);
    }

    public void getAllProducts(GetAllProductsCallback callback) {
        database.getReference().child("Product").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ProductModel> products = new ArrayList<>();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    // Assuming ProductModel has a constructor that takes a DataSnapshot
                    ProductModel product = productSnapshot.getValue(ProductModel.class);
                    if (product != null && product.isAvailable()) {
                        products.add(product);
                    }
                }
                // Notify the callback about the successful retrieval
                callback.onSuccess(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Notify the callback about the error
                callback.onError(error.toString());
            }
        });
    }

    public void getAllCategory(GetAllCategoryCallback callback) {
        database.getReference().child("Category")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ProductCategoryModel categoryModel = snapshot1.getValue(ProductCategoryModel.class);
                            if (categoryModel != null) {
                                categoryModels.add(categoryModel);
                            }
                        }
                        callback.onSuccess(categoryModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toString());
                    }
                });


    }


    public void getUserCartById(String id, GetUserCartByIdCallback callback) {

        database.getReference().child("Cart").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ShoppingCartsProductModel> models = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ShoppingCartsProductModel productModel = snapshot1.getValue(ShoppingCartsProductModel.class);
                    if (productModel != null) {
                        models.add(productModel);
                    }
                }
                callback.onSuccess(models);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }


    public void removeCartItemById(String uid, String itemId) {
        database.getReference().child("Cart").child(Objects.requireNonNull(uid)).child(itemId).removeValue();
    }

    public void UpdateCartQuantityById(String uid,String itemId,int Quantity){
            database.getReference().child("Cart").child(uid).child(itemId).child("defaultQuantity").setValue(Quantity);
    }




    public void setUserInfo(UserinfoModels userInfo,SetUserInfoCallback callback){
        database.getReference().child("UserInfo").child(userInfo.getUserId()).setValue(userInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(task);
            }
        }).addOnFailureListener(e -> callback.onError(e.toString()) );
    }






}