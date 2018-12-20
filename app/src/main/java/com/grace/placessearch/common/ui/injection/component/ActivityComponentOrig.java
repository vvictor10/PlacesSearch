package com.grace.placessearch.common.ui.injection.component;

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponentKT;
import com.grace.placessearch.common.ui.injection.scope.ActivityScope;
import com.grace.placessearch.maps.ui.VenuesMapActivity;
import com.grace.placessearch.maps.ui.VenuesMapActivityOrig;
import com.grace.placessearch.search.ui.SearchActivity;
import com.grace.placessearch.venue.detail.ui.VenueDetailActivity;
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivity;

import dagger.Component;

@Deprecated
@ActivityScope
@Component(dependencies = PlacesSearchComponentKT.class)
public interface ActivityComponentOrig {

    void inject(SearchActivity activity);

    void inject(VenuesMapActivity activity);

    void inject(VenuesMapActivityOrig activity);

    void inject(VenueDetailsActivity activity);

    void inject(VenueDetailActivity activity);
}
