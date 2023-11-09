package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hishd.tinycart.model.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ShoppingCartsProductModel implements Parcelable, Item, Serializable {
        private String id;
        private String ItemName,ItemDescription,ItemUnit;
        private double stock,price,MRP,Discount,Quantity;
        private String Layout,Tag,Category;
        private ArrayList<String> ProductImages;
        private boolean isLive;
        private int TapOn;
        private long EditDate;
        private int SellerOfItem;
        private int SelectProductQuantity;



        public ShoppingCartsProductModel() {} // For Firebase

        public ShoppingCartsProductModel(String Id ,String itemName, String itemDescription, String itemUnit, double stock, double price, double MRP, double discount, double quantity, String layout, String tag, String category, ArrayList<String> productImages, boolean isLive, int tapOn, long editDate, int sellerOfItem,int selectProductQuantity) {
            id = Id;
            ItemName = itemName;
            ItemDescription = itemDescription;
            ItemUnit = itemUnit;
            this.stock = stock;
            this.price = price;
            this.MRP = MRP;
            Discount = discount;
            Quantity = quantity;
            Layout = layout;
            Tag = tag;
            Category = category;
            ProductImages = productImages;
            this.isLive = isLive;
            TapOn = tapOn;
            EditDate = editDate;
            SellerOfItem = sellerOfItem;
            SelectProductQuantity = selectProductQuantity;
        }

        protected ShoppingCartsProductModel(Parcel in) {
            id = in.readString();
            ItemName = in.readString();
            ItemDescription = in.readString();
            ItemUnit = in.readString();
            stock = in.readDouble();
            price = in.readDouble();
            MRP = in.readDouble();
            Discount = in.readDouble();
            Quantity = in.readDouble();
            Layout = in.readString();
            Tag = in.readString();
            Category = in.readString();
            ProductImages = in.createStringArrayList();
            isLive = in.readByte() != 0;
            TapOn = in.readInt();
            EditDate = in.readLong();
            SellerOfItem = in.readInt();
            SelectProductQuantity = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(ItemName);
            dest.writeString(ItemDescription);
            dest.writeString(ItemUnit);
            dest.writeDouble(stock);
            dest.writeDouble(price);
            dest.writeDouble(MRP);
            dest.writeDouble(Discount);
            dest.writeDouble(Quantity);
            dest.writeString(Layout);
            dest.writeString(Tag);
            dest.writeString(Category);
            dest.writeStringList(ProductImages);
            dest.writeByte((byte) (isLive ? 1 : 0));
            dest.writeInt(TapOn);
            dest.writeLong(EditDate);
            dest.writeInt(SellerOfItem);
            dest.writeInt(SelectProductQuantity);
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getSelectProductQuantity() {
            return SelectProductQuantity;
        }

        public void setSelectProductQuantity(int selectProductQuantity) {
            SelectProductQuantity = selectProductQuantity;
        }

    @Override
    public BigDecimal getItemPrice() {
        return BigDecimal.valueOf(price);
    }

    public String getItemName() {
            return ItemName;
        }

        public void setItemName(String itemName) {
            ItemName = itemName;
        }

        public String getItemDescription() {
            return ItemDescription;
        }

        public void setItemDescription(String itemDescription) {
            ItemDescription = itemDescription;
        }

        public String getItemUnit() {
            return ItemUnit;
        }

        public void setItemUnit(String itemUnit) {
            ItemUnit = itemUnit;
        }

        public double getStock() {
            return stock;
        }

        public void setStock(double stock) {
            this.stock = stock;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getMRP() {
            return MRP;
        }

        public void setMRP(double MRP) {
            this.MRP = MRP;
        }

        public double getDiscount() {
            return Discount;
        }

        public void setDiscount(double discount) {
            Discount = discount;
        }

        public double getQuantity() {
            return Quantity;
        }

        public void setQuantity(double quantity) {
            Quantity = quantity;
        }

        public String getLayout() {
            return Layout;
        }

        public void setLayout(String layout) {
            Layout = layout;
        }

        public String getTag() {
            return Tag;
        }

        public void setTag(String tag) {
            Tag = tag;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String category) {
            Category = category;
        }

        public ArrayList<String> getProductImages() {
            return ProductImages;
        }

        public void setProductImages(ArrayList<String> productImages) {
            ProductImages = productImages;
        }

        public boolean isLive() {
            return isLive;
        }

        public void setLive(boolean live) {
            isLive = live;
        }

        public int getTapOn() {
            return TapOn;
        }

        public void setTapOn(int tapOn) {
            TapOn = tapOn;
        }

        public long getEditDate() {
            return EditDate;
        }

        public void setEditDate(long editDate) {
            EditDate = editDate;
        }

        public int getSellerOfItem() {
            return SellerOfItem;
        }

        public void setSellerOfItem(int sellerOfItem) {
            SellerOfItem = sellerOfItem;
        }



    }