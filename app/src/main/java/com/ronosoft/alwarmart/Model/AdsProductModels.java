package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

import java.util.ArrayList;
@Keep
public class AdsProductModels {
    private String id;
    private String productId;
    private ArrayList<SponsorType> sponsorTypes;


    public AdsProductModels(){}

    public AdsProductModels(String id, String productId, ArrayList<SponsorType> sponsorTypes) {
        this.id = id;
        this.productId = productId;
        this.sponsorTypes = sponsorTypes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ArrayList<SponsorType> getSponsorTypes() {
        return sponsorTypes;
    }

    public void setSponsorTypes(ArrayList<SponsorType> sponsorTypes) {
        this.sponsorTypes = sponsorTypes;
    }
}
