package com.crazyostudio.ecommercegrocery.interfaceClass;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;

import java.util.ArrayList;

public interface ShoppingCartsInterface {
    void remove(int pos,String id);
    void TotalPrice(int pos, ArrayList<ProductModel> models);
}
