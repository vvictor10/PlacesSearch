package com.grace.placessearch.common.ui.injection.component

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent
import com.grace.placessearch.common.ui.injection.scope.ActivityScope
import com.grace.placessearch.maps.ui.VenuesMapActivity
import com.grace.placessearch.search.ui.SearchActivity
import dagger.Component

@ActivityScope
@Component(dependencies = [PlacesSearchComponent::class])
interface ActivityComponent {

    fun inject(activity: SearchActivity)

    fun inject(activity: VenuesMapActivity)

}
