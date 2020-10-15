package com.grace.placessearch.common.app

/**
 * A local data store equivalent to save favorite venue ids.
 */
class FavoritePlacesPreferences {

    var favoriteVenues: String? = null

    override fun toString(): String {
        return String.format("favoriteVenues: %s", favoriteVenues)
    }
}
