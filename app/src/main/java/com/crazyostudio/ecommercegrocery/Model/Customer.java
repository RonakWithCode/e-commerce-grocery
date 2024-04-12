package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Customer implements Parcelable {
    private String customerId;
    private String fullName;
    private String phoneNumber;
    private String phoneNumber2;

    public Customer() {
    }

    public Customer(String customerId, String fullName, String phoneNumber, String phoneNumber2) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.phoneNumber2 = phoneNumber2;
    }

    protected Customer(Parcel in) {
        customerId = in.readString();
        fullName = in.readString();
        phoneNumber = in.readString();
        phoneNumber2 = in.readString();
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(customerId);
        dest.writeString(fullName);
        dest.writeString(phoneNumber);
        dest.writeString(phoneNumber2);
    }
}
