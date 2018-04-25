package com.grace.placessearch.search.data;

import com.grace.placessearch.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.data.model.VenueResponse;
import com.grace.placessearch.data.model.VenuesResponse;
import com.grace.placessearch.service.PlacesApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;

/**
 * Search data manager, a component to host all business logic associated
 * with Search and the like.
 */
@Singleton
public class SearchDataManager {

    private final PlacesApi placesApi;

    @Inject
    public SearchDataManager(PlacesApi placesApi) {
        this.placesApi = placesApi;
    }

    /**
     * Used to search for venues based on a search string.
     */
    public Observable<Result<VenuesResponse>> searchForVenues(String term) {
        return placesApi.searchForVenues(term);
    }

    /**
     * Used to search for venue suggestions based on a search string.
     */
    public Observable<Result<SuggestedVenuesResponse>> suggestedSearchForVenues(String term) {
        return placesApi.searchForSuggestedVenues(term);
    }

    /**
     * Used to fetch details of a single venue
     */
    public Observable<Result<VenueResponse>> getVenue(String venueId) {
        return placesApi.getVenue(venueId);
    }

}
