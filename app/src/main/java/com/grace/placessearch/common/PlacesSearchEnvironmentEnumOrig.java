package com.grace.placessearch.common;

@Deprecated
public enum PlacesSearchEnvironmentEnumOrig {

    PROD("https://api.foursquare.com/v2/"),
    STAGING("https://api.foursquare.com/v2/"); // TODO: place holder, please update with env details.

    public String placesBaseUrl;

    PlacesSearchEnvironmentEnumOrig(String placesBaseUrl) {
        this.placesBaseUrl = placesBaseUrl;
    }
}
