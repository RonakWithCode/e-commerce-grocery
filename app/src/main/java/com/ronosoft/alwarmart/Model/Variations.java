package com.ronosoft.alwarmart.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
@Keep
public class Variations implements Parcelable {
    private String id;
    private String name;
    private String weight;
    private String weightSIUnit;
    private String image;


    public Variations() {}

    public Variations(String id, String name, String weight, String weightSIUnit, String image) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.weightSIUnit = weightSIUnit;
        this.image = image;
    }

    protected Variations(Parcel in) {
        id = in.readString();
        name = in.readString();
        weight = in.readString();
        weightSIUnit = in.readString();
        image = in.readString();
    }

    public static final Creator<Variations> CREATOR = new Creator<Variations>() {
        @Override
        public Variations createFromParcel(Parcel in) {
            return new Variations(in);
        }

        @Override
        public Variations[] newArray(int size) {
            return new Variations[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(weight);
        dest.writeString(weightSIUnit);
        dest.writeString(image);
    }
}
