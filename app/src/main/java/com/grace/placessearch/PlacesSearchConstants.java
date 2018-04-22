package com.grace.placessearch;

import com.grace.placessearch.util.DecimalByteUnit;

/**
 * Created by vicsonvictor on 4/21/18.
 */

public class PlacesSearchConstants {

    public static final String HTTP = "http";
    public static final int HTTP_TIMEOUT_VALUE = 10;

    // Disk cache size used for Picasso - set to 50MB for now.
    public static final int IMAGE_DISK_CACHE_SIZE = (int) DecimalByteUnit.MEGABYTES.toBytes(50);

    public static String CACHE_KEY_TRENDING_VENUES = "TrendingVenuesCache";
    public static final String CACHE_KEY_FAVORITES_CACHE = "FavoritesCache";
}
