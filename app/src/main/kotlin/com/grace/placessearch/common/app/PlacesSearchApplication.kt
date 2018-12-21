package com.grace.placessearch.common.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.LruCache
import com.grace.placessearch.BuildConfig
import com.grace.placessearch.common.app.injection.component.DaggerPlacesSearchComponent
import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent
import com.grace.placessearch.common.app.injection.module.PlacesSearchModule
import com.grace.placessearch.common.log.DebugTree
import com.grace.placessearch.common.service.PlacesSearchStartupIntentService
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by vicsonvictor on 4/21/18.
 */
class PlacesSearchApplication : Application() {

    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    private val component: PlacesSearchComponent by lazy {
        DaggerPlacesSearchComponent.builder().placesSearchModule(PlacesSearchModule(this)).build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        component.inject(this)
        setupTimber()
    }

    override fun onCreate() {
        super.onCreate()
        initiateAppStartupSequence()
    }

    fun component(): PlacesSearchComponent {
        return component
    }

    private fun setupTimber() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(DebugTree(TAG))
            false -> Timber.plant(DebugTree(TAG))    // TODO - VV - Also Set up Crashlytics tree as needed for release version.
        }
    }

    /**
     * Triggers the [PlacesSearchStartupIntentService] to initiate the app
     * start up data fetch sequence.
     */
    private fun initiateAppStartupSequence() {

        // Start start-up intent service, if not already running
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var startUpServiceRunning = false
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.grace.placessearch.common.service.PlacesSearchStartupIntentService" == service.service.className) {
                Timber.d("PlacesSearchStartupIntentService is already running")
                startUpServiceRunning = true
            }
        }

        if (!startUpServiceRunning) {
            val serviceIntent = Intent(applicationContext, PlacesSearchStartupIntentService::class.java)
            Timber.i("Starting PlacesSearchStartupIntentService ....")
            startService(serviceIntent)
        }

    }

    companion object {
        private val TAG = PlacesSearchApplication::class.java.simpleName
    }

}
