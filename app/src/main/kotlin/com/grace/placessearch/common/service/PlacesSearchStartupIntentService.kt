package com.grace.placessearch.common.service

import android.app.IntentService
import android.content.Intent
import android.util.LruCache
import com.grace.placessearch.common.app.PlacesSearchApplication
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.data.PlacesSearchStartupManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by vicsonvictor on 4/21/18.
 */

class PlacesSearchStartupIntentService : IntentService(PlacesSearchStartupIntentService::class.java.name) {

    @Inject
    lateinit var placesSearchStartupManager: PlacesSearchStartupManager

    @Inject
    lateinit var preferenceManager: PlacesSearchPreferenceManager

    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    override fun onCreate() {
        super.onCreate()
        (application as PlacesSearchApplication).component().inject(this)
    }

    override fun onHandleIntent(workIntent: Intent?) {
        Timber.d("Handling intent..")
        placesSearchStartupManager.fetchAndCacheData()
    }

}
