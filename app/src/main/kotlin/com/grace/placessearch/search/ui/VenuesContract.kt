package com.grace.placessearch.search.ui

import com.grace.placessearch.data.model.Venue

/**
 * This interface defines the data dependency contract between the Venues
 * Views(e.g. [SearchActivity]) and the Presenters (e.g. [VenuesPresenter]).
 *
 * Created by vicsonvictor on 4/21/18.
 */
interface VenuesContract {

    interface View {
        fun onSearch(venues: java.util.ArrayList<Venue>)
        fun onSuggestedSearches(suggestedSearches: List<String>)
        fun onVenue(venue: Venue)
        fun onError()
    }

    interface Presenter {
        fun bindView(view: VenuesContract.View)
        fun unBindView()
        fun doSearch(term: String)
        fun doSuggestedSearch(searchTerm: String)
        fun doGetVenue(venueId: String)
    }
}

