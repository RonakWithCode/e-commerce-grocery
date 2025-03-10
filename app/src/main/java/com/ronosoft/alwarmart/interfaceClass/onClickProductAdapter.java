package com.ronosoft.alwarmart.interfaceClass;

import com.ronosoft.alwarmart.Model.ProductModel;
import java.util.ArrayList;

@FunctionalInterface
public interface onClickProductAdapter {
    void onClick(ProductModel productModel, ArrayList<ProductModel> sameProducts);
}
