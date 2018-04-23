package com.grace.placessearch.search.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.grace.placessearch.R;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.ui.BaseNavigationActivity;
import com.grace.placessearch.ui.view.LoadingIndicatorView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SearchActivity extends BaseNavigationActivity implements SearchContract.View, SearchResultsAdapter.VenueListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.search_edittext)
    EditText searchEditText;

    @Bind(R.id.search_results_recycler_view)
    RecyclerView searchResultsRecyclerView;

    @Bind(R.id.suggested_search_list_recycler_view)
    RecyclerView suggestedSearchesRecyclerView;

    @Bind(R.id.clear_icon)
    ImageView clearIcon;

    @Bind(R.id.loading_indicator)
    LoadingIndicatorView loadingIndicatorView;

    @Bind(R.id.no_results_message)
    TextView noResultsMessageText;

    @Bind(R.id.suggested_searches)
    ViewGroup suggestedSearchesContainer;

    @Bind(R.id.suggested_search_header)
    ViewGroup suggestedSearchHeader;

    @Bind(R.id.suggested_searches_message)
    TextView suggestedSearchesMessage;

    @Inject
    Picasso picasso;

    @Inject
    SearchPresenter searchPresenter;

    @Inject
    PlacesSearchPreferenceManager preferenceManager;

    @Inject
    LruCache lruCache;

    protected RecyclerView.Adapter searchResultsAdapter;
    private LinearLayoutManager searchResultsLayoutManager;
    private String searchInput;

    protected SuggestedSearchRecyclerAdapter suggestedSearchRecyclerAdapter;
    private LinearLayoutManager suggestedSearchTermsLayoutManager;
    private List<String> suggestedSearches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        component().inject(this);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        initToolbar(toolbar);

        initSearchTextView();

        //FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = setupDrawerListeners();

        toggle.syncState();

        //NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupResultsRecylerView();
        setupSuggestedSearchListRecyclerView();

        showNoResultsState(false);

        displaySuggestedSearchViews(false);
    }

    @NonNull
    private ActionBarDrawerToggle setupDrawerListeners() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                hideKeyboard();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                hideKeyboard();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                hideKeyboard();
            }
        });
        return toggle;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("!");
        searchPresenter.bindView(this);
    }

    @Override
    protected void onPause() {
        searchPresenter.unBindView();
        hideKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        super.onPause();
    }

    @OnClick(R.id.clear_icon)
    public void onClearMenuClicked() {
        searchEditText.setText("");
        searchEditText.setSelection(0);
        searchEditText.setCursorVisible(true);
        searchEditText.isEnabled();
//        showingResultsState = false;
        showKeyboard();

        showNoResultsState(false);
        displaySuggestedSearchViews(false);
    }

    @OnClick(R.id.search_edittext)
    public void onSearchEditTextClick() {
        searchEditText.setCursorVisible(true);
        searchEditText.isEnabled();
    }

    @OnClick(R.id.search_icon)
    public void onSearchIconClick() {
        String searchEditTextInput = searchEditText.getText().toString().trim();
        if (searchEditTextInput.equals(searchInput)) {
            return;
        }
        doSearch(searchEditText.getText().toString().trim());
    }

    @NonNull
    private TextView.OnEditorActionListener getOnEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchString = searchEditText.getText().toString().trim();
                    Timber.i("OnEditorActionListener User Action|%s|%s|%s", "Search for Products", "Search String", searchString);
                    doSearch(searchString);
                    return true;
                }
                return false;
            }
        };
    }

    private void initSearchTextView() {
        searchEditText.clearComposingText();
        searchEditText.setText("");
        searchEditText.setSelection(0);

        searchEditText.setOnEditorActionListener(getOnEditorActionListener());
        searchEditText.addTextChangedListener(searchTextWatcher);
    }

    private void showNoResultsState(boolean show) {
        if (show) {
            noResultsMessageText.setVisibility(View.VISIBLE);
            noResultsMessageText.setText(String.format(getString(R.string.search_no_results_response),
                    searchInput));
        } else {
            noResultsMessageText.setVisibility(View.INVISIBLE);
        }
    }

    private final TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // make sure menus are initialized since text watcher is created before menus
            if (clearIcon != null) {
                clearIcon.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }

            searchInput = s.toString();

            if (s.length() != 0) {
//                recentSearchTermsAdapter.setSearchResults(getSavedRecentSearchList(), false, searchInput);
//                displaySuggestedSearchHeader(true);
//            } else {
//                if (!ignoreSearchEditTextUpdate) {
                    searchPresenter.doSuggestedSearch(s.toString());
//                } else {
//                    recentSearchTermsAdapter.setSearchResults(suggestedSearches, true, searchInput);
//                    ignoreSearchEditTextUpdate = false;
//                }
            } else {
                displaySuggestedSearchViews(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Initializes the adapter/recycler view to display results
     */
    private void setupResultsRecylerView() {

        searchResultsAdapter = new SearchResultsAdapter(this, this, preferenceManager, picasso);

        searchResultsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchResultsRecyclerView.setLayoutManager(searchResultsLayoutManager);

        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

//        productsListRecyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) { // skip call on scroll down action
//                    fetchNextPageOfSearchResults();
//                }
//            }
//        });

    }

    /**
     * Initializes the suggested search adapter/recycler view to display results
     */
    private void setupSuggestedSearchListRecyclerView() {
        suggestedSearchTermsLayoutManager = new LinearLayoutManager(this);
        suggestedSearchesRecyclerView.setLayoutManager(suggestedSearchTermsLayoutManager);
        suggestedSearchRecyclerAdapter = new SuggestedSearchRecyclerAdapter(suggestedSearchesRecyclerView, new ArrayList<String>(), suggestedSearchHeader);
        suggestedSearchRecyclerAdapter.setClickListeners(suggestedSearchItemClickListener, backgroundTouchListener);
        suggestedSearchesRecyclerView.setAdapter(suggestedSearchRecyclerAdapter);
    }

    private View.OnTouchListener backgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyboard();
            return false;
        }
    };

    private SuggestedSearchRecyclerAdapter.SuggestedSearchOnClickListener suggestedSearchItemClickListener =
            new SuggestedSearchRecyclerAdapter.SuggestedSearchOnClickListener() {
                @Override
                public void onSuggestedSearchItemClick(String searchTerm) {
//                    if (!recentSearchTermsAdapter.isSuggestedSearches()) {
//                        searchEditText.setText(searchTerm);
//                        searchEditText.setSelection(searchTerm.length());
//                    } else {
//                        keepSuggestedSearchesSearchInput = true;
//                        suggestedSearchesSearchInput = searchInput;
//                    }
                    Timber.i("SuggestedSearchOnClickListener User Action|%s|%s|%s", "Suggested searches", "Search String", searchTerm);
                    displaySuggestedSearchViews(false);
                    doSearch(searchTerm);
                }
            };

    private void doSearch(String searchString) {
        if (searchString.length() > 0) {
            Timber.d("Searching for %s", searchString);
            this.searchInput = searchString;

            // hide suggested searches
            displaySuggestedSearchViews(false);

            // close the keyboard and disable cursor
            hideKeyboard();
            searchEditText.setCursorVisible(false);

            showLoadingIndicator(true);
//            // show loading indicator
//            loadingIndicatorView.setVisibility(View.VISIBLE);

//            // perform search - API call
//            resetPaginationAttributes();
//            originalSearchResponse = null;
            searchPresenter.doSearch(searchString);
        }
    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_search) {
//            // Handle the camera action
//        } else if (id == R.id.nav_favorites) {
//
//        } else if (id == R.id.nav_settings) {
//
//        } else if (id == R.id.nav_view) {
//
//        }
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    private void initToolbar(Toolbar toolbar) {
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setDisplayShowHomeEnabled(true);
//        }
//        toolbar.setNavigationIcon(R.drawable.hamburger_dark);
//    }

    @Override
    public void onSearch(List<Venue> venues) {
        showLoadingIndicator(false);
        if (venues != null && !venues.isEmpty()) {
            showNoResultsState(false);
            Timber.i("Updating data");
            ((SearchResultsAdapter) searchResultsAdapter).updateData(venues);
        } else {
            Timber.i("Empty data or bad response");
            ((SearchResultsAdapter) searchResultsAdapter).updateData(new ArrayList<Venue>());
            showNoResultsState(true);
        }
    }

    @Override
    public void onSuggestedSearches(List<String> suggestedSearches) {
        if (searchInput.isEmpty()) {
            return;
        }
        updateSuggestedSearches(suggestedSearches);
    }

    @Override
    public void onError() {
        showLoadingIndicator(false);
        Snackbar.make(fab, "Something went wrong. Please try again later!", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void onNextError() {

    }

    @Override
    public void onClick(Venue venue) {
        Snackbar.make(fab, "Venue " + venue.getName() + " clicked", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void showLoadingIndicator(boolean show) {
        if (show) {
            loadingIndicatorView.setVisibility(View.VISIBLE);
        } else {
            loadingIndicatorView.setVisibility(View.GONE);
        }
    }

    private void updateSuggestedSearches(List<String> suggestedSearches) {
        this.suggestedSearches = suggestedSearches;
        displaySuggestedSearchViews(true);
        suggestedSearchRecyclerAdapter.setSearchResults(suggestedSearches, searchInput);

        if (suggestedSearchRecyclerAdapter.isSearchResultsEmpty()) {
            suggestedSearchesMessage.setText(R.string.no_suggested_searches);
            suggestedSearchesMessage.setVisibility(View.VISIBLE);
        } else {
            suggestedSearchesMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void displaySuggestedSearchViews(boolean show) {
        if (show) {
            suggestedSearchesContainer.setVisibility(View.VISIBLE);
        } else {
            suggestedSearchesContainer.setVisibility(View.INVISIBLE);
            if (suggestedSearchRecyclerAdapter != null) {
                suggestedSearchRecyclerAdapter.clearSearchResults();
            }
        }
    }

}
