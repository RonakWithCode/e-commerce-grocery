package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class ProductModel implements Parcelable {
    private boolean isAvailable; //
    private String productId; // id
    private String productName;
    private String productDescription;
    private String Brand;
    private String category;
    private String subCategory;
    private double price;
    private double mrp;
    private double discount; // this discount on product
    private int stockCount; // Stock
    private int minSelectableQuantity; // number of min select quantity of product
    private int MaxSelectableQuantity; // number of max select quantity of product
    private int SelectableQuantity; // number of max select quantity of product
    private String weight;
    private String weightSIUnit;
    private String productLife; // product life, expiry date
    private String productType; // productType is eg home , etc
    private String productIsFoodItem; // is veg or non veg or something else
    private ArrayList<String> keywords; // for SEO
    private ArrayList<String> ProductImage;
    @Nullable
    private ArrayList<Variations> variations;

    private String barcode;


    public ProductModel() {}

    public ProductModel(boolean isAvailable, String productId, String productName, String productDescription, String brand, String category, String subCategory, double price, double mrp, double discount, int stockCount, int minSelectableQuantity, int maxSelectableQuantity, String weight, String weightSIUnit, String productLife, String productType, String productIsFoodItem, ArrayList<String> keywords, ArrayList<String> productImage, @Nullable ArrayList<Variations> variations) {
        this.isAvailable = isAvailable;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        Brand = brand;
        this.category = category;
        this.subCategory = subCategory;
        this.price = price;
        this.mrp = mrp;
        this.discount = discount;
        this.stockCount = stockCount;
        this.minSelectableQuantity = minSelectableQuantity;
        MaxSelectableQuantity = maxSelectableQuantity;
        this.SelectableQuantity = minSelectableQuantity;
        this.weight = weight;
        this.weightSIUnit = weightSIUnit;
        this.productLife = productLife;
        this.productType = productType;
        this.productIsFoodItem = productIsFoodItem;
        this.keywords = keywords;
        ProductImage = productImage;
        this.variations = variations;
    }

    protected ProductModel(Parcel in) {
        isAvailable = in.readByte() != 0;
        productId = in.readString();
        productName = in.readString();
        productDescription = in.readString();
        Brand = in.readString();
        category = in.readString();
        subCategory = in.readString();
        price = in.readDouble();
        mrp = in.readDouble();
        discount = in.readDouble();
        stockCount = in.readInt();
        minSelectableQuantity = in.readInt();
        MaxSelectableQuantity = in.readInt();
        SelectableQuantity = in.readInt();
        weight = in.readString();
        weightSIUnit = in.readString();
        productLife = in.readString();
        productType = in.readString();
        productIsFoodItem = in.readString();
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
        dest.writeDouble(discount);
        dest.writeInt(stockCount);
        dest.writeInt(minSelectableQuantity);
        dest.writeInt(MaxSelectableQuantity);
        dest.writeInt(SelectableQuantity);
        dest.writeString(weight);
        dest.writeString(weightSIUnit);
        dest.writeString(productLife);
        dest.writeString(productType);
        dest.writeString(productIsFoodItem);
        dest.writeStringList(keywords);
        dest.writeStringList(ProductImage);
        dest.writeTypedList(variations);

    }

    @Override
    public int describeContents() {
        return 0;
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

    // Getter and Setter methods

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getSelectableQuantity() {
        return SelectableQuantity;
    }

    public void setSelectableQuantity(int selectableQuantity) {
        SelectableQuantity = selectableQuantity;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public int getMinSelectableQuantity() {
        return minSelectableQuantity;
    }

    public void setMinSelectableQuantity(int minSelectableQuantity) {
        this.minSelectableQuantity = minSelectableQuantity;
    }

    public int getMaxSelectableQuantity() {
        return MaxSelectableQuantity;
    }

    public void setMaxSelectableQuantity(int maxSelectableQuantity) {
        MaxSelectableQuantity = maxSelectableQuantity;
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

    public String getProductIsFoodItem() {
        return productIsFoodItem;
    }

    public void setProductIsFoodItem(String productIsFoodItem) {
        this.productIsFoodItem = productIsFoodItem;
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

    public ArrayList<Variations> getVariations() {
        return variations;
    }

    public void setVariations(ArrayList<Variations> variations) {
        this.variations = variations;
    }



}