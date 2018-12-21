package com.grace.placessearch.common.app.injection.module

import android.content.Context
import android.util.LruCache
import com.grace.placessearch.BuildConfig
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.common.PlacesSearchEnvironmentEnum
import com.grace.placessearch.common.app.injection.qualifier.ForApplication
import com.grace.placessearch.common.util.PlacesSearchUtil
import com.grace.placessearch.service.PlacesApi
import com.grace.placessearch.service.network.PlacesApiRetrofit
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Module
class PlacesSearchModuleKT(private val context: Context) {

    companion object {
        const val CACHE_SIZE = 5 * 1024 * 1024
    }

    private val mPlacesApi: PlacesApi by lazy {
        Timber.i("Creating new PlacesRetrofit instance.")
        PlacesApiRetrofit(PlacesSearchEnvironmentEnum.PROD.placesBaseUrl, "20180421",
                BuildConfig.FOUR_SQUARE_API_CLIENT_ID, BuildConfig.FOUR_SQUARE_API_CLIENT_SECRET, PlacesSearchUtil.latLngOfUserLocation)
    }

    @ForApplication
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideLruCache(): LruCache<Any, Any> {
        return LruCache(CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun providePlaces(): PlacesApi {
        return mPlacesApi
    }

    @Provides
    @Singleton
    fun providePicasso(client: OkHttpClient): Picasso {
        return Picasso.Builder(context)
                .downloader(OkHttp3Downloader(client))
                .listener { _, uri, exception -> Timber.w(exception, "Failed to load image: %s", uri) }
                .build()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        return createOkHttpClient(context).build()
    }

    /**
     * Creates a cache enabled [OkHttpClient] instance and returns it.
     * Currently, this is being only used for Picasso image handling and caching.
     */
    private fun createOkHttpClient(context: Context): OkHttpClient.Builder {
        // Install an HTTP cache in the application cache directory.
        val cacheDir = File(context.cacheDir, PlacesSearchConstants.HTTP)
        val cache = Cache(cacheDir, PlacesSearchConstants.IMAGE_DISK_CACHE_SIZE.toLong())

        return OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE.toLong(), SECONDS)
                .readTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE.toLong(), SECONDS)
                .writeTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE.toLong(), SECONDS)
    }

}
