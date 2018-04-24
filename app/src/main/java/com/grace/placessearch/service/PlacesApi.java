package com.grace.placessearch.service;

import com.grace.placessearch.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.data.model.VenueResponse;
import com.grace.placessearch.data.model.VenuesResponse;


import retrofit2.adapter.rxjava.Result;
import rx.Observable;

/**
 * Created by vicsonvictor on 4/21/18.
 */

public interface PlacesApi {
    int READ_TIMEOUT = 20000; //ms

    Observable<Result<VenuesResponse>> getTrendingVenues();

    Observable<Result<VenuesResponse>> searchForVenues(String searchTerm);

    Observable<Result<SuggestedVenuesResponse>> searchForSuggestedVenues(String searchTerm);

    Observable<Result<VenueResponse>> getVenue(String venueId);
}
