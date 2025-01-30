package com.ronosoft.alwarmart.interfaceClass;

import com.ronosoft.alwarmart.Model.ShoppingCartsProductModel;

public interface ShoppingCartsInterface {
    void remove(int pos,String id,ShoppingCartsProductModel cartsProductModel);
    void UpdateQuantity(ShoppingCartsProductModel model,String id);
//    void UpdateQuantity(ShoppingCartsProductModel model,String id);
}
