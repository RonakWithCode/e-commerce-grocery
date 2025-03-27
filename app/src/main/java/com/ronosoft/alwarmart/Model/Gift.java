package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Gift implements Parcelable {

    private String GiftId;
    private String title;
    private String subtitle;
    private String description;
    private double minOrderValue;
    private String category;
    private boolean active;
    // Nested validity object to map Firebase "validity" node
    private Validity validity;
    private Integer stockLimit;      // Optional: Maximum available stock
    private String terms;
    private int priority;
    private Integer maxRedemptions;  // Optional: Limit on the number of redemptions
    private String eligibleUserGroups; // E.g., "First Order", "Second Order", "All Customers"
    private String imageUrl;
    private String createdAt;
    private String updatedAt;

    // No-argument constructor (required for Firebase)
    public Gift() {
    }

    public Gift(String title, String subtitle, String description, double minOrderValue, String category, boolean active,
                Validity validity, Integer stockLimit, String terms, int priority, Integer maxRedemptions,
                String eligibleUserGroups, String imageUrl, String createdAt, String updatedAt,String GiftId) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.minOrderValue = minOrderValue;
        this.category = category;
        this.active = active;
        this.validity = validity;
        this.stockLimit = stockLimit;
        this.terms = terms;
        this.priority = priority;
        this.maxRedemptions = maxRedemptions;
        this.eligibleUserGroups = eligibleUserGroups;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.GiftId = GiftId;
    }

    // Nested Validity class
    public static class Validity implements Parcelable {
        private String startDate; // Expected format: "yyyy-MM-dd"
        private String endDate;   // Expected format: "yyyy-MM-dd"

        public Validity() {
        }

        public Validity(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        protected Validity(Parcel in) {
            startDate = in.readString();
            endDate = in.readString();
        }

        public static final Creator<Validity> CREATOR = new Creator<Validity>() {
            @Override
            public Validity createFromParcel(Parcel in) {
                return new Validity(in);
            }

            @Override
            public Validity[] newArray(int size) {
                return new Validity[size];
            }
        };

        public String getStartDate() {
            return startDate;
        }
        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }
        public String getEndDate() {
            return endDate;
        }
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(startDate);
            dest.writeString(endDate);
        }
    }

    // Getters and setters for Gift fields
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(double minOrderValue) { this.minOrderValue = minOrderValue; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Validity getValidity() { return validity; }
    public void setValidity(Validity validity) { this.validity = validity; }

    public Integer getStockLimit() { return stockLimit; }
    public void setStockLimit(Integer stockLimit) { this.stockLimit = stockLimit; }

    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public Integer getMaxRedemptions() { return maxRedemptions; }
    public void setMaxRedemptions(Integer maxRedemptions) { this.maxRedemptions = maxRedemptions; }


    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getGiftId() {
        return GiftId;
    }

    public void setGiftId(String giftId) {
        GiftId = giftId;
    }

    public String getEligibleUserGroups() {
        return eligibleUserGroups;
    }

    public void setEligibleUserGroups(String eligibleUserGroups) {
        this.eligibleUserGroups = eligibleUserGroups;
    }

    /**
     * Checks whether this gift is eligible for a given order.
     * Validity dates are expected to be in the "yyyy-MM-dd" format.
     *
     * @param orderValue    the total value of the order.
     * @param currentTimeMs the current time in milliseconds.
     * @return true if eligible; false otherwise.
     */

    public boolean isEligible(double orderValue, long currentTimeMs) {
        if (!active) return false;
        if (orderValue < minOrderValue) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            if (validity != null && validity.getStartDate() != null && !validity.getStartDate().isEmpty()) {
                long start = sdf.parse(validity.getStartDate()).getTime();
                if (currentTimeMs < start) return false;
            }
            if (validity != null && validity.getEndDate() != null && !validity.getEndDate().isEmpty()) {
                long end = sdf.parse(validity.getEndDate()).getTime();
                if (currentTimeMs > end) return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        // Additional checks for stock or max redemptions can be added here if desired.
        return true;
    }



    // Parcelable implementation
    protected Gift(Parcel in) {
        title = in.readString();
        subtitle = in.readString();
        description = in.readString();
        minOrderValue = in.readDouble();
        category = in.readString();
        active = in.readByte() != 0;
        validity = in.readParcelable(Validity.class.getClassLoader());
        if (in.readByte() == 0) {
            stockLimit = null;
        } else {
            stockLimit = in.readInt();
        }
        terms = in.readString();
        priority = in.readInt();
        if (in.readByte() == 0) {
            maxRedemptions = null;
        } else {
            maxRedemptions = in.readInt();
        }
        eligibleUserGroups = in.readString();
        imageUrl = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        GiftId = in.readString();
    }

    public static final Creator<Gift> CREATOR = new Creator<Gift>() {
        @Override
        public Gift createFromParcel(Parcel in) {
            return new Gift(in);
        }

        @Override
        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(description);
        dest.writeDouble(minOrderValue);
        dest.writeString(category);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeParcelable(validity, flags);
        if (stockLimit == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(stockLimit);
        }
        dest.writeString(terms);
        dest.writeInt(priority);
        if (maxRedemptions == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maxRedemptions);
        }
        dest.writeString(eligibleUserGroups);
        dest.writeString(imageUrl);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(GiftId);
    }
}
