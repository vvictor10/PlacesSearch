package com.grace.placessearch.common

import com.grace.placessearch.BuildConfig
import com.grace.placessearch.common.util.DecimalByteUnit

/**
 * Created by vicsonvictor on 4/21/18.
 */

object PlacesSearchConstants {

    val HTTP = "http"
    val HTTP_TIMEOUT_VALUE = 10

    val HEART_CROSS_FADE_ANIMATION_DURATION = 150

    // Disk cache size used for Picasso - set to 50MB for now.
    val IMAGE_DISK_CACHE_SIZE = DecimalByteUnit.MEGABYTES.toBytes(50).toInt()

    val CACHE_KEY_TRENDING_VENUES = "TrendingVenuesCache"

    val USER_LOCATION_LAT = BuildConfig.USER_LOCATION_LAT.toDouble()
    val USER_LOCATION_LNG = BuildConfig.USER_LOCATION_LNG.toDouble()

    val MAP_PINS_EXTRA = "MapPinsExtra"

    val VENUE_NAME_EXTRA = "VenueNameExtra"
    val VENUE_ID_EXTRA = "VenueIdExtra"
    val VENUE_POSITION_EXTRA = "VenuePositionExtra"
    val VENUE_IS_FAVORITE = "IsFavoriteExtra"
}
