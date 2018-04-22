package com.grace.placessearch.service.local;

import static org.assertj.core.api.Assertions.assertThat;

import com.grace.placessearch.data.model.VenuesResponse;

import org.junit.Test;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.observables.BlockingObservable;

public class VenuesTest extends PlacesTestBase {

    @Test
    public void testTrendingVenues() {
        Observable<Result<VenuesResponse>> observable = getPlaces().getTrendingVenues();
        BlockingObservable<Result<VenuesResponse>> bo = BlockingObservable.from(observable);
        VenuesResponse venuesResponse = bo.first().response().body();
        assertThat(venuesResponse).isNotNull();
        assertThat(venuesResponse.getResponse().getVenues().size()).isEqualTo(18);
    }
}
