package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class HomeProductModel implements Parcelable {
    private String title;
    private ArrayList<ProductModel> product;

    public HomeProductModel() {
    }

    public HomeProductModel(String title, ArrayList<ProductModel> product) {
        this.title = title;
        this.product = product;
    }

    protected HomeProductModel(Parcel in) {
        title = in.readString();
        product = in.createTypedArrayList(ProductModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(product);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HomeProductModel> CREATOR = new Creator<HomeProductModel>() {
        @Override
        public HomeProductModel createFromParcel(Parcel in) {
            return new HomeProductModel(in);
        }

        @Override
        public HomeProductModel[] newArray(int size) {
            return new HomeProductModel[size];
        }
    };

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
