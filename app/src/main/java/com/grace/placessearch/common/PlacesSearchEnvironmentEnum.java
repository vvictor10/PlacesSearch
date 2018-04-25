package com.grace.placessearch.common;

public enum PlacesSearchEnvironmentEnum {

    PROD("https://api.foursquare.com/v2/"),
    STAGING("https://api.foursquare.com/v2/"); // TODO: place holder, please update with env details.

    public String placesBaseUrl;

    PlacesSearchEnvironmentEnum(String placesBaseUrl) {
        this.placesBaseUrl = placesBaseUrl;
    }
}
