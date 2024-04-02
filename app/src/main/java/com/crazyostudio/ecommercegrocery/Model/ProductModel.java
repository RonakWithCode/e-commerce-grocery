package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProductModel implements Parcelable {
    private String productId;
    private String productName;
    private String productDescription;
    private String Brand;
    private String category;
    private double price;
    private double mrp;
    private String unit;
    private String SubUnit; // Unit of measurement (e.g., kg, gram, litre, pack)
    private String ProductType; // Unit of measurement (e.g., kg, gram, litre, pack)
    private int DefaultQuantity; // Quantity available
    private int quantity;
    private ArrayList<String> imageURL; // URL of the product image
    private boolean isAvailable; // Indicates whether the product is available or not
    // Sales data
    private int totalSales;
    private String EditDate;
    private String whoEdit;

    private ArrayList<String> keywords;

    public ProductModel() {}

    public ProductModel(@Nullable String productId, String productName, String productDescription, String category, String ProductType, double price, double mrp, String unit, String subUnit, int defaultQuantity, int quantity, ArrayList<String> imageURL, boolean isAvailable, int totalSales, String EditDate, String whoEdit,ArrayList<String> keywords,String Brand) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.category = category;
        this.ProductType = ProductType;
        this.price = price;
        this.mrp = mrp;
        this.unit = unit;
        SubUnit = subUnit;
        DefaultQuantity = defaultQuantity;
        this.quantity = quantity;
        this.imageURL = imageURL;
        this.isAvailable = isAvailable;
        this.totalSales = totalSales;
        this.EditDate = EditDate;
        this.whoEdit = whoEdit;
        this.keywords = keywords;
        this.Brand = Brand;
    }

    protected ProductModel(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        productDescription = in.readString();
        category = in.readString();
        ProductType = in.readString();
        price = in.readDouble();
        mrp = in.readDouble();
        unit = in.readString();
        SubUnit = in.readString();
        DefaultQuantity = in.readInt();
        quantity = in.readInt();
        imageURL = in.createStringArrayList();
        isAvailable = in.readByte() != 0;
        totalSales = in.readInt();
        EditDate = in.readString();
        whoEdit = in.readString();
        keywords = in.createStringArrayList();
        Brand = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getEditDate() {
        return EditDate;
    }

    public void setEditDate(String editDate) {
        EditDate = editDate;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSubUnit() {
        return SubUnit;
    }

    public void setSubUnit(String subUnit) {
        SubUnit = subUnit;
    }

    public int getDefaultQuantity() {
        return DefaultQuantity;
    }

    public void setDefaultQuantity(int defaultQuantity) {
        DefaultQuantity = defaultQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<String> getImageURL() {
        return imageURL;
    }


    public void setImageURL(ArrayList<String> imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public String getWhoEdit() {
        return whoEdit;
    }

    public void setWhoEdit(String whoEdit) {
        this.whoEdit = whoEdit;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeString(category);
        dest.writeString(ProductType);
        dest.writeDouble(price);
        dest.writeDouble(mrp);
        dest.writeString(unit);
        dest.writeString(SubUnit);
        dest.writeInt(DefaultQuantity);
        dest.writeInt(quantity);
        dest.writeStringList(imageURL);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeInt(totalSales);
        dest.writeString(EditDate);
        dest.writeString(whoEdit);
        dest.writeStringList(keywords);
        dest.writeString(Brand);
    }
}