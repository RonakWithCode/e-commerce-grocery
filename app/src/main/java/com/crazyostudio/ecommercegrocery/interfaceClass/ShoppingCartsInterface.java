package com.crazyostudio.ecommercegrocery.interfaceClass;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;

import java.util.ArrayList;

public interface ShoppingCartsInterface {
    void remove(int pos,String id);
    void UpdateQuantity(ShoppingCartsProductModel model,String id);
}
