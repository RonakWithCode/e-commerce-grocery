package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

import java.util.ArrayList;
@Keep
public class BestsellersModels {
    private String lastUpdated;
    private String version;
    private ArrayList<String> productIds;



    public BestsellersModels() {}

    public BestsellersModels(String lastUpdated, String version, ArrayList<String> productIds) {
        this.lastUpdated = lastUpdated;
        this.version = version;
        this.productIds = productIds;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(ArrayList<String> productIds) {
        this.productIds = productIds;
    }
}
