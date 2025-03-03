package com.ronosoft.alwarmart.Model;



import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import java.util.ArrayList;


public class ProductModel implements Parcelable {
    // Base fields
    private boolean isAvailable;
    private String productId;
    private String productName;
    private String productDescription;
    private String brand;
    private String category;
    private String subCategory;
    private double price;
    private double mrp;
    private double purchasePrice;
    private double discount;
    private String discountType;
    private int stockCount;
    private int minSelectableQuantity;
    private int maxSelectableQuantity;
    private int selectableQuantity;
    private String weight;
    private String weightSIUnit;
    private String productType;
    private String productIsFoodItem;
    private String time;
    private ArrayList<String> keywords;
    private ArrayList<String> productImage;
    @Nullable
    private ArrayList<Variations> variations;
    private String barcode;
    private String productLayoutType;

    // Layout-specific fields
    // Cloth
    private String fabric;
    private String fit;
    private String color;
    // Shoes
    private String material;
    private String closureType;
    private String style;
    private String availableColor;
    // Grocery
    private ArrayList<License> licenses;
    private String productShelfLife;
    private String productPackagingDetails;

    // License inner class
    public static class License implements Parcelable {
        private String name;
        private String code;

        public License(){}

        public License(String name, String code) {
            this.name = name;
            this.code = code;
        }

        protected License(Parcel in) {
            name = in.readString();
            code = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(code);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<License> CREATOR = new Creator<License>() {
            @Override
            public License createFromParcel(Parcel in) {
                return new License(in);
            }

            @Override
            public License[] newArray(int size) {
                return new License[size];
            }
        };


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // Constants
//    Cloth: "Cloth",
//    Shoes: "Shoes",
//    Grocery: "Grocery"
    public static final String LAYOUT_TYPE_CLOTH = "Cloth";
    public static final String LAYOUT_TYPE_SHOES = "Shoes";
    public static final String LAYOUT_TYPE_GROCERY = "Grocery";

    public static final String DISCOUNT_TYPE_PERCENTAGE = "Percentage";
    public static final String DISCOUNT_TYPE_FLAT = "Flat Amount";
    public static final String DISCOUNT_TYPE_NO_DISCOUNT = "No Discount";

    // Default constructor
    public ProductModel() {}

    // Parcelable implementation
    protected ProductModel(Parcel in) {
        isAvailable = in.readByte() != 0;
        productId = in.readString();
        productName = in.readString();
        productDescription = in.readString();
        brand = in.readString();
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
        productType = in.readString();
        productIsFoodItem = in.readString();
        keywords = in.createStringArrayList();
        productImage = in.createStringArrayList();
        variations = in.createTypedArrayList(Variations.CREATOR);
        barcode = in.readString();
        productLayoutType = in.readString();
        time = in.readString();
        // Layout-specific fields
        fabric = in.readString();
        fit = in.readString();
        color = in.readString();
        material = in.readString();
        closureType = in.readString();
        style = in.readString();
        availableColor = in.readString();
        licenses = in.createTypedArrayList(License.CREATOR);
        productShelfLife = in.readString();
        productPackagingDetails = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeString(brand);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(time);
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
        dest.writeString(productType);
        dest.writeString(productIsFoodItem);
        dest.writeStringList(keywords);
        dest.writeStringList(productImage);
        dest.writeTypedList(variations);
        dest.writeString(barcode);
        dest.writeString(productLayoutType);

        // Layout-specific fields
        dest.writeString(fabric);
        dest.writeString(fit);
        dest.writeString(color);
        dest.writeString(material);
        dest.writeString(closureType);
        dest.writeString(style);
        dest.writeString(availableColor);
        dest.writeTypedList(licenses);
        dest.writeString(productShelfLife);
        dest.writeString(productPackagingDetails);
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


    public boolean isAvailable() {
        return isAvailable;
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
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public int getMinSelectableQuantity() {
        return minSelectableQuantity;
    }

    public void setMinSelectableQuantity(int minSelectableQuantity) {
        this.minSelectableQuantity = minSelectableQuantity;
    }

    public int getMaxSelectableQuantity() {
        return maxSelectableQuantity;
    }

    public void setMaxSelectableQuantity(int maxSelectableQuantity) {
        this.maxSelectableQuantity = maxSelectableQuantity;
    }

    public int getSelectableQuantity() {
        return selectableQuantity;
    }

    public void setSelectableQuantity(int selectableQuantity) {
        this.selectableQuantity = selectableQuantity;
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
        return productImage;
    }

    public void setProductImage(ArrayList<String> productImage) {
        this.productImage = productImage;
    }

    @Nullable
    public ArrayList<Variations> getVariations() {
        return variations;
    }

    public void setVariations(@Nullable ArrayList<Variations> variations) {
        this.variations = variations;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductLayoutType() {
        return productLayoutType;
    }

    public void setProductLayoutType(String productLayoutType) {
        this.productLayoutType = productLayoutType;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getFit() {
        return fit;
    }

    public void setFit(String fit) {
        this.fit = fit;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getClosureType() {
        return closureType;
    }

    public void setClosureType(String closureType) {
        this.closureType = closureType;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAvailableColor() {
        return availableColor;
    }

    public void setAvailableColor(String availableColor) {
        this.availableColor = availableColor;
    }


    public ArrayList<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(ArrayList<License> licenses) {
        this.licenses = licenses;
    }

    public String getProductShelfLife() {
        return productShelfLife;
    }

    public void setProductShelfLife(String productShelfLife) {
        this.productShelfLife = productShelfLife;
    }

    public String getProductPackagingDetails() {
        return productPackagingDetails;
    }

    public void setProductPackagingDetails(String productPackagingDetails) {
        this.productPackagingDetails = productPackagingDetails;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}