/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grace.placessearch.venue.detail.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.grace.placessearch.R;
import com.grace.placessearch.common.PlacesSearchConstantsOrig;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.common.ui.BaseNavigationActivity;
import com.grace.placessearch.common.ui.view.LoadingIndicatorView;
import com.grace.placessearch.common.ui.view.ViewUtils;
import com.grace.placessearch.common.util.PlacesSearchUtil;
import com.grace.placessearch.data.model.Category;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.search.ui.VenuesContract;
import com.grace.placessearch.search.ui.VenuesPresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A Map activity displaying the pins passed in the Intent Extra.
 */
@Deprecated
public class VenueDetailsActivity extends BaseNavigationActivity implements VenuesContract.View {

    @Bind(R.id.favorite_status)
    public ImageView favoriteStatusImage;
    @Bind(R.id.non_favorite_status)
    public ImageView nonFavoriteStatusImage;
    @Bind(R.id.directions_image)
    public ImageView directionsImage;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.map_image)
    ImageView mapImage;
    @Bind(R.id.loading_indicator)
    LoadingIndicatorView loadingIndicatorView;
    @Bind(R.id.address_card_view)
    CardView addressCardView;
    @Bind(R.id.venue_categories)
    TextView venueCategories;
    @Bind(R.id.venue_url)
    TextView venueUrl;
    @Bind(R.id.addr_line_1)
    TextView addrLine1;
    @Bind(R.id.addr_line_2)
    TextView addrLine2;
    @Bind(R.id.addr_line_3)
    TextView addrLine3;
    @Bind(R.id.primary_details_card_view)
    CardView primaryDetailsCardView;
    @Bind(R.id.about_card_view)
    CardView aboutCardView;

    @Inject
    Picasso picasso;

    @Inject
    VenuesPresenter venuesPresenter;

    @Inject
    PlacesSearchPreferenceManager preferenceManager;

    @Inject
    LruCache<Object, Object> lruCache;

    private int imageWidth;
    private int imageHeight;
    private boolean isFavorite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);
        ButterKnife.bind(this);

        component().inject(this);

        setupNavigationView();

        setupDrawerListeners();

        initToolbar(toolbar);

        venuesPresenter.bindView(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        imageWidth = displayMetrics.widthPixels;
        imageHeight = (int) (imageWidth * 1.0); //aspect ratio
        setViewHeight(mapImage, imageHeight);

        String venueId = getIntent().getStringExtra(PlacesSearchConstantsOrig.VENUE_ID_EXTRA);

        loadingIndicatorView.setVisibility(View.VISIBLE);
        venuesPresenter.doGetVenue(venueId);
    }

    @Override
    protected void onPause() {
        venuesPresenter.unBindView();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.appbar_back_white);
    }

    private void setViewHeight(final View view, final int viewHeight) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                if (view.getHeight() != viewHeight) {
                    view.getLayoutParams().height = viewHeight;
                    view.requestLayout();
                }
                return true;
            }
        });
    }

    @Override
    public void onVenue(final Venue venue) {
        if (venue != null) {

            toolbarTitle.setText(venue.getName());

            loadMapImage(mapImage, venue);
            setupVenueUrl(venue);

            if (!venue.getCategories().isEmpty()) {
                venueCategories.setText(getCategoriesString(venue.getCategories()));
            } else {
                venueCategories.setVisibility(View.GONE);
            }

            setupAddressInfo(venue);
            setInitialFavoriteStatus(preferenceManager, venue);
        }
    }

    @Override
    public void onError() {
        loadingIndicatorView.setVisibility(View.INVISIBLE);
        primaryDetailsCardView.setVisibility(View.INVISIBLE);
        addressCardView.setVisibility(View.INVISIBLE);
        aboutCardView.setVisibility(View.INVISIBLE);
        displayCustomToast("Something went wrong. Please try later.");
    }

    @Override
    public void onSearch(List<Venue> venues) {
        // Not applicable
    }

    @Override
    public void onSuggestedSearches(List<String> suggestedSearches) {
        // Not applicable
    }

    private void setupAddressInfo(final Venue venue) {
        if (venue.getLocation() != null && !venue.getLocation().getFormattedAddress().isEmpty()) {
            for (int i = 0; i < venue.getLocation().getFormattedAddress().size(); i++) {
                String formattedAddress = venue.getLocation().getFormattedAddress().get(i);
                if (i == 0) {
                    addrLine1.setText(formattedAddress);
                } else if (i == 1) {
                    addrLine2.setText(formattedAddress);
                } else if (i == 2) {
                    addrLine3.setText(formattedAddress);
                }
            }
            directionsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchMapsForDirections(venue);
                }
            });

        } else {
            addressCardView.setVisibility(View.GONE);
        }
    }

    private void setupVenueUrl(final Venue venue) {
        if (venue.getUrl() != null) {
            venueUrl.setText(venue.getUrl());
            venueUrl.setPaintFlags(venueUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            venueUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchUrl(venue.getUrl());
                }
            });
        } else {
            venueUrl.setVisibility(View.GONE);
        }
    }

    private void launchMapsForDirections(Venue venue) {
        StringBuilder staticMapImageUrlBuilder = new StringBuilder();
        staticMapImageUrlBuilder.append("https://www.google.com/maps/dir/?api=1&travelmode=driving&origin=");
        staticMapImageUrlBuilder.append(PlacesSearchConstantsOrig.USER_LOCATION_LAT + "," + PlacesSearchConstantsOrig.USER_LOCATION_LNG);
        staticMapImageUrlBuilder.append("&destination=" + String.format("%.4f", venue.getLocation().getLat()));
        staticMapImageUrlBuilder.append("," + String.format("%.4f", venue.getLocation().getLng()));
        staticMapImageUrlBuilder.append("&key=AIzaSyBJ8FpHurNJQ0oyEhxY4U1HMAZ2xF_pv9w");

        final String mapUrl = staticMapImageUrlBuilder.toString();

        launchUrl(mapUrl);
    }

    private String getCategoriesString(List<Category> categories) {
        if (categories.isEmpty()) {
            return "";
        }

        StringBuilder categoriesString = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            categoriesString.append(categories.get(i).getName());
            if (i != categories.size() - 1) {
                categoriesString.append(", ");
            }
        }

        return categoriesString.toString();
    }

    private void loadMapImage(ImageView imageView, Venue venue) {

        if (venue.getLocation() == null) {
            return;
        }

        final String imageUrl = getMapImageUrl(venue);
        if (imageUrl == null) {
            return;
        }

        picasso.load(imageUrl).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Timber.d("Image loaded for url %s", imageUrl);
                loadingIndicatorView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                Timber.w("Failed to load map image");
            }
        });
    }

    @NonNull
    private String getMapImageUrl(Venue venue) {
        StringBuilder staticMapImageUrlBuilder = new StringBuilder();
        staticMapImageUrlBuilder.append("https://maps.googleapis.com/maps/api/staticmap?size=");
        staticMapImageUrlBuilder.append(imageWidth + "x" + imageHeight + "&maptype=roadmap&markers=color:red%7Clabel:A%7C");
        staticMapImageUrlBuilder.append(PlacesSearchConstantsOrig.USER_LOCATION_LAT + "," + PlacesSearchConstantsOrig.USER_LOCATION_LNG);
        staticMapImageUrlBuilder.append("&markers=color:red%7Clabel:B%7C" + String.format("%.4f", venue.getLocation().getLat()));
        staticMapImageUrlBuilder.append("," + String.format("%.4f", venue.getLocation().getLng()));
        staticMapImageUrlBuilder.append("&key=AIzaSyBJ8FpHurNJQ0oyEhxY4U1HMAZ2xF_pv9w");

        return staticMapImageUrlBuilder.toString();
    }

    private void setInitialFavoriteStatus(final PlacesSearchPreferenceManager placesPreferenceManager, final Venue venue) {
        isFavorite = PlacesSearchUtil.INSTANCE.isFavorite(placesPreferenceManager, venue.getId());
        if (isFavorite) {
            animateFavoriteStatus(true, favoriteStatusImage, nonFavoriteStatusImage);
        } else {
            animateFavoriteStatus(true, nonFavoriteStatusImage, favoriteStatusImage);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                if (isFavorite) {
                    PlacesSearchUtil.INSTANCE.addFavorite(placesPreferenceManager, venue.getId());
                    animateFavoriteStatus(false, favoriteStatusImage, nonFavoriteStatusImage);
                } else {
                    PlacesSearchUtil.INSTANCE.removeFavorite(placesPreferenceManager, venue.getId());
                    animateFavoriteStatus(false, nonFavoriteStatusImage, favoriteStatusImage);
                }
            }
        };

        nonFavoriteStatusImage.setOnClickListener(onClickListener);
        favoriteStatusImage.setOnClickListener(onClickListener);
    }

    private void animateFavoriteStatus(boolean isInitAnimation, View viewToDisplay, final View viewToHide) {

        if (viewToHide.getVisibility() != View.VISIBLE) {
            return;
        }

        // Skip scale down animation for nonFavoriteStatusImage.
        if (viewToHide == favoriteStatusImage) {

            // Animate the 'view-to-hide' to 0% opacity. After the animation ends,
            // set its visibility to GONE as an optimization step (it won't
            // participate in layout passes, etc.)
            viewToHide.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(PlacesSearchConstantsOrig.HEART_CROSS_FADE_ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            viewToHide.setVisibility(View.GONE);
                        }
                    });
        }

        // Set the 'view-to-display' to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewToDisplay.setScaleX(0f);
        viewToDisplay.setScaleY(0f);
        viewToDisplay.setVisibility(View.VISIBLE);

        // Animate the 'view-to-display' to 100% opacity, and clear any animation
        // listener set on the view.

        // If displaying 'non-favorite' status or initial display of 'favorite'
        // status - animate alpha value instantly
        int duration = PlacesSearchConstantsOrig.HEART_CROSS_FADE_ANIMATION_DURATION;
        if (viewToDisplay == nonFavoriteStatusImage || isInitAnimation) {
            duration = 0;
        }

        viewToDisplay.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration)
                .setListener(null);
    }

    protected void launchUrl(String urlToLoad) {

        if (ViewUtils.isChromeTabSupported(this)) { // Chrome Custom tab supported

            Timber.d("Launching using Chrome custom tab.");

            // Launch in Chrome CustomTabs
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // set toolbar color
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.primary_dark));
            builder.setShowTitle(true);
            customTabsIntent.launchUrl(this, Uri.parse(urlToLoad));

        } else { // Chrome Custom tabs not supported

            Timber.d("Launching using browser as Chrome custom tab is not supported.");

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToLoad));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(browserIntent);
        }
    }

}
