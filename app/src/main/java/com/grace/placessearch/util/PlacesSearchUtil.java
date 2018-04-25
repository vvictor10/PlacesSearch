package com.grace.placessearch.util;

import com.google.gson.Gson;
import com.grace.placessearch.PlacesSearchConstants;
import com.grace.placessearch.common.app.FavoritePlacesPreferences;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * Created by vicsonvictor on 4/21/18.
 */

public class PlacesSearchUtil {

    /**
     * Indicates if the passed in venueId is in the user's favorites list.
     * @param preferenceManager
     * @param venueId
     * @return
     */
    public static boolean isFavorite(PlacesSearchPreferenceManager preferenceManager, String venueId) {
        FavoritePlacesPreferences preferences = preferenceManager.getFavoriteVenuePreferences();
        String[] text = new Gson().fromJson(preferences.favoriteVenues, String[].class);

        if (text == null || text.length == 0) {
            return false;
        }

        List<String> favorites = Arrays.asList(text);
        boolean isFavorite = favorites != null && favorites.contains(venueId);
        Timber.d("Is venue %s a favorite for the user? %b", venueId, isFavorite);
        return isFavorite;
    }

    /**
     * Adds a venue to the user's favorites list.
     * @param preferenceManager
     * @param venueId
     */
    public static void addFavorite(PlacesSearchPreferenceManager preferenceManager, String venueId) {
        FavoritePlacesPreferences preferences = preferenceManager.getFavoriteVenuePreferences();
        String[] text = new Gson().fromJson(preferences.favoriteVenues, String[].class);

        List<String> favorites;
        if (text != null) {
            favorites = new ArrayList<>();
            favorites.addAll(Arrays.asList(text));
        } else {
            favorites = new ArrayList<>();
        }

        favorites.add(venueId);

        preferences.favoriteVenues = new Gson().toJson(favorites);
        preferenceManager.setFavoriteVenuePreferences(preferences);
        Timber.i("Favorite venue %s added", venueId);
    }

    /**
     * Removes a favorite venue from the user's list.
     * @param preferenceManager
     * @param venueId
     */
    public static void removeFavorite(PlacesSearchPreferenceManager preferenceManager, String venueId) {
        FavoritePlacesPreferences preferences = preferenceManager.getFavoriteVenuePreferences();
        String[] text = new Gson().fromJson(preferences.favoriteVenues, String[].class);
        if (text == null || text.length == 0) {
            return;
        }

        List<String> favorites = new ArrayList<>();
        favorites.addAll(Arrays.asList(text));

        if (favorites == null || favorites.isEmpty()) {
            return;
        }
        favorites.remove(venueId);
        Timber.i("Favorite venue %s removed", venueId);

        preferences.favoriteVenues = new Gson().toJson(favorites);
        preferenceManager.setFavoriteVenuePreferences(preferences);
    }

    /**
     * Returns the distance in miles between the user location(Seattle center in this case)
     * and the passed in location.
     *
     * @param distanceTo
     * @return
     */
    public static String getDistanceInMiles(Location distanceTo) {

        if (distanceTo == null || distanceTo.getLat() == 0 || distanceTo.getLng() == 0) {
            return "Not Available";
        }

        android.location.Location startPoint = new android.location.Location("a");
        startPoint.setLatitude(PlacesSearchConstants.USER_LOCATION_LAT);
        startPoint.setLongitude(PlacesSearchConstants.USER_LOCATION_LNG);

        android.location.Location endPoint = new android.location.Location("b");
        endPoint.setLatitude(distanceTo.getLat());
        endPoint.setLongitude(distanceTo.getLng());

        float distance = startPoint.distanceTo(endPoint);

        float mile = distance / 1609.34f;
        return String.format("%.2f", mile) + " miles";
    }

    /**
     * Returns the formatted user location(Seattle center in this case)
     * @return
     */
    public static String getLatLngOfUserLocation() {
        return PlacesSearchConstants.USER_LOCATION_LAT + "," + PlacesSearchConstants.USER_LOCATION_LNG;
    }
}
