package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class OrderModel implements Parcelable {
    private String name;
    private String OrderId;
    private ArrayList<ProductModel> ProductModel;
    private String OrderStatus;
    private String Adders;
    private String shipping;
    private String PhoneNumber;
    private double SubTotal,ShippingFee,Total,discount,save;
    private String PaymentStatus;
    private String PaymentType;
    private long OrderTime;
    private String UserId;
    private String token;
    private String Email;


    public OrderModel(){}

    public OrderModel(String Name, String orderId,String shipping ,ArrayList<com.crazyostudio.ecommercegrocery.Model.ProductModel> productModel, String orderStatus, String adders, String phoneNumber, double subTotal, double shippingFee, double total, String paymentStatus, String paymentType, long orderTime, String userId,String token,double discount,double save,String email) {
        name = Name;
        OrderId = orderId;
        this.shipping = shipping;
        ProductModel = productModel;
        OrderStatus = orderStatus;
        Adders = adders;
        PhoneNumber = phoneNumber;
        SubTotal = subTotal;
        ShippingFee = shippingFee;
        Total = total;
        PaymentStatus = paymentStatus;
        PaymentType = paymentType;
        OrderTime = orderTime;
        UserId = userId;
        this.token = token;
        this.discount = discount;
        this.save = save;
        this.Email = email;
    }




    // Constructor for reading data from a Parcel
    protected OrderModel(Parcel in) {
        name = in.readString();
        OrderId = in.readString();
        shipping = in.readString();
        ProductModel = in.createTypedArrayList(com.crazyostudio.ecommercegrocery.Model.ProductModel.CREATOR);
        OrderStatus = in.readString();
        Adders = in.readString();
        PhoneNumber = in.readString();
        SubTotal = in.readDouble();
        ShippingFee = in.readDouble();
        Total = in.readDouble();
        PaymentStatus = in.readString();
        PaymentType = in.readString();
        OrderTime = in.readLong();
        UserId = in.readString();
        token = in.readString();
        this.discount = in.readDouble();
        this.save = in.readDouble();
        this.Email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(OrderId);
        dest.writeString(shipping);
        dest.writeTypedList(ProductModel);
        dest.writeString(OrderStatus);
        dest.writeString(Adders);
        dest.writeString(PhoneNumber);
        dest.writeDouble(SubTotal);
        dest.writeDouble(ShippingFee);
        dest.writeDouble(Total);
        dest.writeString(PaymentStatus);
        dest.writeString(PaymentType);
        dest.writeLong(OrderTime);
        dest.writeString(UserId);
        dest.writeString(token);
        dest.writeDouble(discount);
        dest.writeDouble(save);
        dest.writeString(Email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable CREATOR field
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdders() {
        return Adders;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setAdders(String adders) {
        Adders = adders;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public double getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(double subTotal) {
        SubTotal = subTotal;
    }

    public double getShippingFee() {
        return ShippingFee;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getSave() {
        return save;
    }

    public void setSave(double save) {
        this.save = save;
    }

    public void setShippingFee(double shippingFee) {
        ShippingFee = shippingFee;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public ArrayList<com.crazyostudio.ecommercegrocery.Model.ProductModel> getProductModel() {
        return ProductModel;
    }

    public void setProductModel(ArrayList<com.crazyostudio.ecommercegrocery.Model.ProductModel> productModel) {
        ProductModel = productModel;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public long getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(long orderTime) {
        OrderTime = orderTime;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
