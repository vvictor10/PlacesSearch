package com.grace.placessearch.service

import com.grace.placessearch.common.data.model.SuggestedVenuesResponse
import com.grace.placessearch.common.data.model.VenueResponse
import com.grace.placessearch.common.data.model.VenuesResponse
import retrofit2.adapter.rxjava.Result
import rx.Observable

/**
 * Created by vicsonvictor on 4/21/18.
 */

interface PlacesApi {

    // Fetch Trending venues
    fun trendingVenues(): Observable<Result<VenuesResponse>>

    // Search for venues using a search term
    fun searchForVenues(searchTerm: String): Observable<Result<VenuesResponse>>

    // Search for 'Search' Suggestions based on a term
    fun searchForSuggestedVenues(searchTerm: String): Observable<Result<SuggestedVenuesResponse>>

    // Fetch details of a specific venue
    fun getVenue(venueId: String): Observable<Result<VenueResponse>>

    companion object {
        const val READ_TIMEOUT = 20000 //ms
    }
}
