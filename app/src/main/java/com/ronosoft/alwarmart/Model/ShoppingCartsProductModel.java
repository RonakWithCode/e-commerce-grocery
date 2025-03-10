package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShoppingCartsProductModel implements Parcelable {

    private boolean isAvailable;
    private String productId;
    private String productName;
    private String productDescription;
    private String Brand;
    private String category;
    private String subCategory;
    private double price;
    private double mrp;
    private double purchasePrice;     // New field
    private double discount;
    private String discountType;      // New field (e.g., "Percentage", "Flat Amount")
    private int stockCount;
    private int minSelectableQuantity;
    private int maxSelectableQuantity;
    private int selectableQuantity;
    private String weight;
    private String weightSIUnit;
    private String productLife;
    private String productType;
    private String productIsFoodItem;
    private String time;              // New field (e.g., delivery time)
    private String productLayoutType; // New field (e.g., "Shoes", "Cloth", "Grocery")
    private String barcode;           // New field
    private ArrayList<String> keywords;
    private ArrayList<String> ProductImage;
    @Nullable
    private ArrayList<Variations> variations;

    public ShoppingCartsProductModel() {
    }

    public ShoppingCartsProductModel(boolean isAvailable, String productId, String productName, String productDescription, String brand, String category, String subCategory,
                                     double price, double mrp, double purchasePrice, double discount, String discountType,
                                     int stockCount, int minSelectableQuantity, int maxSelectableQuantity, int selectableQuantity,
                                     String weight, String weightSIUnit, String productLife, String productType, String productIsFoodItem,
                                     String time, String productLayoutType, String barcode,
                                     ArrayList<String> keywords, ArrayList<String> productImage, @Nullable ArrayList<Variations> variations) {
        this.isAvailable = isAvailable;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.Brand = brand;
        this.category = category;
        this.subCategory = subCategory;
        this.price = price;
        this.mrp = mrp;
        this.purchasePrice = purchasePrice;
        this.discount = discount;
        this.discountType = discountType;
        this.stockCount = stockCount;
        this.minSelectableQuantity = minSelectableQuantity;
        this.maxSelectableQuantity = maxSelectableQuantity;
        this.selectableQuantity = selectableQuantity;
        this.weight = weight;
        this.weightSIUnit = weightSIUnit;
        this.productLife = productLife;
        this.productType = productType;
        this.productIsFoodItem = productIsFoodItem;
        this.time = time;
        this.productLayoutType = productLayoutType;
        this.barcode = barcode;
        this.keywords = keywords;
        this.ProductImage = productImage;
        this.variations = variations;
    }

    protected ShoppingCartsProductModel(Parcel in) {
        isAvailable = in.readByte() != 0;
        productId = in.readString();
        productName = in.readString();
        productDescription = in.readString();
        Brand = in.readString();
        category = in.readString();
        subCategory = in.readString();
        price = in.readDouble();
        mrp = in.readDouble();
        purchasePrice = in.readDouble();
        discount = in.readDouble();
        discountType = in.readString();
        stockCount = in.readInt();
        minSelectableQuantity = in.readInt();
        maxSelectableQuantity = in.readInt();
        selectableQuantity = in.readInt();
        weight = in.readString();
        weightSIUnit = in.readString();
        productLife = in.readString();
        productType = in.readString();
        productIsFoodItem = in.readString();
        time = in.readString();
        productLayoutType = in.readString();
        barcode = in.readString();
        keywords = in.createStringArrayList();
        ProductImage = in.createStringArrayList();
        variations = in.createTypedArrayList(Variations.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeString(Brand);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeDouble(price);
        dest.writeDouble(mrp);
        dest.writeDouble(purchasePrice);
        dest.writeDouble(discount);
        dest.writeString(discountType);
        dest.writeInt(stockCount);
        dest.writeInt(minSelectableQuantity);
        dest.writeInt(maxSelectableQuantity);
        dest.writeInt(selectableQuantity);
        dest.writeString(weight);
        dest.writeString(weightSIUnit);
        dest.writeString(productLife);
        dest.writeString(productType);
        dest.writeString(productIsFoodItem);
        dest.writeString(time);
        dest.writeString(productLayoutType);
        dest.writeString(barcode);
        dest.writeStringList(keywords);
        dest.writeStringList(ProductImage);
        dest.writeTypedList(variations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShoppingCartsProductModel> CREATOR = new Creator<ShoppingCartsProductModel>() {
        @Override
        public ShoppingCartsProductModel createFromParcel(Parcel in) {
            return new ShoppingCartsProductModel(in);
        }

        @Override
        public ShoppingCartsProductModel[] newArray(int size) {
            return new ShoppingCartsProductModel[size];
        }
    };

    // Example getters and setters; add others as needed

    public String getProductId() {
        return productId;
    }

    public int getSelectableQuantity() {
        return selectableQuantity;
    }

    public void setSelectableQuantity(int selectableQuantity) {
        this.selectableQuantity = selectableQuantity;
    }

    public double getPrice() {
        return price;
    }

    public int getMinSelectableQuantity() {
        return minSelectableQuantity;
    }

    public int getMaxSelectableQuantity() {
        return maxSelectableQuantity;
    }

    public String getProductIsFoodItem() {
        return productIsFoodItem;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
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

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public void setMinSelectableQuantity(int minSelectableQuantity) {
        this.minSelectableQuantity = minSelectableQuantity;
    }

    public void setMaxSelectableQuantity(int maxSelectableQuantity) {
        this.maxSelectableQuantity = maxSelectableQuantity;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightSIUnit() {
        return weightSIUnit;
    }

    public void setWeightSIUnit(String weightSIUnit) {
        this.weightSIUnit = weightSIUnit;
    }

    public String getProductLife() {
        return productLife;
    }

    public void setProductLife(String productLife) {
        this.productLife = productLife;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setProductIsFoodItem(String productIsFoodItem) {
        this.productIsFoodItem = productIsFoodItem;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProductLayoutType() {
        return productLayoutType;
    }

    public void setProductLayoutType(String productLayoutType) {
        this.productLayoutType = productLayoutType;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getProductImage() {
        return ProductImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        ProductImage = productImage;
    }

    @Nullable
    public ArrayList<Variations> getVariations() {
        return variations;
    }

    public void setVariations(@Nullable ArrayList<Variations> variations) {
        this.variations = variations;
    }


    // Continue adding getters and setters for the remaining fields...
}
