package com.grace.placessearch.service;

import com.grace.placessearch.common.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.common.data.model.VenueResponse;
import com.grace.placessearch.common.data.model.VenuesResponse;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Deprecated
public interface PlacesApiOrig {
    int READ_TIMEOUT = 20000; //ms

    // Fetch Trending venues
    Observable<Result<VenuesResponse>> getTrendingVenues();

    // Search for venues using a search term
    Observable<Result<VenuesResponse>> searchForVenues(String searchTerm);

    // Search for 'Search' Suggestions based on a term
    Observable<Result<SuggestedVenuesResponse>> searchForSuggestedVenues(String searchTerm);

    // Fetch details of a specific venue
    Observable<Result<VenueResponse>> getVenue(String venueId);
}
