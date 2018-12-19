package com.grace.placessearch.common.app

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson
import com.grace.placessearch.BuildConfig
import com.grace.placessearch.common.app.injection.qualifier.ForApplication
import timber.log.Timber

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesSearchPreferenceManager @Inject
constructor(@ForApplication context: Context) {

    private val sharedPreferences: SharedPreferences
    lateinit var favoriteVenuePreferences: FavoritePlacesPreferences

    init {
        this.sharedPreferences = context.getSharedPreferences(PLACES_SEARCH_PREFERENCES_KEY, Context.MODE_PRIVATE)
        createFavoritePlacesPreferencesInstance()
    }

    private fun createFavoritePlacesPreferencesInstance(): FavoritePlacesPreferences {
        val jsonString = sharedPreferences.getString(FAVORITE_VENUE_PREFERENCES_CONTENT, null)
        return when (jsonString) {
            null -> {
                favoriteVenuePreferences = FavoritePlacesPreferences()
                favoriteVenuePreferences
            }
            else -> {
                val instance = Gson().fromJson(jsonString, FavoritePlacesPreferences::class.java)
                this.favoriteVenuePreferences = instance
                instance
            }
        }
    }

    fun saveFavoriteVenuePreferences(favoriteVenuePreferences: FavoritePlacesPreferences) {
        this.favoriteVenuePreferences = favoriteVenuePreferences
        sharedPreferences.edit().putString(FAVORITE_VENUE_PREFERENCES_CONTENT, Gson().toJson(favoriteVenuePreferences)).commit()
    }

    companion object {
        val PLACES_SEARCH_PREFERENCES_KEY = BuildConfig.PROJECT_NAME
        private val FAVORITE_VENUE_PREFERENCES_CONTENT = PlacesSearchPreferenceManager::class.java.simpleName + ".favoriteVenuePreferences"
    }

}
