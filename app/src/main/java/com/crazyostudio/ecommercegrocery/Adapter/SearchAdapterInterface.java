package com.crazyostudio.ecommercegrocery.Adapter;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartFirebaseModel;

public interface SearchAdapterInterface {
    void onclick(ProductModel productModel);
    void Remove(ProductModel productModel);
    void UpdateQTY(ProductModel productModel);
    void AddProduct(ShoppingCartFirebaseModel shoppingCartFirebaseModel);

}
