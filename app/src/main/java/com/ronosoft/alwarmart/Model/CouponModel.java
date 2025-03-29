package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

@Keep
public class CouponModel {
    String couponCode;
    boolean active;
    double couponValue;
    int totalUse;
    double minOderValue;

    public CouponModel(){}

    public CouponModel(String couponCode, boolean active, double couponValue, int totalUse, double minOderValue) {
        this.couponCode = couponCode;
        this.active = active;
        this.couponValue = couponValue;
        this.totalUse = totalUse;
        this.minOderValue = minOderValue;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }



    public double getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(double couponValue) {
        this.couponValue = couponValue;
    }

    public int getTotalUse() {
        return totalUse;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTotalUse(int totalUse) {
        this.totalUse = totalUse;
    }

    public double getMinOderValue() {
        return minOderValue;
    }

    public void setMinOderValue(double minOderValue) {
        this.minOderValue = minOderValue;
    }
}
