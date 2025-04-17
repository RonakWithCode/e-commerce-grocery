package com.ronosoft.alwarmart.Manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ronosoft.alwarmart.DAO.CartDAOHelper;
import com.ronosoft.alwarmart.DAO.ShoppingCartFirebaseModelDAO;
import com.ronosoft.alwarmart.Model.ShoppingCartFirebaseModel;
import com.ronosoft.alwarmart.Services.AuthService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductManager {
    Context context;
    CartDAOHelper databaseHelper;

    public ProductManager(Context mContext){
        this.context = mContext;
        databaseHelper = CartDAOHelper.getDB(context);
    }



    public interface addListenerForIsProductInCart{
        void FoundProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel);
        void notFoundInCart();
    }


    public interface AddListenerForAddToBothInDatabase{
        void added(ShoppingCartFirebaseModel shoppingCartFirebaseModel);
        void failure(Exception e);
    }





    public void addToBothDatabase(ShoppingCartFirebaseModel shoppingCartsProductModel,AddListenerForAddToBothInDatabase listener){
        String uId = new AuthService().getUserId();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(uId);
//        String productNameToFind = shoppingCartsProductModel.getProductId();
        productsRef.child(shoppingCartsProductModel.getProductId()).setValue(shoppingCartsProductModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ShoppingCartFirebaseModelDAO modelDAO = new ShoppingCartFirebaseModelDAO(shoppingCartsProductModel.getProductId(),shoppingCartsProductModel.getProductSelectQuantity());
                        databaseHelper.ModelDAO().insertAll(modelDAO);
                        listener.added(shoppingCartsProductModel);
                    }
                })
                .addOnFailureListener(listener::failure);
//                .addOnFailureListener(error -> basicFun.AlertDialog(requireContext(), error.toString()));


    }




    public void UpdateCartQuantityById(String uid,String itemId,int Quantity){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(uid).child(itemId).child("productSelectQuantity").setValue(Quantity).addOnCompleteListener(task -> {
            databaseHelper.ModelDAO().updateQuantityByProductId(itemId,Quantity);
        }).addOnFailureListener(e -> {
        });
    }

    @SuppressLint("LogNotTimber")
    public void RemoveCartProductById(String itemId){
        AuthService authService = new AuthService();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(authService.getUserId())).child(itemId).removeValue().addOnCompleteListener(task -> {
            databaseHelper.ModelDAO().deleteByProductId(itemId);
        }).addOnFailureListener(e -> {
            Log.e("ProductMnager","Remove " + e);
        });
    }

    public void removeCartProducts(){
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(uid)).child(itemId).removeValue().addOnCompleteListener(task -> {
        databaseHelper.ModelDAO().deleteAll();
//        }).addOnFailureListener(e -> {
//            Log.e("ProductMnager","Remove " + e);
//        });
    }

    public LiveData<ShoppingCartFirebaseModelDAO> observeCartItem(String productId) {
        return databaseHelper.ModelDAO().observeProductById(productId);
    }



    public void deleteProduct(){
        databaseHelper.ModelDAO().deleteAll();
//        databaseHelper.ModelDAO.deleteAll()
    }




    public interface SyncCartCallback {
        void onSyncSuccess();
        void onSyncFailure(String error);
    }

    public void syncCartFromFirebase(SyncCartCallback callback) {
        String uId = new AuthService().getUserId();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(uId);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ShoppingCartFirebaseModelDAO> daoList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ShoppingCartFirebaseModel model = child.getValue(ShoppingCartFirebaseModel.class);
                    if (model != null) {
                        ShoppingCartFirebaseModelDAO dao = new ShoppingCartFirebaseModelDAO(
                                model.getProductId(),
                                model.getProductSelectQuantity()
                        );
                        daoList.add(dao);
                    }
                }
                databaseHelper.ModelDAO().deleteAll();
                for (ShoppingCartFirebaseModelDAO dao : daoList) {
                    databaseHelper.ModelDAO().insertAll(dao);
                }
                Log.i("ProductManager", "Cart synchronized: " + daoList.size() + " items");
                if (callback != null) {
                    callback.onSyncSuccess();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductManager", "Failed to sync cart: " + error.getMessage());
                if (callback != null) {
                    callback.onSyncFailure(error.getMessage());
                }
            }
        });
    }


    // New method to get all cart items as LiveData
    public LiveData<List<ShoppingCartFirebaseModelDAO>> getAllCartItems() {
        return databaseHelper.ModelDAO().getAllCartItems();
    }


}
