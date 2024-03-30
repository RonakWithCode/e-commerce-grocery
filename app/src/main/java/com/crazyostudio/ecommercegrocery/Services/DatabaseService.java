package com.crazyostudio.ecommercegrocery.Services;

import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Objects;

public class DatabaseService {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

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
    public interface getUserInfoCallback {
        void onSuccess(UserinfoModels user);

        void onError(String errorMessage);
    }
    public interface getUserInfoDocumentSnapshotCallback {
        void onSuccess(DocumentSnapshot user);

        void onError(String errorMessage);
    }
    public interface UpdateUserInfoCallback {
        void onSuccess();

        void onError(String errorMessage);
    }
    public interface SetAddersCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
    public void getAllProducts(GetAllProductsCallback callback) {
        database.collection("Product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ProductModel> products = new ArrayList<>();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    ProductModel product = document.toObject(ProductModel.class);
                    if (product != null && product.isAvailable()) {
                        products.add(product);
                    }
                }
                callback.onSuccess(products);
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    public void getAllCategory(GetAllCategoryCallback callback) {
        database.collection("Category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    ProductCategoryModel categoryModel = document.toObject(ProductCategoryModel.class);
                    if (categoryModel != null) {
                        categoryModels.add(categoryModel);
                    }
                }
                callback.onSuccess(categoryModels);
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }


    public void getUserCartById(String id, GetUserCartByIdCallback callback) {
        database.collection("Cart").document(id).collection("items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ShoppingCartsProductModel> models = new ArrayList<>();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    ShoppingCartsProductModel productModel = document.toObject(ShoppingCartsProductModel.class);
                    if (productModel != null) {
                        models.add(productModel);
                    }
                }
                callback.onSuccess(models);
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    public void removeCartItemById(String uid, String itemId) {
        database.collection("Cart").document(uid).collection("items").document(itemId).delete();
    }

    public void UpdateCartQuantityById(String uid, String itemId, int quantity) {
        database.collection("Cart").document(uid).collection("items").document(itemId).update("defaultQuantity", quantity);
    }

    public void setUserInfo(UserinfoModels userInfo, SetUserInfoCallback callback) {
        database.collection("UserInfo").document(userInfo.getUserId()).set(userInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(task);
            }
        }).addOnFailureListener(e -> callback.onError(e.toString()));
    }


    public void getUserInfo(String userId, getUserInfoCallback callback) {
        database.collection("UserInfo").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UserinfoModels userInfo = document.toObject(UserinfoModels.class);
                    callback.onSuccess(userInfo);
                } else {
                    // User document does not exist
                    callback.onError("User document does not exist");
                }
            } else {
                // Error getting document
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    public void getUserInfoByDocumentSnapshot(String userId, getUserInfoDocumentSnapshotCallback callback) {
        database.collection("UserInfo").document(userId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        // Error occurred
                        callback.onError(error.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Document exists
                        callback.onSuccess(documentSnapshot);
                    } else {
                        // User document does not exist
                        callback.onError("User document does not exist");
                    }
                });
    }

    public void setAdders(AddressModel newAddress,String userId ,SetAddersCallback callback) {
        database.collection("UserInfo").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            UserinfoModels userInfo = document.toObject(UserinfoModels.class);
                            if (userInfo != null) {
                                ArrayList<AddressModel> addresses = userInfo.getAddress();
                                if (addresses == null) {
                                    addresses = new ArrayList<>();
                                }
                                addresses.add(newAddress);
                                userInfo.setAddress(addresses);

                                database.collection("UserInfo").document(userId)
                                        .set(userInfo, SetOptions.merge())
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onError("Failed to save address.");
                                            }
                                        });
                            }
                        } else {
                            callback.onError("User info document doesn't exist.");
                        }
                    } else {
                        callback.onError("Failed to retrieve user info.");
                    }
                });
    }



   public void UpdateUserInfo(String userId,String name,String email,UpdateUserInfoCallback callback){
        database.collection("UserInfo")
                .document(userId)
                .update(
                        "username", name,
                        "emailAddress", email
                        // Add other fields as needed
                )
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        Toast.makeText(requireContext(), "User info saved successfully", Toast.LENGTH_SHORT).show();
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to save user info");
//                        Toast.makeText(requireContext(), "Failed to save user info", Toast.LENGTH_SHORT).show();
                        // Handle task failure
                    }
                });
    }


}
