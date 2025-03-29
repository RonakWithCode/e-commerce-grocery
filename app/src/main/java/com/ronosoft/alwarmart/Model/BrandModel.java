package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

@Keep
public class BrandModel {
    String BrandName;
    String brandIcon;


    public BrandModel(){}

    public BrandModel(String brandName, String brandIcon) {
        BrandName = brandName;
        this.brandIcon = brandIcon;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getBrandIcon() {
        return brandIcon;
    }

    public void setBrandIcon(String brandIcon) {
        this.brandIcon = brandIcon;
    }
}
