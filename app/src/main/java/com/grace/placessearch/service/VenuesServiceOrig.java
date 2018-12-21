package com.grace.placessearch.service;


import com.grace.placessearch.common.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.common.data.model.VenueResponse;
import com.grace.placessearch.common.data.model.VenuesResponse;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by vicsonvictor on 4/21/18.
 */
@Deprecated
public interface VenuesServiceOrig {

    @GET("venues/trending")
    Observable<Result<VenuesResponse>> trendingVenues();

    @GET("venues/search?intent=checkin&limit=25")
    Observable<Result<VenuesResponse>> search(@Query("query") String queryTerm);

    @GET("venues/suggestcompletion?intent=checkin&limit=50")
    Observable<Result<SuggestedVenuesResponse>> suggestCompletion(@Query("query") String queryTerm);

    @GET("venues/{venue_id}")
    Observable<Result<VenueResponse>> venue(@Path("venue_id") String venueId);

}
