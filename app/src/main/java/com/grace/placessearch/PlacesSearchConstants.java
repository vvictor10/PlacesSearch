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

    public static double SEATTLE_CENTER_LAT = 47.6062;
    public static double SEATTLE_CENTER_LNG = -122.3321;
}
