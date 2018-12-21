package com.grace.placessearch.common.data

import android.util.LruCache
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.data.model.VenuesResponse
import com.grace.placessearch.service.PlacesApi
import retrofit2.adapter.rxjava.Result
import rx.Subscriber
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesSearchStartupManager @Inject
constructor(lruCache: LruCache<Any, Any>, placesApi: PlacesApi) {

    private val lruCache = lruCache
    private val placesApi = placesApi

    /**
     * Used for app-start-up data caching.
     */
    fun fetchAndCacheData() {
        // Trending data could be potentially used to display popular venues
        // instead of displaying blank results. Not doing it to stick to the requirements :)

        fetchTrendingVenues()
    }

    private fun fetchTrendingVenues() {
        @Suppress("UNCHECKED_CAST")
        val cached = lruCache.get(PlacesSearchConstants.CACHE_KEY_TRENDING_VENUES) as? List<String>
        if (cached == null) {
            placesApi.trendingVenues()
                    .subscribeOn(Schedulers.io())
                    .subscribe(TrendingVenuesSubscriber())
        }
    }

    private inner class TrendingVenuesSubscriber() : Subscriber<Result<VenuesResponse>>() {

        override fun onCompleted() {
            this.unsubscribe()
        }

        override fun onError(e: Throwable) {
            Timber.w(e, "Unexpected error when fetching trending venues")
        }

        override fun onNext(result: Result<VenuesResponse>) {
            val venuesResponse = result.response().body()
            if (venuesResponse != null && venuesResponse.venueListResponse != null) {
                val trendingVenues = venuesResponse.venueListResponse.venues
                Timber.i("No. of Trending venues cached: %d", trendingVenues.size)
                lruCache.put(PlacesSearchConstants.CACHE_KEY_TRENDING_VENUES, trendingVenues)
            }
        }
    }


}