package com.crazyostudio.ecommercegrocery.Model;



public class BannerModels {

    private boolean isActive;
    private String BannerCaption;
    private String BannerImages;
    private String BannerImagesBg;
    private Boolean isFilterByCategory; // if this true filter by category else show select products by admin
    private String Query; // if isFilterByCategory is true we perform the query for find by category else show the the list that admin create the list
    private String position; // eg TOP, Center , Bottom
    private String BannerId;



    public BannerModels() {}

    public BannerModels(boolean isActive, String bannerCaption, String bannerImages, String bannerImagesBg, Boolean isFilterByCategory, String query, String position, String bannerId) {
        this.isActive = isActive;
        BannerCaption = bannerCaption;
        BannerImages = bannerImages;
        BannerImagesBg = bannerImagesBg;
        this.isFilterByCategory = isFilterByCategory;
        Query = query;
        this.position = position;
        BannerId = bannerId;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public String getBannerImagesBg() {
        return BannerImagesBg;
    }

    public void setBannerImagesBg(String bannerImagesBg) {
        BannerImagesBg = bannerImagesBg;
    }

    public Boolean getFilterByCategory() {
        return isFilterByCategory;
    }

    public void setFilterByCategory(Boolean filterByCategory) {
        isFilterByCategory = filterByCategory;
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        Query = query;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBannerId() {
        return BannerId;
    }

    public void setBannerId(String bannerId) {
        BannerId = bannerId;
    }
}
