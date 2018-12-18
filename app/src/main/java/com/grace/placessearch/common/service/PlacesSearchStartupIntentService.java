package com.grace.placessearch.common.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.LruCache;

import com.grace.placessearch.common.app.PlacesSearchApplication;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.common.data.PlacesSearchStartupManager;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by vicsonvictor on 4/21/18.
 */

public class PlacesSearchStartupIntentService extends IntentService {

    @Inject
    PlacesSearchStartupManager placesSearchStartupManager;

    @Inject
    PlacesSearchPreferenceManager preferenceManager;

    @Inject
    LruCache<Object, Object> lruCache;

    public PlacesSearchStartupIntentService() {
        super(PlacesSearchStartupIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((PlacesSearchApplication) getApplication()).component().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Timber.d("Handling intent..");
        placesSearchStartupManager.fetchAndCacheData();
    }

}
