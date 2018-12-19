package com.grace.placessearch.service.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.grace.placessearch.data.model.SuggestedVenuesResponse
import com.grace.placessearch.data.model.VenueResponse
import com.grace.placessearch.data.model.VenuesResponse
import com.grace.placessearch.service.PlacesApi
import com.grace.placessearch.service.VenuesService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.Result
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by vicsonvictor on 4/21/18.
 */

class PlacesApiRetrofit(private val baseUrl: String, private val apiVersion: String, private val clientId: String, private val clientSecret: String, private val currentLatLong: String) : PlacesApi {

    private val retrofit: Retrofit

    private var callAdapterFactory: CallAdapter.Factory? = null

    private val gsonInstance: Gson
        get() = GsonBuilder().create()

    init {
        retrofit = newInstance(baseUrl)
    }

    private fun newInstance(baseUrl: String): Retrofit {
        val gson = gsonInstance

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { chain ->
                    var request = chain.request()

                    val urlBuilder = request.url().newBuilder()

                    // api version
                    urlBuilder.addQueryParameter("v", apiVersion).build()

                    // add client id
                    urlBuilder.addQueryParameter("client_id", clientId).build()

                    // add client secret
                    urlBuilder.addQueryParameter("client_secret", clientSecret).build()

                    // add current location
                    urlBuilder.addQueryParameter("ll", currentLatLong).build()

                    request = request.newBuilder().url(urlBuilder.build()).build()

                    chain.proceed(request)
                }
                .readTimeout(PlacesApi.READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()

        callAdapterFactory = PlacesRxJavaCallAdapterFactory.create()

        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl) //
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(callAdapterFactory!!)
                .build()

    }

    override fun trendingVenues(): Observable<Result<VenuesResponse>> {
        return retrofit.create(VenuesService::class.java).trendingVenues()
    }

    override fun searchForVenues(searchTerm: String): Observable<Result<VenuesResponse>> {
        return retrofit.create(VenuesService::class.java).search(searchTerm)
    }

    override fun searchForSuggestedVenues(searchTerm: String): Observable<Result<SuggestedVenuesResponse>> {
        return retrofit.create(VenuesService::class.java).suggestCompletion(searchTerm)
    }

    override fun getVenue(venueId: String): Observable<Result<VenueResponse>> {
        return retrofit.create(VenuesService::class.java).venue(venueId)
    }
}
