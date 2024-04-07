package com.crazyostudio.ecommercegrocery.Model;

public class BannerModels {
    private String BannerGoto;
    private String BannerUrl;
    private String BannerCaption;


    public BannerModels() {
    }

    public BannerModels(String bannerGoto, String bannerUrl, String bannerCaption) {
        BannerGoto = bannerGoto;
        BannerUrl = bannerUrl;
        BannerCaption = bannerCaption;
    }

    public String getBannerGoto() {
        return BannerGoto;
    }

    public void setBannerGoto(String bannerGoto) {
        BannerGoto = bannerGoto;
    }

    public String getBannerUrl() {
        return BannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        BannerUrl = bannerUrl;
    }

    public String getBannerCaption() {
        return BannerCaption;
    }

    public void setBannerCaption(String bannerCaption) {
        BannerCaption = bannerCaption;
    }
}
