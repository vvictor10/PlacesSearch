package com.grace.placessearch.common.app.injection.component;

import android.util.LruCache;

import com.grace.placessearch.common.app.PlacesSearchApplication;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.common.app.injection.module.PlacesSearchModule;
import com.grace.placessearch.common.service.PlacesSearchStartupIntentService;
import com.grace.placessearch.common.data.VenuesDataManager;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Singleton
@Component(modules = {PlacesSearchModule.class})
public interface PlacesSearchComponent {

    // provides
    PlacesSearchPreferenceManager providePlacesSearchPreferenceManager();

    VenuesDataManager provideSearchDataManager();

    Picasso providePicasso();

    LruCache provideLruCache();

    // injects
    void inject(PlacesSearchApplication app);

    void inject(PlacesSearchStartupIntentService placesSearchStartupIntentService);

}
