package com.crazyostudio.ecommercegrocery.Model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class StockEntryModel implements Parcelable {
    private int quantity;
    private Date expiryDate;
    private Date entryDate; // Date of entry


    public StockEntryModel(){}

    public StockEntryModel(int quantity, Date expiryDate, Date entryDate) {
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.entryDate = entryDate;
    }

    protected StockEntryModel(Parcel in) {
        quantity = in.readInt();
    }

    public static final Creator<StockEntryModel> CREATOR = new Creator<StockEntryModel>() {
        @Override
        public StockEntryModel createFromParcel(Parcel in) {
            return new StockEntryModel(in);
        }

        @Override
        public StockEntryModel[] newArray(int size) {
            return new StockEntryModel[size];
        }
    };

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(quantity);
    }
}

