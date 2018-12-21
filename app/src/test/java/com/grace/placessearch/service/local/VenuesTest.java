package com.grace.placessearch.service.local;

import static org.assertj.core.api.Assertions.assertThat;

import com.grace.placessearch.common.data.model.SuggestedVenuesResponse;
import com.grace.placessearch.common.data.model.Venue;
import com.grace.placessearch.common.data.model.VenueResponse;
import com.grace.placessearch.common.data.model.VenuesResponse;

import org.junit.Test;

import java.util.List;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.observables.BlockingObservable;

public class VenuesTest extends PlacesTestBase {

    @Test
    public void testTrendingVenues() {
        Observable<Result<VenuesResponse>> observable = getPlaces().trendingVenues();
        BlockingObservable<Result<VenuesResponse>> bo = BlockingObservable.from(observable);
        VenuesResponse venuesResponse = bo.first().response().body();
        assertThat(venuesResponse).isNotNull();
        assertThat(venuesResponse.getVenueListResponse().getVenues().size()).isEqualTo(18);
    }

    @Test
    public void testSuggestedVenues() {
        Observable<Result<SuggestedVenuesResponse>> observable = getPlaces().searchForSuggestedVenues("cof");
        BlockingObservable<Result<SuggestedVenuesResponse>> bo = BlockingObservable.from(observable);
        SuggestedVenuesResponse venuesResponse = bo.first().response().body();
        assertThat(venuesResponse).isNotNull();
        assertThat(venuesResponse.getResponse().getVenues().size()).isEqualTo(25);

        // Test first venue
        List<Venue> suggestedVenues = venuesResponse.getResponse().getVenues();
        Venue venue = suggestedVenues.get(0);
        assertThat(venue).isNotNull();

        assertThat(venue.getId()).isEqualTo("52d456c811d24128cdd7bc8b");
        assertThat(venue.getName()).isEqualTo("Storyville Coffee Company");
        assertThat(venue.getLocation()).isNotNull();
        assertThat(venue.getCategories().size()).isEqualTo(2);
        assertThat(venue.getCategories().get(0).getId()).isEqualTo("4bf58dd8d48988d1e0931735");
        assertThat(venue.getCategories().get(0).getName()).isEqualTo("Coffee Shop");
        assertThat(venue.getCategories().get(1).getId()).isEqualTo("4d4b7105d754a06374d81259");
        assertThat(venue.getCategories().get(1).getName()).isEqualTo("Food");
    }

    @Test
    public void testGetVenue() {
        Observable<Result<VenueResponse>> observable = getPlaces().getVenue("cof");
        BlockingObservable<Result<VenueResponse>> bo = BlockingObservable.from(observable);
        VenueResponse venuesResponse = bo.first().response().body();
        assertThat(venuesResponse).isNotNull();

        // Test venue
        Venue venue = venuesResponse.getSingleVenueResponse().getVenue();
        assertThat(venue).isNotNull();

        assertThat(venue.getId()).isEqualTo("52d456c811d24128cdd7bc8b");
        assertThat(venue.getName()).isEqualTo("Storyville Coffee Company");
        assertThat(venue.getLocation()).isNotNull();
        assertThat(venue.getCategories().size()).isEqualTo(1);
        assertThat(venue.getCategories().get(0).getId()).isEqualTo("4bf58dd8d48988d1e0931735");
        assertThat(venue.getCategories().get(0).getName()).isEqualTo("Coffee Shop");
    }
}
