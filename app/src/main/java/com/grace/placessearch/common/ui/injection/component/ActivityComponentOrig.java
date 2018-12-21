package com.grace.placessearch.common.ui.injection.component;

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent;
import com.grace.placessearch.common.ui.injection.scope.ActivityScope;
import com.grace.placessearch.maps.ui.VenuesMapActivity;
import com.grace.placessearch.maps.ui.VenuesMapActivityOrig;
import com.grace.placessearch.search.ui.SearchActivityOrig;
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivity;
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivityOrig;

import dagger.Component;

@Deprecated
@ActivityScope
@Component(dependencies = PlacesSearchComponent.class)
public interface ActivityComponentOrig {

    void inject(SearchActivityOrig activity);

    void inject(VenuesMapActivity activity);

    void inject(VenuesMapActivityOrig activity);

    void inject(VenueDetailsActivityOrig activity);

    void inject(VenueDetailsActivity activity);
}
