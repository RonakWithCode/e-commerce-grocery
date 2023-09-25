package com.crazyostudio.ecommercegrocery.Model;

import java.util.ArrayList;

public class UserinfoModels {
    private String userId;
    private String username;
    private String emailAddress;
    private String fullName;
    private String profilePictureUrl;
    private String dateOfBirth;
    private String gender;
    private ArrayList<String> address;
    private String city;
    private String state;
    private String phoneNumber;
    private boolean isActive;

    public UserinfoModels(){}
    public UserinfoModels(String userId, String username, String emailAddress, String fullName, String profilePictureUrl, String dateOfBirth, String gender, ArrayList<String> address, String city, String state, String phoneNumber, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.fullName = fullName;
        this.profilePictureUrl = profilePictureUrl;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
    }

    public UserinfoModels(String userId, String username, String emailAddress, String profilePictureUrl, String phoneNumber, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.profilePictureUrl = profilePictureUrl;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
    }
    public UserinfoModels(String userId, String username, String emailAddress, String phoneNumber, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
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

    public ArrayList<String> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<String> address) {
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
}
