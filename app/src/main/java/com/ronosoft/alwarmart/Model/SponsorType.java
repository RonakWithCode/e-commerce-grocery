package com.ronosoft.alwarmart.Model;

import androidx.annotation.Keep;

@Keep

public class SponsorType {
    private String priorityLevel;
    private String type;

    public SponsorType(){}


    public SponsorType(String priorityLevel, String type) {
        this.priorityLevel = priorityLevel;
        this.type = type;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
