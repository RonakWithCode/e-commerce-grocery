package com.crazyostudio.ecommercegrocery.Model;


public class BrandModel {
    String BrandName;
    String BrandIcon;


    public BrandModel(){}

    public BrandModel(String brandName, String brandIcon) {
        BrandName = brandName;
        BrandIcon = brandIcon;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getBrandIcon() {
        return BrandIcon;
    }

    public void setBrandIcon(String brandIcon) {
        BrandIcon = brandIcon;
    }
}
