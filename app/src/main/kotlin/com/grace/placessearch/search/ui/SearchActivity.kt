package com.grace.placessearch.search.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.LruCache
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.grace.placessearch.R
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.ui.BaseNavigationActivity
import com.grace.placessearch.data.model.MapPin
import com.grace.placessearch.data.model.Venue
import com.grace.placessearch.maps.ui.VenuesMapActivity
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_search.*
import kotlinx.android.synthetic.main.content_search.*
import kotlinx.android.synthetic.main.search_toolbar.*
import kotlinx.android.synthetic.main.suggested_searches.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SearchActivity : BaseNavigationActivity(), VenuesContract.View, SearchResultsAdapter.VenueListener {

    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var venuesPresenter: VenuesPresenter
    @Inject
    lateinit var preferenceManager: PlacesSearchPreferenceManager
    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    lateinit var searchResultsAdapter: SearchResultsAdapter
    lateinit var suggestedSearchRecyclerAdapter: SuggestedSearchResultsAdapter

    private lateinit var searchResultsLayoutManager: LinearLayoutManager
    private lateinit var searchInput: String
    private lateinit var suggestedSearchTermsLayoutManager: LinearLayoutManager
    private var searchResults: List<Venue> = ArrayList()

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // make sure menus are initialized since text watcher is created before menus
            clear_icon.visibility = when (s.isNotEmpty()) {
                true -> View.VISIBLE
                false -> View.INVISIBLE
            }

            searchInput = s.toString()

            when (s.isNotEmpty()) {
                true -> {
                    displayNoResultsState(false)
                    venuesPresenter.doSuggestedSearch(s.toString())
                }
                false -> displaySuggestedSearchViews(false)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun displayNoResultsState(show: Boolean) {
        when (show) {
            true -> {
                no_results_message.visibility = View.VISIBLE
                no_results_message.text = String.format(getString(R.string.search_no_results_response),
                        searchInput)
            }
            false -> no_results_message.visibility = View.INVISIBLE
        }
    }

    private val backgroundTouchListener = View.OnTouchListener { v, event ->
        hideKeyboard()
        false
    }

    private val suggestedSearchItemClickListener = object : SuggestedSearchResultsAdapter.SuggestedSearchOnClickListener {
        override fun onSuggestedSearchItemClick(searchTerm: String) {
            Timber.i("SuggestedSearchOnClickListener User Action|%s|%s|%s", "Suggested searches", "Search String", searchTerm)
            displaySuggestedSearchViews(false)
            doSearch(searchTerm)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)

        component().inject(this)

        setupNavigationView()

        setupDrawerListeners()

        initToolbar(findViewById<View>(R.id.toolbar) as Toolbar)

        initSearchTextView()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        setupSearchResultsRecyclerView()
        setupSuggestedSearchResultsRecyclerView()

        displayNoResultsState(false)

        displaySuggestedSearchViews(false)
    }

    override fun onPause() {
        venuesPresenter.unBindView()
        hideKeyboard()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        venuesPresenter.bindView(this)
    }

    @OnClick(R.id.clear_icon)
    fun onClearMenuClicked() {
        search_edittext.setText("")
        search_edittext.setSelection(0)
        search_edittext.isCursorVisible = true
        search_edittext.isEnabled = true
        showKeyboard()

        displayNoResultsState(false)
        displaySuggestedSearchViews(false)
    }

    @OnClick(R.id.search_edittext)
    fun onSearchEditTextClicked() {
        search_edittext.isCursorVisible = true
        search_edittext.isEnabled = true
    }

    @OnClick(R.id.search_icon)
    fun onSearchIconClicked() {
        val searchEditTextInput = search_edittext.text.toString().trim { it <= ' ' }
        if (searchEditTextInput == searchInput) {
            return
        }
        displayMapFab(false)
        doSearch(search_edittext.text.toString().trim { it <= ' ' })
    }

    @OnClick(R.id.map_fab)
    fun mapFabClicked() {
        val intent = Intent(this, VenuesMapActivity::class.java)
        intent.putParcelableArrayListExtra(PlacesSearchConstants.MAP_PINS_EXTRA, getMapPinsOfSearchResults())
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    private fun getMapPinsOfSearchResults(): ArrayList<MapPin> {
        val mapPins = ArrayList<MapPin>()
        for (i in searchResults.indices) {
            val venue = searchResults[i]
            if (venue.location != null) {
                val mapPin = MapPin()
                mapPin.venueId = venue.id
                mapPin.pinName = venue.name
                mapPin.lat = venue.location.lat
                mapPin.lng = venue.location.lng
                mapPin.imgUrl = venue.mapPinUrl
                mapPins.add(mapPin)
            }
        }
        return mapPins
    }

    private fun doSearch(searchString: String) {
        if (searchString.isNotEmpty()) {
            Timber.d("Searching for %s", searchString)
            this.searchInput = searchString

            // hide suggested searches
            displaySuggestedSearchViews(false)

            // close the keyboard and disable cursor
            hideKeyboard()
            search_edittext.isCursorVisible = false

            displayLoadingIndicator(true)
            displayMapFab(false)
            venuesPresenter.doSearch(searchString)
        }
    }

    override fun onSearch(venues: ArrayList<Venue>) {
        displayLoadingIndicator(false)
        if (venues != null && !venues.isEmpty()) {
            displayNoResultsState(false)
            this.searchResults = venues
            Timber.d("Updating data - size: %d", venues.size)
            displayMapFab(true)
            searchResultsLayoutManager.scrollToPosition(0)
            searchResultsAdapter.updateData(venues)
        } else {
            Timber.i("Empty data or bad response")
            searchResultsLayoutManager.scrollToPosition(0)
            searchResultsAdapter.updateData(ArrayList())
            displayNoResultsState(true)
            displayMapFab(false)
        }
    }

    override fun onSuggestedSearches(suggestedSearches: List<String>) {
        if (searchInput.isEmpty()) {
            return
        }
        updateSuggestedSearches(suggestedSearches)
    }

    override fun onVenue(venue: Venue) {
        // Not applicable
    }

    override fun onError() {
        displayLoadingIndicator(false)
        Snackbar.make(map_fab, "Something went wrong. Please try again later!", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
    }

    override fun onVenueItemClicked(venue: Venue) {
        val intent = Intent(this, VenueDetailsActivity::class.java)
        intent.putExtra(PlacesSearchConstants.VENUE_NAME_EXTRA, venue.name)
        intent.putExtra(PlacesSearchConstants.VENUE_ID_EXTRA, venue.id)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    /**
     * Initializes the adapter/recycler view to display results
     */
    private fun setupSearchResultsRecyclerView() {
        searchResultsAdapter = SearchResultsAdapter(this, this, preferenceManager, picasso)
        searchResultsLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        search_results_recycler_view.setLayoutManager(searchResultsLayoutManager)
        search_results_recycler_view.setAdapter(searchResultsAdapter)
    }

    /**
     * Initializes the suggested search adapter/recycler view to display results
     */
    private fun setupSuggestedSearchResultsRecyclerView() {
        suggestedSearchTermsLayoutManager = LinearLayoutManager(this)
        suggested_search_list_recycler_view.layoutManager = suggestedSearchTermsLayoutManager
        suggestedSearchRecyclerAdapter = SuggestedSearchResultsAdapter(suggested_search_list_recycler_view, ArrayList())
        suggestedSearchRecyclerAdapter.setClickListeners(suggestedSearchItemClickListener, backgroundTouchListener)
        suggested_search_list_recycler_view.adapter = suggestedSearchRecyclerAdapter
    }

    private fun displayMapFab(show: Boolean) {
        when (show) {
            true -> map_fab.visibility = View.VISIBLE
            false -> map_fab.visibility = View.VISIBLE
        }
    }

    private fun initSearchTextView() {
        search_edittext.clearComposingText()
        search_edittext.setText("")
        search_edittext.setSelection(0)

        search_edittext.setOnEditorActionListener(getOnEditorActionListener())
        search_edittext.addTextChangedListener(searchTextWatcher)
    }

    fun displayLoadingIndicator(show: Boolean) {
        when (show) {
            true -> loading_indicator.visibility = View.VISIBLE
            false -> loading_indicator.visibility = View.GONE
        }
    }

    private fun updateSuggestedSearches(suggestedSearches: List<String>) {
        displaySuggestedSearchViews(true)
        suggestedSearchRecyclerAdapter.setSearchResults(suggestedSearches, searchInput)

        when (suggestedSearchRecyclerAdapter.isSearchResultsEmpty()) {
            true -> {
                suggested_searches_message.text = resources.getString(R.string.no_suggested_searches)
                suggested_searches_message.visibility = View.VISIBLE
            }
            false -> suggested_searches_message.visibility = View.INVISIBLE
        }
    }

    private fun displaySuggestedSearchViews(show: Boolean) {
        when (show) {
            true -> suggested_searches.visibility = View.VISIBLE
            false -> {
                suggested_searches.visibility = View.INVISIBLE
                if (suggestedSearchRecyclerAdapter != null) {
                    suggestedSearchRecyclerAdapter.clearSearchResults()
                }
            }
        }
    }

    private fun getOnEditorActionListener(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchString = search_edittext.text.toString().trim { it <= ' ' }
                Timber.i("OnEditorActionListener User Action|%s|%s|%s", "Search for Venues", "Search String", searchString)
                doSearch(searchString)
                return@OnEditorActionListener true
            }
            false
        }
    }
}