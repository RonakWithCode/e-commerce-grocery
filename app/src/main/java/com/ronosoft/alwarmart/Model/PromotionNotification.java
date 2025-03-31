package com.ronosoft.alwarmart.Model;

import java.util.List;

public class PromotionNotification {
    private String id;              // Unique ID for the notification
    private String title;           // e.g., "Diwali Sale"
    private String description;     // e.g., "Up to 50% off on festive items!"
    private String eventType;       // e.g., "Festival", "Event", "Sale"
    private List<String> categories;// Categories to promote (e.g., ["Sweets", "Decor"])
    private long startTime;         // Start timestamp (in milliseconds)
    private long endTime;           // End timestamp (in milliseconds)
    private boolean isActive;       // Whether the notification is currently active
    private String bannerImageUrl;  // URL for a promotional banner image
    private String actionButtonText;// e.g., "Shop Now"
    private String actionButtonUrl; // Optional URL or category filter for action

    // Default constructor for Firestore
    public PromotionNotification() {}

    public PromotionNotification(String id, String title, String description, String eventType,
                                 List<String> categories, long startTime, long endTime, boolean isActive,
                                 String bannerImageUrl, String actionButtonText, String actionButtonUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.categories = categories;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
        this.bannerImageUrl = bannerImageUrl;
        this.actionButtonText = actionButtonText;
        this.actionButtonUrl = actionButtonUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }
    public String getActionButtonText() { return actionButtonText; }
    public void setActionButtonText(String actionButtonText) { this.actionButtonText = actionButtonText; }
    public String getActionButtonUrl() { return actionButtonUrl; }
    public void setActionButtonUrl(String actionButtonUrl) { this.actionButtonUrl = actionButtonUrl; }
}