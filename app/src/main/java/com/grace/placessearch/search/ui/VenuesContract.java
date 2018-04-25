package com.grace.placessearch.search.ui;

import com.grace.placessearch.data.model.Venue;

import java.util.List;

/**
 * This interface defines the data dependency contract between the Venues
 * Views(e.g. {@link SearchActivity}) and the Presenters (e.g. {@link VenuesPresenter}).
 *
 * Created by vicsonvictor on 4/21/18.
 */
public interface VenuesContract {

    interface View {
        void onSearch(List<Venue> venues);
        void onSuggestedSearches(List<String> suggestedSearches);
        void onVenue(Venue venue);
        void onError();
    }

    interface Presenter {
        void bindView(VenuesContract.View view);
        void unBindView();
        void doSearch(String term);
        void doSuggestedSearch(String searchTerm);
        void doGetVenue(String venueId);
    }
}

