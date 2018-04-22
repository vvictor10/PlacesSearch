package com.grace.placessearch.search.ui;

import com.grace.placessearch.data.model.Venue;

import java.util.List;

/**
 * Created by vicsonvictor on 4/21/18.
 */
public interface SearchContract {

    interface View {
        void onSearch(List<Venue> venues);

        void onSuggestedSearches(List<String> suggestedSearches);

        void onError();

        void onNextError();
    }

    interface Presenter {
        void bindView(SearchContract.View view);

        void unBindView();

        void doSearch(String term);

        void doSuggestedSearch(String searchTerm);
    }
}

