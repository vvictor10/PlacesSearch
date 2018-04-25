package com.grace.placessearch;

/**
 * Created by kenneth.mojica on 5/31/16.
 */
public enum PlacesSearchEnvironmentEnum {

    PROD("https://api.foursquare.com/v2/"),
    STAGING("https://api.foursquare.com/v2/"); // TODO: place holder, please update with env details.

    public String placesBaseUrl;

    PlacesSearchEnvironmentEnum(String placesBaseUrl) {
        this.placesBaseUrl = placesBaseUrl;
    }
}
