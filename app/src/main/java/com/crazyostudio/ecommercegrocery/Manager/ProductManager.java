package com.crazyostudio.ecommercegrocery.Manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.crazyostudio.ecommercegrocery.DAO.CartDAOHelper;
import com.crazyostudio.ecommercegrocery.DAO.ShoppingCartFirebaseModelDAO;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;
import com.crazyostudio.ecommercegrocery.Services.AuthService;
import com.crazyostudio.ecommercegrocery.Services.DatabaseService;
import com.crazyostudio.ecommercegrocery.javaClasses.basicFun;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class ProductManager {
    Context context;
    CartDAOHelper databaseHelper;

    public ProductManager(Context mContext){
        this.context = mContext;
        databaseHelper = CartDAOHelper.getDB(context);
    }


    private interface addListenerForCheckProductInCartAndAddById{
        void added(int DefaultQuantity);
        void AlreadyExistsInCart(int DefaultQuantity);
        void failure(Exception error);
    }


//  This is use for check product in cart and also added product into Cart or room database
    public void CheckProductInCartAndAddById(String ProductId,int DefaultQuantity , addListenerForCheckProductInCartAndAddById callback){
        String UserId  = new AuthService().getUserId();

        isProductInCart(ProductId, new addListenerForIsProductInCart() {
            @Override
            public void FoundProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
                callback.AlreadyExistsInCart(shoppingCartFirebaseModel.getProductSelectQuantity());
            }

            @Override
            public void notFoundInCart() {
//                Write code for add Product
                DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(UserId);
                ShoppingCartFirebaseModel firebaseModel = new ShoppingCartFirebaseModel(ProductId,DefaultQuantity);
                productsRef.child(ProductId).setValue(firebaseModel)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    ProductAddToRoom(firebaseModel);
                                    callback.added(DefaultQuantity);
                                }
                            })
                            .addOnFailureListener(callback::failure);
            }
        });
    }

//    public void CheckProductInCartInRoom(){
//
//    }


//    public void UpdateQtyInBothQTY(){
//
//    }
    public void UpdateCartQuantityById(String uid,String itemId,int Quantity){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(uid).child(itemId).child("productSelectQuantity").setValue(Quantity).addOnCompleteListener(task -> {
            databaseHelper.ModelDAO().updateQuantityByProductId(itemId,Quantity);
        }).addOnFailureListener(e -> {
        });
    }

    @SuppressLint("LogNotTimber")
    public void RemoveCartProductById(String uid, String itemId){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Cart").child(Objects.requireNonNull(uid)).child(itemId).removeValue().addOnCompleteListener(task -> {
            databaseHelper.ModelDAO().deleteByProductId(itemId);
        }).addOnFailureListener(e -> {
            Log.e("ProductMnager","Remove " + e);
        });
    }

    public interface addListenerForIsProductInCart{
        void FoundProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel);
        void notFoundInCart();
    }

    public void isProductInCart(String id,addListenerForIsProductInCart callback) {
        List<ShoppingCartFirebaseModelDAO> daoList = getAllRoomCartData();
        for (ShoppingCartFirebaseModelDAO dao : daoList) {
            if (dao.getProductId().equals(id)) {
                callback.FoundProduct(new ShoppingCartFirebaseModel(dao.getProductId(),dao.getProductSelectQuantity()));
            }
        }
//        callback.notFoundInCart();
    }

    public List<ShoppingCartFirebaseModelDAO> getAllRoomCartData(){
        return databaseHelper.ModelDAO().getAllModel();
    }

    public void ProductAddToRoom(ShoppingCartFirebaseModel shoppingCartFirebaseModel) {
        ShoppingCartFirebaseModelDAO cartFirebaseModelDAO = new ShoppingCartFirebaseModelDAO(shoppingCartFirebaseModel.getProductId(),shoppingCartFirebaseModel.getProductSelectQuantity());
        databaseHelper.ModelDAO().insertAll(cartFirebaseModelDAO);
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







}
