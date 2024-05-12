package com.crazyostudio.ecommercegrocery.Model;

public class BannerModels {

    private boolean isLive;
    private String BannerCaption;
    private String BannerImages;
    private String DarkBannerImage;
    private String BannerBg;
    private String DarkBannerBg;

    private boolean byCategory; // if this true filter by category else show select products

    private String filterQuery;

    private String position;


    public BannerModels() {}

    public BannerModels(boolean isLive, String bannerCaption, String bannerImages, String darkBannerImage, String bannerBg, String darkBannerBg, boolean byCategory, String filterQuery,String position) {
        this.isLive = isLive;
        BannerCaption = bannerCaption;
        BannerImages = bannerImages;
        DarkBannerImage = darkBannerImage;
        BannerBg = bannerBg;
        DarkBannerBg = darkBannerBg;
        this.byCategory = byCategory;
        this.filterQuery = filterQuery;
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getBannerCaption() {
        return BannerCaption;
    }

    public void setBannerCaption(String bannerCaption) {
        BannerCaption = bannerCaption;
    }

    public String getBannerImages() {
        return BannerImages;
    }

    public void setBannerImages(String bannerImages) {
        BannerImages = bannerImages;
    }

    public String getDarkBannerImage() {
        return DarkBannerImage;
    }

    public void setDarkBannerImage(String darkBannerImage) {
        DarkBannerImage = darkBannerImage;
    }

    public String getBannerBg() {
        return BannerBg;
    }

    public void setBannerBg(String bannerBg) {
        BannerBg = bannerBg;
    }

    public String getDarkBannerBg() {
        return DarkBannerBg;
    }

    public void setDarkBannerBg(String darkBannerBg) {
        DarkBannerBg = darkBannerBg;
    }

    public boolean isByCategory() {
        return byCategory;
    }

    public void setByCategory(boolean byCategory) {
        this.byCategory = byCategory;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }
}
