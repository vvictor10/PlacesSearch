package com.grace.placessearch.service.local;


import com.grace.placessearch.service.PlacesApi;

import java.io.File;

public abstract class PlacesTestBase {

    private static final PlacesApi PLACES_API = new PlacesApiLocal(getResourcesPath());
    private static final String RESOURCES_PATH = "/resources";

    public static final String getResourcesPath() {
        return new File(".").getAbsolutePath() + RESOURCES_PATH;
    }

    protected final PlacesApi getPlaces() {
        return PLACES_API;
    }
}
