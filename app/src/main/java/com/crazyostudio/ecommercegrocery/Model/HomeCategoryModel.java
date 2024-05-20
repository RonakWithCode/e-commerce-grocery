package com.crazyostudio.ecommercegrocery.Model;

import java.util.ArrayList;

public class HomeCategoryModel {
    private String CategoryName;
    private ArrayList<String> CategoryImages;
    private String ProductSize;
    private String Tag;

    public HomeCategoryModel() {}

    public HomeCategoryModel(String categoryName, ArrayList<String> categoryImages, String productSize, String tag) {
        CategoryName = categoryName;
        CategoryImages = categoryImages;
        ProductSize = productSize;
        Tag = tag;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public ArrayList<String> getCategoryImages() {
        return CategoryImages;
    }

    public void setCategoryImages(ArrayList<String> categoryImages) {
        CategoryImages = categoryImages;
    }

    public String getProductSize() {
        return ProductSize;
    }

    public void setProductSize(String productSize) {
        ProductSize = productSize;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }
}
