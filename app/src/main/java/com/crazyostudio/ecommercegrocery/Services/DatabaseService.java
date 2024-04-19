package com.crazyostudio.ecommercegrocery.Services;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.Model.OrderModel;
import com.crazyostudio.ecommercegrocery.Model.ProductCategoryModel;
import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Model.UserinfoModels;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseService {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public interface GetAllProductsCallback {
        void onSuccess(ArrayList<ProductModel> products);

        void onError(String errorMessage);
    }

    public interface GetAllProductsModelCallback {
        void onSuccess(ProductModel products);

        void onError(String errorMessage);
    }
    public interface GetAllShoppingCartsProductModelCallback {
        void onSuccess(ArrayList<ShoppingCartsProductModel> products);

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
    public interface GetUserCartByIdShoppingCartsProductCallback {
        void onSuccess(ArrayList<ShoppingCartsProductFirebaseModel> cartsProductModels);

        void onError(String errorMessage);
    }

    public interface GetAllShoppingCartsProductModelFirebaseCallback {
        void onSuccess(ArrayList<ShoppingCartsProductFirebaseModel> products);

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
    public interface removeAddersCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
    public interface SetWishListCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
    public interface UpdateTokenCallback {
        void onSuccess(String token);
        void onError(String errorMessage);
    }
    public interface GetWishListItemsCallback {
        void onSuccess(ArrayList<String> products);
        void onError(String errorMessage);
    }
    public interface PlaceOrderCallback {
        void onSuccess();
        void onError(Exception errorMessage);
    }




    public void getAllProductsByCategoryOnly(String category, GetAllProductsCallback callback) {
        database.collection("Product")
                .whereEqualTo("category", category) // Filter by category
                .get()
                .addOnCompleteListener(task -> {
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


    public void getAllProductById(String id, GetAllProductsModelCallback callback) {
        if (id != null) {
            database.collection("Product").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ProductModel product = document.toObject(ProductModel.class);
                        if (product != null && product.isAvailable()) {
                            callback.onSuccess(product);
                        } else {
                            callback.onError("Product is null or not available");
                        }
                    } else {
                        callback.onError("Product document does not exist");
                    }
                } else {
                    callback.onError("Failed to retrieve product: " + task.getException().toString());
                }
            });
        } else {
            // Handle the case where id is null
            callback.onError("Provided document id is null");
        }
    }




    public void getAllProductsByCategory(String id,String category, GetAllProductsCallback callback) {
        database.collection("Product")
                .whereEqualTo("category", category) // Filter by category
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ProductModel> products = new ArrayList<>();
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            ProductModel product = document.toObject(ProductModel.class);
                            if (product != null && !product.getProductId().equals(id) && product.isAvailable()) {
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<ShoppingCartFirebaseModel> list = new ArrayList<>();
                    ArrayList<String> productIds = new ArrayList<>();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ShoppingCartFirebaseModel productModel = snapshot1.getValue(ShoppingCartFirebaseModel.class);
                        if (productModel != null) {
                            list.add(productModel);
                            productIds.add(productModel.getProductId());
                        }
                    }
                    getProductsByModelId(productIds, new GetAllShoppingCartsProductModelCallback() {
                        @Override
                        public void onSuccess(ArrayList<ShoppingCartsProductModel> products) {
                            ArrayList<ShoppingCartsProductModel> finalProduct = new ArrayList<>();
                            for (ShoppingCartsProductModel product : products) {
                                for (ShoppingCartFirebaseModel cartModel : list) {
                                    if (product.getProductId().equals(cartModel.getProductId())) {
                                        product.setDefaultQuantity(cartModel.getProductSelectQuantity());
                                        finalProduct.add(product);
                                    }
                                }
                            }
                            // Log the products after all changes are made
                            Log.i("DATABASE_ERROR", "List of changed products: " + finalProduct);
                            callback.onSuccess(finalProduct);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.i("DATABASE_ERROR", "Error retrieving products: " + errorMessage);
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    // Handle case where cart is empty or doesn't exist
                    callback.onError("Cart is empty");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("DATABASE_ERROR", "Error onCancelled: " + error);
                callback.onError(error.toString());
            }
        });

    }
    public void getUserCartByIdShoppingCarts(String id, GetUserCartByIdShoppingCartsProductCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<ShoppingCartFirebaseModel> list = new ArrayList<>();
                    ArrayList<String> productIds = new ArrayList<>();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ShoppingCartFirebaseModel productModel = snapshot1.getValue(ShoppingCartFirebaseModel.class);
                        if (productModel != null) {
                            list.add(productModel);
                            productIds.add(productModel.getProductId());
                        }
                    }
                    getProductsByModelIdFirebase(productIds, new GetAllShoppingCartsProductModelFirebaseCallback() {
                        @Override
                        public void onSuccess(ArrayList<ShoppingCartsProductFirebaseModel> products) {
                            ArrayList<ShoppingCartsProductFirebaseModel> finalProduct = new ArrayList<>();
                            for (ShoppingCartsProductFirebaseModel product : products) {
                                for (ShoppingCartFirebaseModel cartModel : list) {
                                    if (product.getProductId().equals(cartModel.getProductId())) {
                                        product.setDefaultQuantity(cartModel.getProductSelectQuantity());
                                        finalProduct.add(product);
                                    }
                                }
                            }
                            // Log the products after all changes are made
                            Log.i("DATABASE_ERROR", "List of changed products: " + finalProduct);
                            callback.onSuccess(finalProduct);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.i("DATABASE_ERROR", "Error retrieving products: " + errorMessage);
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    // Handle case where cart is empty or doesn't exist
                    callback.onError("Cart is empty");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("DATABASE_ERROR", "Error onCancelled: " + error);
                callback.onError(error.toString());
            }
        });

    }

    public void getRecommendations(ArrayList<String> categories, ArrayList<String> productNames, GetAllProductsCallback callback) {
        List<Query> queries = new ArrayList<>();

        // Create a list of queries for each combination of category and product name
        for (String category : categories) {
            for (String productName : productNames) {
                Query query = database.collection("Product").whereEqualTo("category", category)
                        .whereEqualTo("productName", productName)
                        .limit(10); // Limit to 10 recommendations per combination
                queries.add(query);
            }
        }

        // Use the Firestore 'in' operator to combine multiple queries into one
        Query combinedQuery = queries.get(0);
        for (int i = 1; i < queries.size(); i++) {
            combinedQuery = combinedQuery.startAt(queries.get(i));
        }

        combinedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<ProductModel> recommendations = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProductModel product = document.toObject(ProductModel.class);
                        recommendations.add(product);
                    }
                    // Pass the recommendations to the callback
                    callback.onSuccess(recommendations);
                } else {
                    // Pass the exception to the callback
                    callback.onError("error "+task.getException());
                }
            }
        });
    }

