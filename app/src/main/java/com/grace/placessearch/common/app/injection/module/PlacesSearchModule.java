package com.grace.placessearch.common.app.injection.module;

import static java.util.concurrent.TimeUnit.SECONDS;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.grace.placessearch.BuildConfig;
import com.grace.placessearch.common.PlacesSearchConstants;
import com.grace.placessearch.common.PlacesSearchEnvironmentEnum;
import com.grace.placessearch.common.app.injection.qualifier.ForApplication;
import com.grace.placessearch.common.util.PlacesSearchUtil;
import com.grace.placessearch.service.PlacesApi;
import com.grace.placessearch.service.network.PlacesApiRetrofit;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Module
public class PlacesSearchModule {

    private final int CACHE_SIZE = 5 * 1024 * 1024;

    private final Context context;
    private PlacesApi mPlacesApi;

    public PlacesSearchModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public LruCache provideLruCache() {
        return new LruCache(CACHE_SIZE);
    }

    @Provides
    @Singleton
    public PlacesApi providePlaces() {
        return getPlacesInstance();
    }

    @Provides
    @Singleton
    public Picasso providePicasso(OkHttpClient client) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Timber.w(exception, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return createOkHttpClient(context).build();
    }

    @NonNull
    private PlacesApi getPlacesInstance() {
        if (mPlacesApi == null) {
            mPlacesApi = new PlacesApiRetrofit(PlacesSearchEnvironmentEnum.PROD.placesBaseUrl, "20180421",
                    BuildConfig.FOUR_SQUARE_API_CLIENT_ID, BuildConfig.FOUR_SQUARE_API_CLIENT_SECRET, PlacesSearchUtil.getLatLngOfUserLocation());
            Timber.i("Created new PlacesRetrofit instance.");
        }
        return mPlacesApi;
    }

    /**
     * Creates a cache enabled {@link OkHttpClient} instance and returns it.
     * Currently, this is being only used for Picasso image handling and caching.
     */
    private static OkHttpClient.Builder createOkHttpClient(Context context) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(context.getCacheDir(), PlacesSearchConstants.HTTP);
        Cache cache = new Cache(cacheDir, PlacesSearchConstants.IMAGE_DISK_CACHE_SIZE);

        return new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE, SECONDS)
                .readTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE, SECONDS)
                .writeTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE, SECONDS);
    }

}
