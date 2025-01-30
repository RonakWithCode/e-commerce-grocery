package com.ronosoft.alwarmart.Adapter;

import com.ronosoft.alwarmart.Model.ProductModel;
import com.ronosoft.alwarmart.Model.ShoppingCartFirebaseModel;

public interface SearchAdapterInterface {
    void onclick(ProductModel productModel);
    void Remove(ProductModel productModel);
    void UpdateQTY(ProductModel productModel);
    void AddProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel);

}
