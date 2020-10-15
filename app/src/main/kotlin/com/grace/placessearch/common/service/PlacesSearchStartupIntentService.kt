package com.grace.placessearch.common.service

import android.content.Context
import android.content.Intent
import android.util.LruCache
import androidx.core.app.JobIntentService
import com.grace.placessearch.common.app.PlacesSearchApplication
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.data.PlacesSearchStartupManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by vicsonvictor on 4/21/18.
 */

class PlacesSearchStartupIntentService : JobIntentService() {

    @Inject
    lateinit var placesSearchStartupManager: PlacesSearchStartupManager

    @Inject
    lateinit var preferenceManager: PlacesSearchPreferenceManager

    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    override fun onHandleWork(intent: Intent) {
        Timber.d("Handling intent..")
        placesSearchStartupManager.fetchAndCacheData()
    }

    override fun onCreate() {
        super.onCreate()
        (application as PlacesSearchApplication).component().inject(this)
    }

}
