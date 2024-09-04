package com.example.rentalhousing;

import com.google.gson.annotations.SerializedName;

public class SearchResult {

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("address")
    private Address address; // Add a field for address details

    public String getDisplayName() {
        return displayName;
    }

    public Address getAddress() {
        return address;
    }

    // Inner class to represent the address details
    public static class Address {
        @SerializedName("road")
        private String road;

        @SerializedName("suburb")
        private String suburb;

        @SerializedName("city")
        private String city;

        @SerializedName("state")
        private String state;

        @SerializedName("country")
        private String country;

        @SerializedName("postcode")
        private String postcode;

        public String getRoad() {
            return road;
        }

        public String getSuburb() {
            return suburb;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public String getPostcode() {
            return postcode;
        }
    }
}
