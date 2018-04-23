package com.grace.placessearch.search.ui;

import android.util.LruCache;

import com.grace.placessearch.data.model.Category;
import com.grace.placessearch.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.data.model.VenuesResponse;
import com.grace.placessearch.search.data.SearchDataManager;
import com.grace.placessearch.ui.injection.scope.ActivityScope;

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

@ActivityScope
public class SearchPresenter implements SearchContract.Presenter {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final SearchDataManager searchDataManager;
    private static LruCache lruCache = null;
    private static String searchTerm;
    private SearchContract.View viewListener;

    @Inject
    public SearchPresenter(LruCache lruCache, SearchDataManager searchDataManager) {
        this.lruCache = lruCache;
        this.searchDataManager = searchDataManager;
    }

    @Override
    public void bindView(SearchContract.View viewListener) {
        this.viewListener = viewListener;
    }

    @Override
    public void unBindView() {
        unsubscribe();
        this.viewListener = null;
    }

    @Override
    public void doSearch(String searchTerm) {
        this.searchTerm = searchTerm;
        subscriptions.add(getSearchSubscription(searchTerm));
    }

    @Override
    public void doSuggestedSearch(String searchTerm) {
        subscriptions.add(getSearchSuggestionsSubscription(searchTerm));
    }

    private void unsubscribe() {
        subscriptions.clear();
    }

    private Subscription getSearchSubscription(String term) {
        return searchDataManager.searchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SearchSubscriber(viewListener));
    }

    private Subscription getSearchSuggestionsSubscription(String term) {
        return searchDataManager.suggestedSearchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SearchSuggestionsSubscriber(viewListener));
    }

    private static class SearchSuggestionsSubscriber extends Subscriber<Result<SuggestedVenuesResponse>> {

        private SearchContract.View listener;

        public SearchSuggestionsSubscriber(SearchContract.View listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted() {
            // n/a
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            listener.onError();
        }

        @Override
        public void onNext(Result<SuggestedVenuesResponse> result) {
            List<String> suggestedSearchStrings = new ArrayList<>();
            SuggestedVenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getResponse() != null) {
                List<Venue> minivenues = venuesResponse.getResponse().getVenues();
                Timber.i("No. of venues for search: %d", minivenues.size());

                for (int i = 0; i < minivenues.size(); i++) {
                    Venue venue = minivenues.get(i);
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
                    Timber.i("Suggested search strings %s", suggestedSearchStrings);
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

        private SearchContract.View listener;

        public SearchSubscriber(SearchContract.View listener) {
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
        public void onNext(Result<VenuesResponse> result) {
            VenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getResponse() != null) {
                List<Venue> venues = venuesResponse.getResponse().getVenues();
                Timber.i("No. of venues for search: %d", venues.size());
                if (listener != null) {
                    listener.onSearch(venues);
                }
            }
        }
    }

}
