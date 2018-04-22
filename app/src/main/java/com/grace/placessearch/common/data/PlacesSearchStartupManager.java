package com.grace.placessearch.common.data;

import android.util.LruCache;

import com.grace.placessearch.PlacesSearchConstants;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.data.model.VenuesResponse;
import com.grace.placessearch.service.PlacesApi;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.Result;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by vicsonvictor on 2/15/16.
 */
@Singleton
public class PlacesSearchStartupManager {

    private final LruCache lruCache;
    private final PlacesApi mPlacesApi;
    private final PlacesSearchPreferenceManager preferenceManager;

    @Inject
    public PlacesSearchStartupManager(LruCache lruCache,
            PlacesApi placesApi,
            PlacesSearchPreferenceManager placesSearchPreferenceManager) {

        this.lruCache = lruCache;
        this.mPlacesApi = placesApi;
        this.preferenceManager = placesSearchPreferenceManager;
    }

    /**
     * Used for app-start-up data caching.
     */
    public void fetchAndCacheData() {
        Timber.i("called");
        fetchTrendingVenues();
        // First get the visitor token, if not found
//        if (preferenceManager.getVisitorToken() == null) {
//            Timber.i("Visitor Token not found, about to get one from the guest-authentication call");
//            ecomm.getVisitorToken(new AuthRequest(EcommConstants.US_SITE_CODE))
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new GuestAuthResponseSubscriber(skipStartupDataRefresh));
//        } else {
//            Timber.i("Visitor Token found in lruCache");
//            ((EcommRetrofit) ecomm).setVisitorToken(preferenceManager.getVisitorToken());
//            Timber.i("Visitor Token set on EcommRetrofit instance %s", ecomm);
//            if (!skipStartupDataRefresh) {
//                Timber.i("Refreshing CMS & Rollouts & other app start-up data");
//                cacheAppStartupData();
//            }
//
//            syncEcommStartupData();
//        }

    }

    private void fetchTrendingVenues() {
        Timber.i("called");
        Set<String> cached = (Set<String>) lruCache.get(PlacesSearchConstants.CACHE_KEY_TRENDING_VENUES);
        if (cached == null) {
            mPlacesApi.getTrendingVenues()
                    .subscribeOn(Schedulers.io())
                    .subscribe(new TrendingVenuesSubscriber());
        }
    }

    private class TrendingVenuesSubscriber extends Subscriber<Result<VenuesResponse>> {

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
            if (venuesResponse != null && venuesResponse.getResponse() != null) {
                List<Venue> trendingVenues = venuesResponse.getResponse().getVenues();
                Timber.i("No. of Trending venues cached: %d", trendingVenues.size());
                lruCache.put(PlacesSearchConstants.CACHE_KEY_TRENDING_VENUES, trendingVenues);
            }
        }
    }

}
