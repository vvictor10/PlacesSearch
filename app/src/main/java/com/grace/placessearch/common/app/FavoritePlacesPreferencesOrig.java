package com.grace.placessearch.common.app;

/**
 * A local data store equivalent to save favorite venue ids.
 */
@Deprecated
public class FavoritePlacesPreferencesOrig {

    public String favoriteVenues;

    @Override
    public String toString() {
        return String.format("favoriteVenues: %s", favoriteVenues);
    }
}
