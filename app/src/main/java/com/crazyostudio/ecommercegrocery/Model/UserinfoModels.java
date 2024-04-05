package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class UserinfoModels implements Parcelable {
    private String userId;
    private String token;
    private String username;
    private String emailAddress;
    private String fullName;
    private String profilePictureUrl;
    private String dateOfBirth;
    private String gender;
    private ArrayList<AddressModel> address;
    private String city;
    private String state;
    private String phoneNumber;
    private boolean isActive;
    private boolean isPrivacy_modeBool;
    private boolean isDeal_notificationBool;
    private boolean isAccount_shipping_notificationBool;
//    private boolean isActive;

    public UserinfoModels(){}
//    public UserinfoModels(String userId, String username, String emailAddress, String fullName, String profilePictureUrl, String dateOfBirth, String gender, ArrayList<AddressModel> address, String city, String state, String phoneNumber, boolean isActive) {
//        this.userId = userId;
//        this.username = username;
//        this.emailAddress = emailAddress;
//        this.fullName = fullName;
//        this.profilePictureUrl = profilePictureUrl;
//        this.dateOfBirth = dateOfBirth;
//        this.gender = gender;
//        this.address = address;
//        this.city = city;
//        this.state = state;
//        this.phoneNumber = phoneNumber;
//        this.isActive = isActive;
//        isPrivacy_modeBool = true;
//        isDeal_notificationBool = true;
//        isAccount_shipping_notificationBool = true;
//    }

    public UserinfoModels(String token,String userId, String username, String phoneNumber, boolean isActive) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        isPrivacy_modeBool = true;
        isDeal_notificationBool = true;
        isAccount_shipping_notificationBool = true;
    }

//    public UserinfoModels(String token,String userId, String username, String emailAddress, String profilePictureUrl, String phoneNumber, boolean isActive) {
//        this.token = token;
//        this.userId = userId;
//        this.username = username;
//        this.emailAddress = emailAddress;
//        this.profilePictureUrl = profilePictureUrl;
//        this.phoneNumber = phoneNumber;
//        this.isActive = isActive;
//        isPrivacy_modeBool = true;
//        isDeal_notificationBool = true;
//        isAccount_shipping_notificationBool = true;
//    }
//    public UserinfoModels(String token,String userId, String username, String emailAddress, String phoneNumber, boolean isActive) {
//        this.token = token;
//        this.userId = userId;
//        this.username = username;
//        this.emailAddress = emailAddress;
//        this.phoneNumber = phoneNumber;
//        this.isActive = isActive;
//        isPrivacy_modeBool = true;
//        isDeal_notificationBool = true;
//        isAccount_shipping_notificationBool = true;
//    }
//
//    public UserinfoModels(String token,String userId, String username, String emailAddress, String profilePictureUrl, String gender, ArrayList<AddressModel> address, String phoneNumber, boolean isActive) {
//        this.token = token;
//        this.userId = userId;
//        this.username = username;
//        this.emailAddress = emailAddress;
//        this.profilePictureUrl = profilePictureUrl;
//        this.gender = gender;
//        this.address = address;
//        this.phoneNumber = phoneNumber;
//        this.isActive = isActive;
//        isPrivacy_modeBool = true;
//        isDeal_notificationBool = true;
//        isAccount_shipping_notificationBool = true;
//    }
    // Constructor for reading data from a Parcel
    protected UserinfoModels(Parcel in) {
        token = in.readString();
        userId = in.readString();
        username = in.readString();
        emailAddress = in.readString();
        fullName = in.readString();
        profilePictureUrl = in.readString();
        dateOfBirth = in.readString();
        gender = in.readString();
        address = in.createTypedArrayList(AddressModel.CREATOR);
        city = in.readString();
        state = in.readString();
        phoneNumber = in.readString();
        isActive = in.readByte() != 0;
        isPrivacy_modeBool = in.readByte() != 0;
        isDeal_notificationBool = in.readByte() != 0;
        isAccount_shipping_notificationBool = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(emailAddress);
        dest.writeString(fullName);
        dest.writeString(profilePictureUrl);
        dest.writeString(dateOfBirth);
        dest.writeString(gender);
        dest.writeTypedList(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (isPrivacy_modeBool ? 1 : 0));
        dest.writeByte((byte) (isDeal_notificationBool ? 1 : 0));
        dest.writeByte((byte) (isAccount_shipping_notificationBool ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable CREATOR field
    public static final Parcelable.Creator<UserinfoModels> CREATOR = new Parcelable.Creator<UserinfoModels>() {
        @Override
        public UserinfoModels createFromParcel(Parcel in) {
            return new UserinfoModels(in);
        }

        @Override
        public UserinfoModels[] newArray(int size) {
            return new UserinfoModels[size];
        }
    };

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<AddressModel> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<AddressModel> address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPrivacy_modeBool() {
        return isPrivacy_modeBool;
    }

    public void setPrivacy_modeBool(boolean privacy_modeBool) {
        isPrivacy_modeBool = privacy_modeBool;
    }

    public boolean isDeal_notificationBool() {
        return isDeal_notificationBool;
    }

    public void setDeal_notificationBool(boolean deal_notificationBool) {
        isDeal_notificationBool = deal_notificationBool;
    }

    public boolean isAccount_shipping_notificationBool() {
        return isAccount_shipping_notificationBool;
    }

    public void setAccount_shipping_notificationBool(boolean account_shipping_notificationBool) {
        isAccount_shipping_notificationBool = account_shipping_notificationBool;
    }
}
