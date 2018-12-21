package com.grace.placessearch.common.ui.injection.component

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponentKT
import com.grace.placessearch.common.ui.injection.scope.ActivityScope
import com.grace.placessearch.maps.ui.VenuesMapActivity
import com.grace.placessearch.maps.ui.VenuesMapActivityOrig
import com.grace.placessearch.search.ui.SearchActivity
import com.grace.placessearch.search.ui.SearchActivityOrig
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivity
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivityOrig
import dagger.Component

@ActivityScope
@Component(dependencies = [PlacesSearchComponentKT::class])
interface ActivityComponent {

    fun inject(activity: SearchActivityOrig)

    fun inject(activity: SearchActivity)

    fun inject(activity: VenuesMapActivity)

    fun inject(activity: VenuesMapActivityOrig)

    fun inject(activity: VenueDetailsActivityOrig)

    fun inject(activity: VenueDetailsActivity)
}
