package com.ronosoft.alwarmart.Services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ronosoft.alwarmart.DAO.CartDAOHelper;
import com.ronosoft.alwarmart.DAO.ShoppingCartFirebaseModelDAO;
import com.ronosoft.alwarmart.Manager.ProductManager;
import com.ronosoft.alwarmart.Model.AddressModel;
import com.ronosoft.alwarmart.Model.OffersModel;
import com.ronosoft.alwarmart.Model.OrderModel;
import com.ronosoft.alwarmart.Model.ProductCategoryModel;
import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.ShoppingCartFirebaseModel;
import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;
import com.ronosoft.alwarmart.Model.UserinfoModels;

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
        void onSuccess(ProductModel product);
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
        void onSuccess(ArrayList<ShoppingCartsProductModel> cartsProductModels);
        void onError(String errorMessage);
    }

    public interface GetAllShoppingCartsProductModelFirebaseCallback {
        void onSuccess(ArrayList<ShoppingCartsProductModel> products);
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

    public interface getOffer {
        void onSuccess(ArrayList<OffersModel> offers);
        void onError(DatabaseError errorMessage);
    }

    /**
     * Retrieves products filtered by category with index on "available".
     */
    public void getAllProductsByCategoryOnly(String category, GetAllProductsCallback callback) {
        database.collection("Product")
                .whereEqualTo("category", category)
                .orderBy("available", Query.Direction.DESCENDING) // Changed to "available"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ProductModel> products = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                ProductModel product = document.toObject(ProductModel.class);
                                if (product != null && product.isAvailable()) { // Assuming isAvailable() checks "available"
                                    products.add(product);
                                }
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    /**
     * Retrieves products filtered by brand, checking "available".
     */
    public void getBrandProducts(String brand, GetAllProductsCallback callback) {
        database.collection("Product")
                .whereEqualTo("brand", brand)
                .orderBy("available", Query.Direction.DESCENDING) // Added index and availability check
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ProductModel> products = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                ProductModel product = document.toObject(ProductModel.class);
                                if (product != null && product.isAvailable()) { // Check availability
                                    products.add(product);
                                }
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    /**
     * Retrieves all products (no index needed here).
     */
    public void getAllProducts(GetAllProductsCallback callback) {
        database.collection("Product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ProductModel> products = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ProductModel product = document.toObject(ProductModel.class);
                        if (product != null && product.isAvailable()) {
                            products.add(product);
                        }
                    }
                }
                callback.onSuccess(products);
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    /**
     * Retrieves a product by its document id.
     */
    public void getAllProductById(String id, GetAllProductsModelCallback callback) {
        if (id != null) {
            database.collection("Product").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
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
            callback.onError("Provided document id is null");
        }
    }

    /**
     * Retrieves products for a given category excluding the given product id.
     */
    public void getAllProductsByCategory(String id, String category, GetAllProductsCallback callback) {
        database.collection("Product")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ProductModel> products = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                ProductModel product = document.toObject(ProductModel.class);
                                if (product != null && !product.getProductId().equals(id) && product.isAvailable()) {
                                    products.add(product);
                                }
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    /**
     * Retrieves all categories.
     */
    public void getAllCategory(GetAllCategoryCallback callback) {
        database.collection("Category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ProductCategoryModel> categoryModels = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ProductCategoryModel categoryModel = document.toObject(ProductCategoryModel.class);
                        if (categoryModel != null) {
                            categoryModels.add(categoryModel);
                        }
                    }
                }
                callback.onSuccess(categoryModels);
            } else {
                callback.onError(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    /**
     * Retrieves the user's cart using Firebase Realtime Database.
     */
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
                                        product.setSelectableQuantity(cartModel.getProductSelectQuantity());
                                        finalProduct.add(product);
                                    }
                                }
                            }
                            callback.onSuccess(finalProduct);
                        }
                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    callback.onError("Cart is empty");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                        public void onSuccess(ArrayList<ShoppingCartsProductModel> products) {
                            ArrayList<ShoppingCartsProductModel> finalProduct = new ArrayList<>();
                            for (ShoppingCartsProductModel product : products) {
                                for (ShoppingCartFirebaseModel cartModel : list) {
                                    if (product.getProductId().equals(cartModel.getProductId())) {
                                        product.setSelectableQuantity(cartModel.getProductSelectQuantity());
                                        finalProduct.add(product);
                                    }
                                }
                            }
                            callback.onSuccess(finalProduct);
                        }
                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
                } else {
                    callback.onError("Cart is empty");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }

    public void getRecommendations(ArrayList<String> categories, ArrayList<String> productNames, GetAllProductsCallback callback) {
        List<Query> queries = new ArrayList<>();
        for (String category : categories) {
            for (String productName : productNames) {
                Query query = database.collection("Product")
                        .whereEqualTo("category", category)
                        .whereEqualTo("productName", productName)
                        .limit(10);
                queries.add(query);
            }
        }
        Query combinedQuery = queries.get(0);
        for (int i = 1; i < queries.size(); i++) {
            combinedQuery = combinedQuery.startAt(queries.get(i));
        }
        combinedQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<ProductModel> recommendations = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ProductModel product = document.toObject(ProductModel.class);
                        if (product != null && product.isAvailable()) {
                            recommendations.add(product);
                        }
                    }
                }
                callback.onSuccess(recommendations);
            } else {
                callback.onError("error " + task.getException());
            }
        });
    }

    public void getProductsByModelId(ArrayList<String> modelIds, GetAllShoppingCartsProductModelCallback callback) {
        database.collection("Product")
                .whereIn("productId", modelIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ShoppingCartsProductModel> products = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                ShoppingCartsProductModel product = document.toObject(ShoppingCartsProductModel.class);
                                if (product != null && product.isAvailable()) {
                                    products.add(product);
                                }
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    public void getProductsByModelIdFirebase(ArrayList<String> modelIds, GetAllShoppingCartsProductModelFirebaseCallback callback) {
        database.collection("Product")
                .whereIn("productId", modelIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ShoppingCartsProductModel> products = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                ShoppingCartsProductModel product = document.toObject(ShoppingCartsProductModel.class);
                                if (product != null && product.isAvailable()) {
                                    products.add(product);
                                }
                            }
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    public void removeCartItemById(String uid, String itemId) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(uid)).child(itemId).removeValue();
    }

    public void removeCartItems(Context context, String uid) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(uid)).removeValue();
        new ProductManager(context).removeCartProducts();
    }

    public void UpdateCartQuantityById(String uid, String itemId, int Quantity) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(uid).child(itemId).child("productSelectQuantity").setValue(Quantity);
    }

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
                ArrayList<String> wishListItems = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String productId = snapshot.getValue(String.class);
                        if (productId != null) {
                            wishListItems.add(productId);
                        }
                    }
                    callback.onSuccess(wishListItems);
                } else {
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
        database.collection("UserInfo").document(userInfo.getUserId())
                .set(userInfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userInfo.getUsername()).build();
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                .updateProfile(profileUpdates);
                        callback.onSuccess(task);
                    }
                }).addOnFailureListener(e -> callback.onError(e.toString()));
    }

    public void CheckNotificationToken(UpdateTokenCallback callback) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        callback.onSuccess(token);
                    } else {
                        callback.onError("Failed to get FCM token: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                })
                .addOnFailureListener(e -> callback.onError("Exception while getting FCM token: " + e.getMessage()));
    }

    public void getUserInfo(String userId, getUserInfoCallback callback) {
        database.collection("UserInfo").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            UserinfoModels userInfo = document.toObject(UserinfoModels.class);
                            callback.onSuccess(userInfo);
                        } else {
                            callback.onError("User document does not exist");
                        }
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    public void getUserInfoByDocumentSnapshot(String userId, getUserInfoDocumentSnapshotCallback callback) {
        database.collection("UserInfo").document(userId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot);
                    } else {
                        callback.onError("User document does not exist");
                    }
                });
    }

    public void setAdders(AddressModel newAddress, String userId, SetAddersCallback callback) {
        database.collection("UserInfo").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
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

    public void removeAdders(String uid, AddressModel address, removeAddersCallback callback) {
        database.collection("UserInfo").document(uid)
                .update("address", FieldValue.arrayRemove(address))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Failure to remove address: " + task.getException().toString());
                    }
                });
    }

    public void UpdateUserInfo(String userId, String name, UpdateUserInfoCallback callback) {
        database.collection("UserInfo").document(userId)
                .update("username", name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                .updateProfile(profileUpdates);
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to save user info");
                    }
                });
    }

    public void PlaceOder(OrderModel order, PlaceOrderCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Order")
                .child(order.getCustomer().getCustomerId())
                .child(order.getOrderId())
                .setValue(order)
                .addOnCompleteListener(task -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void getOffers(getOffer offer) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("offer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<OffersModel> offersModelArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            OffersModel offersModel = data.getValue(OffersModel.class);
                            if (offersModel != null) {
                                offersModelArrayList.add(offersModel);
                            }
                        }
                        offer.onSuccess(offersModelArrayList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        offer.onError(error);
                    }
                });
    }

    public interface AddCartByIdAndADD {
        void added();
        void ExistsInCart(int DefaultQuantity);
        void failure(Exception error);
    }

    public void checkCartByIdAndAdd(String productNameToFind, int DefaultQuantity, AddCartByIdAndADD inter) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        com.google.firebase.database.Query query = productsRef.orderByKey().equalTo(productNameToFind);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ShoppingCartFirebaseModel shoppingCartFirebaseModel = dataSnapshot.getValue(ShoppingCartFirebaseModel.class);
                    inter.added();
                } else {
                    ShoppingCartFirebaseModel shoppingCartFirebaseModel = new ShoppingCartFirebaseModel(productNameToFind, DefaultQuantity);
                    productsRef.child(productNameToFind).setValue(shoppingCartFirebaseModel)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    inter.added();
                                }
                            })
                            .addOnFailureListener(error -> inter.failure(error));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Silent fail, Crashlytics will catch if critical
            }
        });
    }

    public void updateRoomDatabase(Context context, String id) {
        CartDAOHelper databaseHelper = CartDAOHelper.getDB(context);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseHelper.clearAllTables();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ShoppingCartFirebaseModel productModel = snapshot1.getValue(ShoppingCartFirebaseModel.class);
                                if (productModel != null) {
                                    ShoppingCartFirebaseModelDAO shoppingCartFirebaseModelDAO =
                                            new ShoppingCartFirebaseModelDAO(productModel.getProductId(), productModel.getProductSelectQuantity());
                                    databaseHelper.ModelDAO().insertAll(shoppingCartFirebaseModelDAO);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Silent fail, Crashlytics will catch if critical
                    }
                });
    }

    public interface addCartRoomDatabase {
        void Found(List<ShoppingCartFirebaseModelDAO> list);
        void notFound();
    }

    public void getCartRoomDatabase(Context context, addCartRoomDatabase callback) {
        CartDAOHelper databaseHelper = CartDAOHelper.getDB(context);
        List<ShoppingCartFirebaseModelDAO> daoList = databaseHelper.ModelDAO().getAllModel();
        if (daoList.isEmpty()) {
            callback.notFound();
        } else {
            callback.Found(daoList);
        }
    }
}