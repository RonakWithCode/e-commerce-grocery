package com.ronosoft.alwarmart.Model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SponsorTypeModel implements Parcelable {

    private String SponsorHomeType;
    private String SponsorSerachType ;
    private String SponsorRecommendationType;


    public SponsorTypeModel() {
    }

    public SponsorTypeModel(String sponsorHomeType, String sponsorSerachType, String sponsorRecommendationType) {
        SponsorHomeType = sponsorHomeType;
        SponsorSerachType = sponsorSerachType;
        SponsorRecommendationType = sponsorRecommendationType;
    }

    protected SponsorTypeModel(Parcel in) {
        SponsorHomeType = in.readString();
        SponsorSerachType = in.readString();
        SponsorRecommendationType = in.readString();
    }

    public static final Creator<SponsorTypeModel> CREATOR = new Creator<SponsorTypeModel>() {
        @Override
        public SponsorTypeModel createFromParcel(Parcel in) {
            return new SponsorTypeModel(in);
        }

        @Override
        public SponsorTypeModel[] newArray(int size) {
            return new SponsorTypeModel[size];
        }
    };

    public String getSponsorHomeType() {
        return SponsorHomeType;
    }

    public void setSponsorHomeType(String sponsorHomeType) {
        SponsorHomeType = sponsorHomeType;
    }

    public String getSponsorSerachType() {
        return SponsorSerachType;
    }

    public void setSponsorSerachType(String sponsorSerachType) {
        SponsorSerachType = sponsorSerachType;
    }

    public String getSponsorRecommendationType() {
        return SponsorRecommendationType;
    }

    public void setSponsorRecommendationType(String sponsorRecommendationType) {
        SponsorRecommendationType = sponsorRecommendationType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(SponsorHomeType);
        dest.writeString(SponsorSerachType);
        dest.writeString(SponsorRecommendationType);
    }
}
