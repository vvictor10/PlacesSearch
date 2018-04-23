package com.grace.placessearch.service;

import com.grace.placessearch.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.data.model.VenuesResponse;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by vicsonvictor on 4/21/18.
 */

public interface VenuesService {

    @GET("venues/trending")
    Observable<Result<VenuesResponse>> trendingVenues();

    @GET("venues/search?intent=checkin&limit=25")
    Observable<Result<VenuesResponse>> search(@Query("query") String queryTerm);

    @GET("venues/suggestcompletion?intent=checkin&limit=50")
    Observable<Result<SuggestedVenuesResponse>> suggestCompletion(@Query("query") String queryTerm);

}
