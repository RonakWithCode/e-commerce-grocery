package com.crazyostudio.ecommercegrocery.Model;
import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {
    private String name, phone, Address;

    public AddressModel() {}

    public AddressModel(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        Address = address;
    }

    protected AddressModel(Parcel in) {
        name = in.readString();
        phone = in.readString();
        Address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(Address);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
