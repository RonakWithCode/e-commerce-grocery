package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {
    private String fullName;
    private String mobileNumber;
    private String flatHouse;
    private String address;
    private String landmark;
    private boolean isHomeSelected;
    private double latitude;
    private double longitude;

    public AddressModel() {}

    // public AddressModel(String fullName, String mobileNumber, String flatHouse, String address, String landmark, boolean isHomeSelected) {
    //     this(fullName, mobileNumber, flatHouse, address, landmark, isHomeSelected, 0.0, 0.0);
    // }

    public AddressModel(String fullName, String mobileNumber, String flatHouse, String address, String landmark, boolean isHomeSelected, double latitude, double longitude) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.flatHouse = flatHouse;
        this.address = address;
        this.landmark = landmark;
        this.isHomeSelected = isHomeSelected;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected AddressModel(Parcel in) {
        fullName = in.readString();
        mobileNumber = in.readString();
        flatHouse = in.readString();
        address = in.readString();
        landmark = in.readString();
        isHomeSelected = in.readByte() != 0;
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(mobileNumber);
        dest.writeString(flatHouse);
        dest.writeString(address);
        dest.writeString(landmark);
        dest.writeByte((byte) (isHomeSelected ? 1 : 0));
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFlatHouse() {
        return flatHouse;
    }

    public void setFlatHouse(String flatHouse) {
        this.flatHouse = flatHouse;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public boolean isHomeSelected() {
        return isHomeSelected;
    }

    public void setHomeSelected(boolean homeSelected) {
        isHomeSelected = homeSelected;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
}
