package com.grace.placessearch.common.ui.injection.component;

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent;
import com.grace.placessearch.maps.ui.VenuesMapActivity;
import com.grace.placessearch.search.ui.SearchActivity;
import com.grace.placessearch.common.ui.injection.scope.ActivityScope;
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = PlacesSearchComponent.class)
public interface ActivityComponent {

    void inject(SearchActivity activity);

    void inject(VenuesMapActivity activity);

    void inject(VenueDetailsActivity activity);
}
