package com.crazyostudio.ecommercegrocery.Model;

import java.util.ArrayList;

public class HomeProductModel {
    private String title;
    private ArrayList<ProductModel> product;

    public HomeProductModel() {
    }

    public HomeProductModel(String title, ArrayList<ProductModel> product) {
        this.title = title;
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ProductModel> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<ProductModel> product) {
        this.product = product;
    }
}