//    private void getRecommendations(String category, String productName, GetAllProductsCallback callback) {
//        Query query =  database.collection("Product").whereEqualTo("category", category)
//                .whereGreaterThanOrEqualTo("productName", productName)
//                .limit(10); // Limit to 10 recommendations
//
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                ArrayList<ProductModel> recommendations = new ArrayList<>();
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    ProductModel product = document.toObject(ProductModel.class);
//                    recommendations.add(product);
//                }
//                callback.onSuccess(recommendations);
//                // Display recommendations to the user
////                displayRecommendations(recommendations);
//            } else {
//                // Handle errors
//                callback.onError("Error getting recommendations: " + task.getException());
//                Log.d("DATABASE-SERVICE", "Error getting recommendations: ", task.getException());
//            }
//        });
//    }

    public void getProductsByModelId(ArrayList<String> modelIds, GetAllShoppingCartsProductModelCallback callback) {
        database.collection("Product")
                .whereIn("productId", modelIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ShoppingCartsProductModel> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ShoppingCartsProductModel product = document.toObject(ShoppingCartsProductModel.class);
                            if (product != null && product.isAvailable()) {
                                products.add(product);
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        Log.i("DATABASE_ERROR", "Error retrieving products: " + task.getException());
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                }).addOnFailureListener(e -> {
                    Log.i("DATABASE_ERROR", "Error onFailure: " + e);
                    callback.onError(e.toString());
                });
    }
    public void getProductsByModelIdFirebase(ArrayList<String> modelIds, GetAllShoppingCartsProductModelFirebaseCallback callback) {
        database.collection("Product")
                .whereIn("productId", modelIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ShoppingCartsProductFirebaseModel> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ShoppingCartsProductFirebaseModel product = document.toObject(ShoppingCartsProductFirebaseModel.class);
                            if (product != null && product.isAvailable()) {
                                products.add(product);
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        Log.i("DATABASE_ERROR", "Error retrieving products: " + task.getException());
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                }).addOnFailureListener(e -> {
                    Log.i("DATABASE_ERROR", "Error onFailure: " + e);
                    callback.onError(e.toString());
                });
    }


    public void removeCartItemById(String uid, String itemId) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(uid)).child(itemId).removeValue();

    }

    public void removeCartItems(String uid) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(uid)).removeValue();

    }


    public void UpdateCartQuantityById(String uid,String itemId,int Quantity){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(uid).child(itemId).child("productSelectQuantity").setValue(Quantity);
    }



