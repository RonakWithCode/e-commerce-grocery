package com.crazyostudio.ecommercegrocery.Model;

public class ProductCategoryModel {
    private String Tag,ImageUri;

    public ProductCategoryModel() {
    }

    public ProductCategoryModel(String tag, String imageUri) {
        Tag = tag;
        ImageUri = imageUri;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
