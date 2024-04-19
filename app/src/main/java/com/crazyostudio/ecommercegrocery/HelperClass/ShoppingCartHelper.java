package com.crazyostudio.ecommercegrocery.HelperClass;

import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductFirebaseModel;
import com.crazyostudio.ecommercegrocery.Model.ShoppingCartsProductModel;

import java.util.ArrayList;

public class ShoppingCartHelper {

    public static double calculateTotalSavings(ArrayList<ShoppingCartsProductModel> products) {
        double totalSavings = 0.0;

        for (ShoppingCartsProductModel product : products) {
            double savingsForProduct = product.getMrp() * product.getDefaultQuantity();
            totalSavings += savingsForProduct;
        }

        return totalSavings;
    }
//    public static double calculateTotalSavingsByShoppingCartsProductModel(ArrayList<ShoppingCartsProductModel> products) {
//        double totalSavings = 0.0;
//
//        for (ShoppingCartsProductModel product : products) {
//            double savingsForProduct = product.getMrp() * product.getDefaultQuantity();
//            totalSavings += savingsForProduct;
//        }
//
//        return totalSavings;
//    }

    public static double calculateTotalPrices(ArrayList<ShoppingCartsProductModel> productList) {
        double totalPrices = 00.0;

        for (ShoppingCartsProductModel product : productList) {
            double totalPrice = product.getPrice() * (double) product.getDefaultQuantity();
            totalPrices = totalPrices + totalPrice;
        }

        return totalPrices;
    }

}
