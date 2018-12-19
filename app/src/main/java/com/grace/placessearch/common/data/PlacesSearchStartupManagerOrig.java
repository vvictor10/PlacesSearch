package com.grace.placessearch.common.data;

import android.util.LruCache;

import com.grace.placessearch.common.PlacesSearchConstantsOrig;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.data.model.VenuesResponse;
import com.grace.placessearch.service.PlacesApiOrig;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.Result;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * This component is responsible for satisfying app start up data dependencies.
 *
 * Created by vicsonvictor on 4/22/2018.
 */
@Deprecated
@Singleton
public class PlacesSearchStartupManagerOrig {

    private final LruCache<Object, Object> lruCache;
    private final PlacesApiOrig mPlacesApi;
    private final PlacesSearchPreferenceManager preferenceManager;

    @Inject
    public PlacesSearchStartupManagerOrig(LruCache<Object, Object> lruCache,
            PlacesApiOrig placesApi,
            PlacesSearchPreferenceManager placesSearchPreferenceManager) {

        this.lruCache = lruCache;
        this.mPlacesApi = placesApi;
        this.preferenceManager = placesSearchPreferenceManager;
    }

    /**
     * Used for app-start-up data caching.
     */
    public void fetchAndCacheData() {
        // Trending data could be potentially used to display popular venues
        // instead of displaying blank results. Not doing it to stick to the requirements :)

        fetchTrendingVenues();
    }

    private void fetchTrendingVenues() {
        List<String> cached = (List<String>) lruCache.get(PlacesSearchConstantsOrig.CACHE_KEY_TRENDING_VENUES);
        if (cached == null) {
            mPlacesApi.getTrendingVenues()
                    .subscribeOn(Schedulers.io())
                    .subscribe(new TrendingVenuesSubscriber());
        }
    }

    private class TrendingVenuesSubscriber extends Subscriber<Result<VenuesResponse>> {

        public TrendingVenuesSubscriber() {

        }

        @Override
        public void onCompleted() {
            this.unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            Timber.w(e, "Unexpected error when fetching trending venues");
        }

        @Override
        public void onNext(Result<VenuesResponse> result) {
            VenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getVenueListResponse() != null) {
                List<Venue> trendingVenues = venuesResponse.getVenueListResponse().getVenues();
                Timber.i("No. of Trending venues cached: %d", trendingVenues.size());
                lruCache.put(PlacesSearchConstantsOrig.CACHE_KEY_TRENDING_VENUES, trendingVenues);
            }
        }
    }

}
