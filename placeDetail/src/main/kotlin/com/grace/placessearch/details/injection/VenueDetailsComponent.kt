package com.grace.placessearch.details.injection

import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent
import com.grace.placessearch.common.ui.injection.scope.ActivityScope
import com.grace.placessearch.details.ui.VenueDetailsActivity
import dagger.Component

/**
 * This class helps to provide dependencies for classes defined in the 'placeDetail' feature module.
 *
 * The base module is not aware of the classes defined in the feature module. However, the feature module
 * knows about the classes defined in the base module. Hence, its possible for us to define a component,
 * which can satisfy dependencies(and other instances defined in the base component's dagger module)
 * which are declared in the base module as follows.
 *
 *     private fun venueDetailsComponent(): VenueDetailsComponent {

            // Gets appComponent from MyApplication available in the base Gradle module
            val appComponent = (applicationContext as PlacesSearchApplication).component()

            // Creates a new instance of VenueDetailsComponent
            return DaggerVenueDetailsComponent.factory().create(appComponent)
        }
 *
 * https://developer.android.com/training/dependency-injection/dagger-multi-module?hl=ru#kotlin
 *
 * https://github.com/googlesamples/android-dynamic-code-loading
 */
@ActivityScope
@Component(dependencies = [PlacesSearchComponent::class])
interface VenueDetailsComponent {

    @Component.Factory
    interface Factory {

        // Takes an instance of AppComponent when creating
        // an instance of VenueDetailsComponent
        fun create(appComponent: PlacesSearchComponent): VenueDetailsComponent
    }

    fun inject(activity: VenueDetailsActivity)
}
