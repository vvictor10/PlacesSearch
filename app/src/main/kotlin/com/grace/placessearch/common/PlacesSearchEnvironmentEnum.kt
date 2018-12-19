package com.grace.placessearch.common

enum class PlacesSearchEnvironmentEnum private constructor(// TODO: place holder, please update with env details.

        var placesBaseUrl: String) {

    PROD("https://api.foursquare.com/v2/"),
    STAGING("https://api.foursquare.com/v2/")
}
