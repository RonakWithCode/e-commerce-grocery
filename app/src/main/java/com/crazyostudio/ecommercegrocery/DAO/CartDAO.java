package com.crazyostudio.ecommercegrocery.DAO;

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

}
