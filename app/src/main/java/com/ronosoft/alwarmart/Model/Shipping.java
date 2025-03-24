package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Shipping implements Parcelable {
    private String shippingMethod;
    private double ShippingFee;
    private Date deliveredData;
    private AddressModel shippingAddress;
    private String shippingStatus;

    public Shipping() {
    }

    public Shipping(String shippingMethod, double shippingFee, Date deliveredData, AddressModel shippingAddress, String shippingStatus) {
        this.shippingMethod = shippingMethod;
        ShippingFee = shippingFee;
        this.deliveredData = deliveredData;
        this.shippingAddress = shippingAddress;
        this.shippingStatus = shippingStatus;
    }

    protected Shipping(Parcel in) {
        shippingMethod = in.readString();
        ShippingFee = in.readDouble();
        shippingAddress = in.readParcelable(AddressModel.class.getClassLoader());
        shippingStatus = in.readString();
    }

    public static final Creator<Shipping> CREATOR = new Creator<Shipping>() {
        @Override
        public Shipping createFromParcel(Parcel in) {
            return new Shipping(in);
        }

        @Override
        public Shipping[] newArray(int size) {
            return new Shipping[size];
        }
    };

    public double getShippingFee() {
        return ShippingFee;
    }

    public void setShippingFee(double shippingFee) {
        ShippingFee = shippingFee;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }
    public Date getDeliveredData() {
        return deliveredData;
    }

    public void setDeliveredData(Date deliveredData) {
        this.deliveredData = deliveredData;
    }
    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public AddressModel getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressModel shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(shippingMethod);
        dest.writeDouble(ShippingFee);
        dest.writeLong(deliveredData != null ? deliveredData.getTime() : -1);
        dest.writeParcelable(shippingAddress, flags);
        dest.writeString(shippingStatus);
    }
}
