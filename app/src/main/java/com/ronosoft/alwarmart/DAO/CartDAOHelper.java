package com.ronosoft.alwarmart.DAO;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = ShoppingCartFirebaseModelDAO.class,exportSchema = false ,version = 1)
public abstract class CartDAOHelper extends RoomDatabase {
    private static final String DB_NAME = "cartdb";
    private static CartDAOHelper instance;

    public static synchronized CartDAOHelper getDB(Context context){
        if (instance==null)
        {
            instance = Room.databaseBuilder(context,CartDAOHelper.class,DB_NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract CartDAO ModelDAO();

}
