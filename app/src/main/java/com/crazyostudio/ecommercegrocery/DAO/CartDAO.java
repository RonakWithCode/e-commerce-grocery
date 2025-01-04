package com.crazyostudio.ecommercegrocery.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;

import java.util.List;


@Dao
public interface CartDAO {
    @Query("select * from cart")
    List<ShoppingCartFirebaseModelDAO> getAllModel();

    @Insert
    void insertAll(ShoppingCartFirebaseModelDAO users);


    @Update
    void UpdateAll(ShoppingCartFirebaseModelDAO users);


    @Delete
    void delete(ShoppingCartFirebaseModelDAO user);





    @Query("UPDATE cart SET productSelectQuantity = :quantity WHERE productId = :productId")
    void updateQuantityByProductId(String productId, int quantity);

    @Query("DELETE FROM cart WHERE productId = :productId")
    void deleteByProductId(String productId);

    @Query("DELETE FROM cart")
    void deleteAll();

    // Get a product by its productId
    @Query("SELECT * FROM cart WHERE productId = :productId")
    ShoppingCartFirebaseModelDAO getProductById(String productId);

    @Query("SELECT * FROM cart WHERE productId = :productId")
    LiveData<ShoppingCartFirebaseModelDAO> observeProductById(String productId);
}
