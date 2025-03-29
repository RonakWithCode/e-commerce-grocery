package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

// QueryModel.java
@Keep
public class QueryModel {
    private String UserId;
    private String name;
    private String email;
    private String subject;
    private String message;
    private String deviceInfo;

    // Add constructors, getters, and setters

    public QueryModel() {
        // Default constructor required for calls to DataSnapshot.getValue(QueryModel.class)
    }

    public QueryModel(String userId,String name, String email, String subject, String message, String deviceInfo) {
        this.UserId = userId;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.deviceInfo = deviceInfo;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
