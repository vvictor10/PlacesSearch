package com.grace.placessearch.search.ui

import android.util.LruCache
import com.grace.placessearch.common.data.VenuesDataManager
import com.grace.placessearch.common.ui.injection.scope.ActivityScope
import com.grace.placessearch.data.model.Category
import com.grace.placessearch.data.model.SuggestedVenuesResponse
import com.grace.placessearch.data.model.VenueResponse
import com.grace.placessearch.data.model.VenuesResponse
import retrofit2.adapter.rxjava.Result
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * This class acts as the proxy to the data/service layer. The Activities and
 * other views needed to interact with data, go through the proxies.
 */
@ActivityScope
class VenuesPresenter @Inject
constructor(private val lruCache: LruCache<Any, Any>, private val venuesDataManager: VenuesDataManager) : VenuesContract.Presenter {

    private val subscriptions = CompositeSubscription()
    private var viewListener: VenuesContract.View? = null

    override fun bindView(viewListener: VenuesContract.View) {
        this.viewListener = viewListener
    }

    override fun unBindView() {
        unsubscribe()
        this.viewListener = null
    }

    override fun doSearch(searchTerm: String) {
        subscriptions.add(getSearchSubscription(searchTerm))
    }

    override fun doSuggestedSearch(searchTerm: String) {
        subscriptions.add(getSearchSuggestionsSubscription(searchTerm))
    }

    override fun doGetVenue(venueId: String) {
        subscriptions.add(getVenueSubscription(venueId))
    }

    private fun unsubscribe() {
        subscriptions.clear()
    }

    private fun getVenueSubscription(venueId: String): Subscription {
        return venuesDataManager.getVenue(venueId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(VenueSubscriber(viewListener))
    }

    private fun getSearchSubscription(term: String): Subscription {
        return venuesDataManager.searchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(SearchSubscriber(viewListener, lruCache))
    }

    private fun getSearchSuggestionsSubscription(term: String): Subscription {
        return venuesDataManager.suggestedSearchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(SearchSuggestionsSubscriber(viewListener))
    }

    private class SearchSuggestionsSubscriber(private val listener: VenuesContract.View?) : Subscriber<Result<SuggestedVenuesResponse>>() {

        override fun onCompleted() {
            // n/a
        }

        override fun onError(e: Throwable) {
            listener?.onError()
        }

        override fun onNext(result: Result<SuggestedVenuesResponse>) {
            val suggestedSearchStrings = ArrayList<String>()
            val venuesResponse = result.response().body()
            if (venuesResponse != null && venuesResponse.response != null) {
                val miniVenues = venuesResponse.response.venues
                Timber.i("No. of venues in search result: %d", miniVenues.size)

                for (i in miniVenues.indices) {

                    val venue = miniVenues[i]
                    if (venue.name != null && !venue.name.isEmpty()) {
                        suggestedSearchStrings.add(venue.name)
                    }

                    val venueCategories = ArrayList<Category>()
                    for (j in venueCategories.indices) {
                        val category = venueCategories[j]
                        if (category.name != null && !category.name.isEmpty()) {
                            suggestedSearchStrings.add(category.name)
                        }
                    }
                }

                Timber.d("Suggested search strings %s", suggestedSearchStrings)
                listener?.onSuggestedSearches(suggestedSearchStrings)

            } else {
                Timber.i("Empty Suggested search strings %s", suggestedSearchStrings)
                listener?.onSuggestedSearches(ArrayList())
            }
        }
    }

    private class SearchSubscriber(private val listener: VenuesContract.View?, private val lruCache: LruCache<Any, Any>) : Subscriber<Result<VenuesResponse>>() {

        override fun onCompleted() {
            // n/a
        }

        override fun onError(e: Throwable) {
            listener?.onError()
        }

        override fun onNext(result: Result<VenuesResponse>) {
            val venuesResponse = result.response().body()
            if (venuesResponse != null && venuesResponse.venueListResponse != null) {
                val venues = venuesResponse.venueListResponse.venues as java.util.ArrayList
                Timber.i("No. of venues for search: %d", venues.size)
                listener?.onSearch(venues)
            }
        }
    }

    private class VenueSubscriber(private val listener: VenuesContract.View?) : Subscriber<Result<VenueResponse>>() {

        override fun onCompleted() {
            // n/a
        }

        override fun onError(e: Throwable) {
            listener?.onError()
        }

        override fun onNext(result: Result<VenueResponse>) {
            val venueResponse = result.response().body()
            if (venueResponse != null && venueResponse.singleVenueResponse != null) {
                Timber.i("Venue details fetched for %s", venueResponse.singleVenueResponse.venue.name)
                listener?.onVenue(venueResponse.singleVenueResponse.venue)
            }
        }
    }

}
