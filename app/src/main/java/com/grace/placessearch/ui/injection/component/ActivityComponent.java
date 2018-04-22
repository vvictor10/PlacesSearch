package com.grace.placessearch.ui.injection.component;

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent;
import com.grace.placessearch.search.ui.SearchActivity;
import com.grace.placessearch.ui.injection.scope.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = PlacesSearchComponent.class)
public interface ActivityComponent {

    void inject(SearchActivity activity);

}
