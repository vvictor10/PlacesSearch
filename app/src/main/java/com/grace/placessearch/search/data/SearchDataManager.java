package com.grace.placessearch.search.data;

import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.data.model.VenuesResponse;
import com.grace.placessearch.service.PlacesApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;

/**
 * Search data manager
 */
@Singleton
public class SearchDataManager {

    private final PlacesApi placesApi;
    private final PlacesSearchPreferenceManager preferenceManager;

    @Inject
    public SearchDataManager(PlacesApi placesApi, PlacesSearchPreferenceManager preferenceManager) {
        this.placesApi = placesApi;
        this.preferenceManager = preferenceManager;
    }

    /**
     * Used to search for venues based on a search string.
     * @param term
     * @return
     */
    public Observable<Result<VenuesResponse>> searchForVenues(String term) {
        return placesApi.searchForVenues(term);
    }

    /**
     * Used to search for venue suggestions based on a search string.
     * @param term
     * @return
     */
    public Observable<Result<SuggestedVenuesResponse>> suggestedSearchForVenues(String term) {
        return placesApi.searchForSuggestedVenues(term);
    }

}
