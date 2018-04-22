package com.grace.placessearch.common.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.LruCache;

import com.grace.placessearch.BuildConfig;
import com.grace.placessearch.common.app.injection.component.DaggerPlacesSearchComponent;
import com.grace.placessearch.common.app.injection.component.PlacesSearchComponent;
import com.grace.placessearch.common.app.injection.module.PlacesSearchModule;
import com.grace.placessearch.common.log.DebugTree;
import com.grace.placessearch.common.service.PlacesSearchStartupIntentService;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by vicsonvictor on 4/21/18.
 */

public class PlacesSearchApplication extends Application {

    private static final String TAG = PlacesSearchApplication.class.getSimpleName();

    @Inject
    LruCache lruCache;

    private PlacesSearchComponent component;

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);

        component().inject(this);

        setupTimber();

        Timber.i("completed!");

    }

    @Override
    public void onCreate() {

        super.onCreate();

        initAppStartupData();
    }

    public PlacesSearchComponent component() {
        if (component == null) {
            component = DaggerPlacesSearchComponent.builder().placesSearchModule(new PlacesSearchModule(this)).build();
        }
        return component;
    }

    private void setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree(TAG));
        } else {
            // TODO - VV - Set up Crashlytics tree as needed for release version.
            Timber.plant(new DebugTree(TAG));
        }
    }

    private void initAppStartupData() {

        // Start start-up intent service, if not already running
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        boolean startUpServiceRunning = false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.grace.seattleplacesearch.common.service.PlacesSearchStartupIntentService".equals(service.service.getClassName())) {
                Timber.i("PlacesSearchStartupIntentService is already running");
                startUpServiceRunning = true;
            }
        }

        if (!startUpServiceRunning) {
            Intent serviceIntent = new Intent(getApplicationContext(), PlacesSearchStartupIntentService.class);
            Timber.i("Starting PlacesSearchStartupIntentService ....");
            startService(serviceIntent);
        }

    }


}
