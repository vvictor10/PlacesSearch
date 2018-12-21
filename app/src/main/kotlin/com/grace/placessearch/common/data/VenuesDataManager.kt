package com.grace.placessearch.common.data

import com.grace.placessearch.common.data.model.SuggestedVenuesResponse
import com.grace.placessearch.common.data.model.VenueResponse
import com.grace.placessearch.common.data.model.VenuesResponse
import com.grace.placessearch.service.PlacesApi
import retrofit2.adapter.rxjava.Result
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A component to host all business logic associated with Venue data.
 * Acts as a facade between the data service layer and the UI code components.
 */
@Singleton
class VenuesDataManager @Inject
constructor(private val placesApi: PlacesApi) {

    /**
     * Used to search for venues based on a search string.
     */
    fun searchForVenues(term: String): Observable<Result<VenuesResponse>> {
        return placesApi.searchForVenues(term)
    }

    /**
     * Used to search for venue suggestions based on a search string.
     */
    fun suggestedSearchForVenues(term: String): Observable<Result<SuggestedVenuesResponse>> {
        return placesApi.searchForSuggestedVenues(term)
    }

    /**
     * Used to fetch details of a single venue
     */
    fun getVenue(venueId: String): Observable<Result<VenueResponse>> {
        return placesApi.getVenue(venueId)
    }

}
