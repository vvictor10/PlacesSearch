package com.grace.placessearch.common.data;

import com.grace.placessearch.common.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.common.data.model.VenueResponse;
import com.grace.placessearch.common.data.model.VenuesResponse;
import com.grace.placessearch.service.PlacesApiOrig;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;

/**
 * A component to host all business logic associated with Venue data.
 * Acts as a facade between the data service layer and the UI code components.
 */
@Deprecated
@Singleton
public class VenuesDataManagerOrig {

    private final PlacesApiOrig placesApi;

    @Inject
    public VenuesDataManagerOrig(PlacesApiOrig placesApi) {
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
