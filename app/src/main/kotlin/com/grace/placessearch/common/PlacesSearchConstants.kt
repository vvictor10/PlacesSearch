package com.grace.placessearch.common

import com.grace.placessearch.BuildConfig
import com.grace.placessearch.common.util.DecimalByteUnit

/**
 * Created by vicsonvictor on 4/21/18.
 */
object PlacesSearchConstants {

    const val HTTP = "http"
    const val HTTP_TIMEOUT_VALUE = 10

    const val HEART_CROSS_FADE_ANIMATION_DURATION = 150

    // Disk cache size used for Picasso - set to 50MB for now.
    @JvmField
    val IMAGE_DISK_CACHE_SIZE = DecimalByteUnit.MEGABYTES.toBytes(50).toInt()

    const val CACHE_KEY_TRENDING_VENUES = "TrendingVenuesCache"

    @JvmField
    val USER_LOCATION_LAT = BuildConfig.USER_LOCATION_LAT.toDouble()
    @JvmField
    val USER_LOCATION_LNG = BuildConfig.USER_LOCATION_LNG.toDouble()

    const val MAP_PINS_EXTRA = "MapPinsExtra"

    const val VENUE_NAME_EXTRA = "VenueNameExtra"
    const val VENUE_ID_EXTRA = "VenueIdExtra"
    const val VENUE_POSITION_EXTRA = "VenuePositionExtra"
    const val VENUE_IS_FAVORITE = "IsFavoriteExtra"
}
