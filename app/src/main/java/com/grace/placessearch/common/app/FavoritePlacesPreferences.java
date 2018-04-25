package com.grace.placessearch.common.app;

/**
 * A local data store equivalent to save favorite venue ids.
 */
public class FavoritePlacesPreferences {

    public String favoriteVenues;

    @Override
    public String toString() {
        return String.format("favoriteVenues: %s", favoriteVenues);
    }
}