//    TODO not test :
    public void addItemToWishList(String uid, String productId, SetWishListCallback callback) {
        DatabaseReference wishListRef = FirebaseDatabase.getInstance().getReference().child("wishList").child(uid);
        wishListRef.setValue(productId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void getWishListItems(String uid, GetWishListItemsCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference wishListRef = firebaseDatabase.getReference().child("wishList").child(uid);
        wishListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> wishListItems = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String productId = snapshot.getValue(String.class);
                        if (productId != null) {
                            wishListItems.add(productId);
                        }
                    }
                    callback.onSuccess(wishListItems);
                } else {
                    // Wishlist for this user is empty
                    callback.onSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });

    }


    public void setUserInfo(UserinfoModels userInfo, SetUserInfoCallback callback) {
        database.collection("UserInfo").document(userInfo.getUserId()).set(userInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userInfo.getUsername()).build();
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateProfile(profileUpdates);
                callback.onSuccess(task);

            }
        }).addOnFailureListener(e -> callback.onError(e.toString()));
    }


//    TODO write code Update Token in this code //-->

    public void CheckNotificationToken(UpdateTokenCallback callback){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(task.getResult());
                Log.i("ThisMainActivityLog", "onComplete: " + task.getResult());
            }else {
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }



//    public void UpdateToken(String token, int time){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("UserInfo").document(Objects.requireNonNull(firebaseAuth.getUid()));
//        userRef.update("token", token)
//                .addOnSuccessListener(aVoid -> {
//                    // Token update successful
//                    binding.ProgressBar.setVisibility(View.GONE);
//                    requireActivity().finish();
//                })
//                .addOnFailureListener(e -> {
//                    // Token update failed
//                    if (time == 0) {
//                        // Retry once if update fails for the first time
//                        binding.ProgressBar.setVisibility(View.GONE);
//                        UpdateToken(token, 1);
//                    } else {
//                        // Sign out if update fails again
//                        firebaseAuth.signOut();
//                        binding.ProgressBar.setVisibility(View.GONE);
//                    }
//                });
//    }




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
    public void removeAdders(String uid,AddressModel address ,removeAddersCallback callback ){
        database.collection("UserInfo")
                .document(uid)
                .update("address", FieldValue.arrayRemove(address))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        // Handle task failure
                        callback.onError("failure to remove a address " + task.getException().toString());
                    }
                });

    }

   public void UpdateUserInfo(String userId,String name,UpdateUserInfoCallback callback){
        database.collection("UserInfo")
                .document(userId)
                .update(
                        "username", name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateProfile(profileUpdates);
//                        Toast.makeText(requireContext(), "User info saved successfully", Toast.LENGTH_SHORT).show();
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to save user info");
//                        Toast.makeText(requireContext(), "Failed to save user info", Toast.LENGTH_SHORT).show();
                        // Handle task failure
                    }
                });
    }




    public void PlaceOder(OrderModel order,PlaceOrderCallback callback){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Order").child(order.getCustomer().getCustomerId()).child(order.getOrderId()).setValue(order).addOnCompleteListener(task -> callback.onSuccess()).addOnFailureListener(callback::onError);
    }



}
