package com.crazyostudio.ecommercegrocery.HelperClass;

import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;

import java.util.ArrayList;

public class ShoppingCartHelper {

    public static double calculateTotalSavings(ArrayList<ShoppingCartsProductFirebaseModel> products) {
        double totalSavings = 0.0;

        for (ShoppingCartsProductFirebaseModel product : products) {
            double savingsForProduct = product.getMrp() * product.getDefaultQuantity();
            totalSavings += savingsForProduct;
        }

        return totalSavings;
    }
}
