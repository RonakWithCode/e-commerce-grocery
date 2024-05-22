package com.crazyostudio.ecommercegrocery.interfaceClass;

import com.crazyostudio.ecommercegrocery.Model.ProductModel;

import java.util.ArrayList;

public interface onClickProductAdapter {
//    void buyNow(ProductModel productModel);
//    void AddTOCart(ProductModel productModel);
    void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts);
}
