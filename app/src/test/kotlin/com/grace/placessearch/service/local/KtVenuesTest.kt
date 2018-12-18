package com.grace.placessearch.service.local

import org.assertj.core.api.Assertions.assertThat

import com.grace.placessearch.data.model.SuggestedVenuesResponse
import com.grace.placessearch.data.model.Venue
import com.grace.placessearch.data.model.VenueResponse
import com.grace.placessearch.data.model.VenuesResponse

import org.junit.Test

import retrofit2.adapter.rxjava.Result
import rx.Observable
import rx.observables.BlockingObservable

class KtVenuesTest : KtPlacesTestBase() {

    @Test
    fun testTrendingVenues() {
        val observable = places.trendingVenues
        val bo = BlockingObservable.from(observable)
        val venuesResponse = bo.first().response().body()
        assertThat(venuesResponse).isNotNull()
        assertThat(venuesResponse.venueListResponse.venues.size).isEqualTo(18)
    }

    @Test
    fun testSuggestedVenues() {
        val observable = places.searchForSuggestedVenues("cof")
        val bo = BlockingObservable.from(observable)
        val venuesResponse = bo.first().response().body()
        assertThat(venuesResponse).isNotNull()
        assertThat(venuesResponse.response.venues.size).isEqualTo(25)

        // Test first venue
        val suggestedVenues = venuesResponse.response.venues
        val venue = suggestedVenues[0]
        assertThat(venue).isNotNull()

        assertThat(venue.id).isEqualTo("52d456c811d24128cdd7bc8b")
        assertThat(venue.name).isEqualTo("Storyville Coffee Company")
        assertThat(venue.location).isNotNull()
        assertThat(venue.categories.size).isEqualTo(2)
        assertThat(venue.categories[0].id).isEqualTo("4bf58dd8d48988d1e0931735")
        assertThat(venue.categories[0].name).isEqualTo("Coffee Shop")
        assertThat(venue.categories[1].id).isEqualTo("4d4b7105d754a06374d81259")
        assertThat(venue.categories[1].name).isEqualTo("Food")
    }

    @Test
    fun testGetVenue() {
        val observable = places.getVenue("cof")
        val bo = BlockingObservable.from(observable)
        val venuesResponse = bo.first().response().body()
        assertThat(venuesResponse).isNotNull()

        // Test venue
        val venue = venuesResponse.singleVenueResponse.venue
        assertThat(venue).isNotNull()

        assertThat(venue.id).isEqualTo("52d456c811d24128cdd7bc8b")
        assertThat(venue.name).isEqualTo("Storyville Coffee Company")
        assertThat(venue.location).isNotNull()
        assertThat(venue.categories.size).isEqualTo(1)
        assertThat(venue.categories[0].id).isEqualTo("4bf58dd8d48988d1e0931735")
        assertThat(venue.categories[0].name).isEqualTo("Coffee Shop")
    }
}
