package com.grace.placessearch.common;

import com.grace.placessearch.BuildConfig;
import com.grace.placessearch.common.util.DecimalByteUnit;

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Deprecated
public class PlacesSearchConstantsOrig {

    public static final String HTTP = "http";
    public static final int HTTP_TIMEOUT_VALUE = 10;

    public static final int HEART_CROSS_FADE_ANIMATION_DURATION = 150;

    // Disk cache size used for Picasso - set to 50MB for now.
    public static final int IMAGE_DISK_CACHE_SIZE = (int) DecimalByteUnit.MEGABYTES.toBytes(50);

    public static String CACHE_KEY_TRENDING_VENUES = "TrendingVenuesCache";

    public static double USER_LOCATION_LAT = new Double(BuildConfig.USER_LOCATION_LAT);
    public static double USER_LOCATION_LNG = new Double(BuildConfig.USER_LOCATION_LNG);

    public static String MAP_PINS_EXTRA = "MapPinsExtra";
    public static String VENUE_NAME_EXTRA = "VenueNameExtra";
    public static String VENUE_ID_EXTRA = "VenueIdExtra";
    public static String VENUE_POSITION_EXTRA = "VenuePositionExtra";
    public static String VENUE_IS_FAVORITE = "IsFavoriteExtra";
}
