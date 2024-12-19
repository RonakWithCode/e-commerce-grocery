package com.crazyostudio.ecommercegrocery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class Variations implements Parcelable {
    private String id;
    private String name;
    private String weightWithSIUnit;

    public Variations() {}

    public Variations(String id, String name, String weightWithSIUnit) {
        this.id = id;
        this.name = name;
        this.weightWithSIUnit = weightWithSIUnit;
    }

    protected Variations(Parcel in) {
        id = in.readString();
        name = in.readString();
        weightWithSIUnit = in.readString();
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

    public String getWeightWithSIUnit() {
        return weightWithSIUnit;
    }

    public void setWeightWithSIUnit(String weightWithSIUnit) {
        this.weightWithSIUnit = weightWithSIUnit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(weightWithSIUnit);
    }
}
