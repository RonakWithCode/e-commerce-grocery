package com.ronosoft.alwarmart.DAO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class ShoppingCartFirebaseModelDAO {

    @PrimaryKey(autoGenerate = true)
    private int Id;

    @ColumnInfo(name="productId")
    private String productId;
    @ColumnInfo(name="productSelectQuantity")
    private int productSelectQuantity;

    public ShoppingCartFirebaseModelDAO(String productId, int productSelectQuantity) {
        this.productId = productId;
        this.productSelectQuantity = productSelectQuantity;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getProductSelectQuantity() {
        return productSelectQuantity;
    }

    public void setProductSelectQuantity(int productSelectQuantity) {
        this.productSelectQuantity = productSelectQuantity;
    }
}
