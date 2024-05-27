package com.example.rentalhousing;

import com.google.gson.annotations.SerializedName;

public class SearchResult {
    @SerializedName("display_name")
    private String displayName;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lon")
    private String longitude;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}