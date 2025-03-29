package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

@Keep

public class ShoppingCartFirebaseModel {
    private String productId;
    private int productSelectQuantity;
    public ShoppingCartFirebaseModel() {
    }

    public ShoppingCartFirebaseModel(String productId, int productSelectQuantity) {
        this.productId = productId;
        this.productSelectQuantity = productSelectQuantity;
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
