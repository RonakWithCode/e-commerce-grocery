package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.Date;
@Keep

public class OrderModel implements Parcelable {
    private String orderId;
    private Customer customer;
    private ArrayList<ShoppingCartsProductModel> orderItems;
    private double orderTotalPrice; // full value of order also say grand total
    private String CouponCode;
    private double CouponCodeValue;
    private String orderStatus;
    private Payment payment;
    private Shipping shipping;
    private Date orderDate;
    private String notes;
    private String token;
    private double Donate; ;
    private double ProcessingFees;
    private Gift mainGift;
//    private Date Date;
    public OrderModel() {
    }

    public OrderModel(String orderId, Customer customer, ArrayList<ShoppingCartsProductModel> orderItems, double orderTotalPrice, String couponCode, String orderStatus, Payment payment, Shipping shipping, Date orderDate, String notes, String token,double CouponCodeValue,double Donate,double ProcessingFees,Gift mainGift) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderItems = orderItems;
        this.orderTotalPrice = orderTotalPrice;
        this.CouponCode = couponCode;
        this.orderStatus = orderStatus;
        this.payment = payment;
        this.shipping = shipping;
        this.orderDate = orderDate;
        this.notes = notes;
        this.token = token;
        this.CouponCodeValue =CouponCodeValue;
        this.Donate = Donate;
        this.ProcessingFees = ProcessingFees;
        this.mainGift = mainGift;

    }


    protected OrderModel(Parcel in) {
        orderId = in.readString();
        orderItems = in.createTypedArrayList(ShoppingCartsProductModel.CREATOR);
        customer = in.readParcelable(Customer.class.getClassLoader());
        orderTotalPrice = in.readDouble();
        CouponCode = in.readString();
        orderStatus = in.readString();
        payment = in.readParcelable(Payment.class.getClassLoader());
        shipping = in.readParcelable(Shipping.class.getClassLoader());
        notes = in.readString();
        token = in.readString();
        CouponCodeValue = in.readDouble();
        Donate = in.readDouble();
        ProcessingFees = in.readDouble();
        mainGift = in.readParcelable(Gift.class.getClassLoader());
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ArrayList<ShoppingCartsProductModel> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<ShoppingCartsProductModel> orderItems) {
        this.orderItems = orderItems;
    }

    public double getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(double orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getCouponCode() {
        return CouponCode;
    }

    public void setCouponCode(String couponCode) {
        CouponCode = couponCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getCouponCodeValue() {
        return CouponCodeValue;
    }

    public void setCouponCodeValue(double couponCodeValue) {
        CouponCodeValue = couponCodeValue;
    }

    public double getDonate() {
        return Donate;
    }

    public void setDonate(double donate) {
        Donate = donate;
    }

    public double getProcessingFees() {
        return ProcessingFees;
    }

    public void setProcessingFees(double processingFees) {
        ProcessingFees = processingFees;
    }

    public Gift getMainGift() {
        return mainGift;
    }

    public void setMainGift(Gift mainGift) {
        this.mainGift = mainGift;
    }


    //

//    public java.util.Date getDate() {
//        return Date;
//    }
//
//    public void setDate(java.util.Date date) {
//        Date = date;
//    }

    @Override
    public int describeContents() {
        return 0;
    }




    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeParcelable(customer, flags);
        dest.writeTypedList(orderItems);
        dest.writeDouble(orderTotalPrice);
        dest.writeString(CouponCode);
        dest.writeString(orderStatus);
        dest.writeParcelable(payment, flags);
        dest.writeParcelable(shipping, flags);
        dest.writeLong(orderDate != null ? orderDate.getTime() : -1);
        dest.writeString(notes);
        dest.writeString(token);
        dest.writeDouble(CouponCodeValue);
        dest.writeDouble(Donate);
        dest.writeDouble(ProcessingFees);
        dest.writeParcelable(mainGift, flags);

    }
}
