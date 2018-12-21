package com.grace.placessearch.common.util

import com.google.gson.Gson
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.data.model.Location
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by vicsonvictor on 4/21/18.
 */

object PlacesSearchUtil {

    /**
     * Returns the formatted user location(Austin center in this case)
     * @return
     */
    val latLngOfUserLocation: String
        get() = PlacesSearchConstants.USER_LOCATION_LAT.toString() + "," + PlacesSearchConstants.USER_LOCATION_LNG

    /**
     * Indicates if the passed in venueId is in the user's favorites list.
     * @param preferenceManager
     * @param venueId
     * @return
     */
    fun isFavorite(preferenceManager: PlacesSearchPreferenceManager, venueId: String?): Boolean {
        val preferences = preferenceManager.favoriteVenuePreferences
        val text = Gson().fromJson(preferences.favoriteVenues, Array<String>::class.java)

        if (text == null || text.isEmpty()) {
            return false
        }

        val favorites = Arrays.asList(*text)
        val isFavorite = favorites.contains(venueId)
        Timber.d("Is venue %s a favorite for the user? %b", venueId, isFavorite)
        return isFavorite
    }

    /**
     * Adds a venue to the user's favorites list.
     * @param preferenceManager
     * @param venueId
     */
    fun addFavorite(preferenceManager: PlacesSearchPreferenceManager, venueId: String?) {

        if (venueId == null)
            return

        val preferences = preferenceManager.favoriteVenuePreferences
        val text = Gson().fromJson(preferences.favoriteVenues, Array<String>::class.java)

        val favorites = ArrayList<String>()
        if (text != null) {
            favorites.addAll(Arrays.asList(*text))
        }

        favorites.add(venueId)

        preferences.favoriteVenues = Gson().toJson(favorites)
        preferenceManager.saveFavoriteVenuePreferences(preferences)
        Timber.i("Favorite venue %s added", venueId)
    }

    /**
     * Removes a favorite venue from the user's list.
     * @param preferenceManager
     * @param venueId
     */
    fun removeFavorite(preferenceManager: PlacesSearchPreferenceManager, venueId: String?) {

        if (venueId == null)
            return

        val preferences = preferenceManager.favoriteVenuePreferences
        val text = Gson().fromJson(preferences.favoriteVenues, Array<String>::class.java)
        if (text == null || text.isEmpty()) {
            return
        }

        val favorites = ArrayList<String>()
        favorites.addAll(Arrays.asList(*text))

        if (favorites.isEmpty()) {
            return
        }

        favorites.remove(venueId)
        Timber.i("Favorite venue %s removed", venueId)

        preferences.favoriteVenues = Gson().toJson(favorites)
        preferenceManager.saveFavoriteVenuePreferences(preferences)
    }

    /**
     * Returns the distance in miles between the user location(Austin center in this case)
     * and the passed in location.
     *
     * @param distanceTo
     * @return
     */
    fun getDistanceInMilesToUserLocation(distanceTo: Location?): String? {

        if (distanceTo == null || distanceTo.lat == 0.0 || distanceTo.lng == 0.0) {
            return null
        }

        val startPoint = android.location.Location("a")
        startPoint.latitude = PlacesSearchConstants.USER_LOCATION_LAT
        startPoint.longitude = PlacesSearchConstants.USER_LOCATION_LNG

        val endPoint = android.location.Location("b")
        endPoint.latitude = distanceTo.lat
        endPoint.longitude = distanceTo.lng

        val distance = startPoint.distanceTo(endPoint)

        val mile = distance / 1609.34f
        return String.format("%.2f", mile)
    }
}
