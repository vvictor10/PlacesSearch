package com.grace.placessearch.common.app.injection.component

import android.util.LruCache
import com.grace.placessearch.common.app.PlacesSearchApplication
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.app.injection.module.PlacesSearchModuleKT
import com.grace.placessearch.common.data.VenuesDataManager
import com.grace.placessearch.common.service.PlacesSearchStartupIntentService
import com.squareup.picasso.Picasso
import dagger.Component
import javax.inject.Singleton

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Singleton
@Component(modules = [PlacesSearchModuleKT::class])
interface PlacesSearchComponentKT {

    // provides
//    fun provideContext(): Context

    fun providePlacesSearchPreferenceManager(): PlacesSearchPreferenceManager

    fun provideSearchDataManager(): VenuesDataManager

    fun providePicasso(): Picasso

    fun provideLruCache(): LruCache<Any, Any>

    // injects
    fun inject(app: PlacesSearchApplication)

    fun inject(placesSearchStartupIntentService: PlacesSearchStartupIntentService)

}
