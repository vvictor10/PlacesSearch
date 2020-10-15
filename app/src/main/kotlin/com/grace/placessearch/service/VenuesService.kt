package com.grace.placessearch.service

import com.grace.placessearch.common.data.model.SuggestedVenuesResponse
import com.grace.placessearch.common.data.model.VenueResponse
import com.grace.placessearch.common.data.model.VenuesResponse
import retrofit2.adapter.rxjava.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Created by vicsonvictor on 4/21/18.
 */

interface VenuesService {

    @GET("venues/trending")
    fun trendingVenues(): Observable<Result<VenuesResponse>>

    @GET("venues/search?intent=checkin&limit=25")
    fun search(@Query("query") queryTerm: String): Observable<Result<VenuesResponse>>

    @GET("venues/suggestcompletion?intent=checkin&limit=50")
    fun suggestCompletion(@Query("query") queryTerm: String): Observable<Result<SuggestedVenuesResponse>>

    @GET("venues/{venue_id}")
    fun venue(@Path("venue_id") venueId: String): Observable<Result<VenueResponse>>

}
