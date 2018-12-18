package com.grace.placessearch.search.ui;

import android.util.LruCache;

import com.grace.placessearch.common.PlacesSearchConstants;
import com.grace.placessearch.data.model.Category;
import com.grace.placessearch.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.data.model.VenueResponse;
import com.grace.placessearch.data.model.VenuesResponse;
import com.grace.placessearch.common.data.VenuesDataManager;
import com.grace.placessearch.common.ui.injection.scope.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.Result;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * This class acts as the proxy to the data/service layer. The Activities and
 * other views needed to interact with data, go through the proxies.
 */
@ActivityScope
public class VenuesPresenter implements VenuesContract.Presenter {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final VenuesDataManager venuesDataManager;
    private final LruCache<Object, Object> lruCache;
    private VenuesContract.View viewListener;

    @Inject
    public VenuesPresenter(LruCache<Object, Object> lruCache, VenuesDataManager venuesDataManager) {
        this.lruCache = lruCache;
        this.venuesDataManager = venuesDataManager;
    }

    @Override
    public void bindView(VenuesContract.View viewListener) {
        this.viewListener = viewListener;
    }

    @Override
    public void unBindView() {
        unsubscribe();
        this.viewListener = null;
    }

    @Override
    public void doSearch(String searchTerm) {
        subscriptions.add(getSearchSubscription(searchTerm));
    }

    @Override
    public void doSuggestedSearch(String searchTerm) {
        subscriptions.add(getSearchSuggestionsSubscription(searchTerm));
    }

    @Override
    public void doGetVenue(String venueId) {
        subscriptions.add(getVenueSubscription(venueId));
    }

    private void unsubscribe() {
        subscriptions.clear();
    }

    private Subscription getVenueSubscription(String venueId) {
        return venuesDataManager.getVenue(venueId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new VenueSubscriber(viewListener));
    }

    private Subscription getSearchSubscription(String term) {
        return venuesDataManager.searchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SearchSubscriber(viewListener, lruCache));
    }

    private Subscription getSearchSuggestionsSubscription(String term) {
        return venuesDataManager.suggestedSearchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SearchSuggestionsSubscriber(viewListener));
    }

    private static class SearchSuggestionsSubscriber extends Subscriber<Result<SuggestedVenuesResponse>> {

        private VenuesContract.View listener;

        public SearchSuggestionsSubscriber(VenuesContract.View listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted() {
            // n/a
        }

        @Override
        public void onError(Throwable e) {
            listener.onError();
        }

        @Override
        public void onNext(Result<SuggestedVenuesResponse> result) {
            List<String> suggestedSearchStrings = new ArrayList<>();
            SuggestedVenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getResponse() != null) {
                List<Venue> miniVenues = venuesResponse.getResponse().getVenues();
                Timber.i("No. of venues in search result: %d", miniVenues.size());

                for (int i = 0; i < miniVenues.size(); i++) {

                    Venue venue = miniVenues.get(i);
                    if (venue.getName() != null && !venue.getName().isEmpty()) {
                        suggestedSearchStrings.add(venue.getName());
                    }

                    List<Category> venueCategories = new ArrayList<>();
                    for (int j = 0; j < venueCategories.size(); j++) {
                        Category category = venueCategories.get(j);
                        if (category.getName() != null && !category.getName().isEmpty()) {
                            suggestedSearchStrings.add(category.getName());
                        }
                    }
                }

                if (listener != null) {
                    Timber.d("Suggested search strings %s", suggestedSearchStrings);
                    listener.onSuggestedSearches(suggestedSearchStrings);
                }

            } else {
                if (listener != null) {
                    Timber.i("Empty Suggested search strings %s", suggestedSearchStrings);
                    listener.onSuggestedSearches(new ArrayList<String>());
                }
            }
        }
    }

    private static class SearchSubscriber extends Subscriber<Result<VenuesResponse>> {

        private VenuesContract.View listener;
        private LruCache<Object, Object> lruCache;

        public SearchSubscriber(VenuesContract.View listener, LruCache<Object, Object> lruCache) {
            this.listener = listener;
            this.lruCache = lruCache;
        }

        @Override
        public void onCompleted() {
            // n/a
        }

        @Override
        public void onError(Throwable e) {
            listener.onError();
        }

        @Override
        public void onNext(Result<VenuesResponse> result) {
            VenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getVenueListResponse() != null) {
                List<Venue> venues = venuesResponse.getVenueListResponse().getVenues();
                Timber.i("No. of venues for search: %d", venues.size());
                if (listener != null) {
                    listener.onSearch(venues);
                }
            }
        }
    }

    private static class VenueSubscriber extends Subscriber<Result<VenueResponse>> {

        private VenuesContract.View listener;

        public VenueSubscriber(VenuesContract.View listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted() {
            // n/a
        }

        @Override
        public void onError(Throwable e) {
            listener.onError();
        }

        @Override
        public void onNext(Result<VenueResponse> result) {
            VenueResponse venueResponse = result.response().body();
            if (venueResponse != null && venueResponse.getSingleVenueResponse() != null) {
                Timber.i("Venue details fetched for %s", venueResponse.getSingleVenueResponse().getVenue().getName());
                if (listener != null) {
                    listener.onVenue(venueResponse.getSingleVenueResponse().getVenue());
                }
            }
        }
    }

}
