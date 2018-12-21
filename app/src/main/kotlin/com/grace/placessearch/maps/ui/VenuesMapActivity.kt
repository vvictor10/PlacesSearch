/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grace.placessearch.maps.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.grace.placessearch.R
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.data.model.MapPin
import com.grace.placessearch.venue.detail.ui.VenueDetailsActivity
import com.squareup.picasso.Picasso

import javax.inject.Inject

import timber.log.Timber

/**
 * A Map activity displaying the pins passed in the Intent Extra.
 */
class VenuesMapActivity : AppCompatActivity(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    @Inject
    lateinit var picasso: Picasso

    lateinit var mapPins: List<MapPin>
    lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venues_map)

        mapPins = intent.getParcelableArrayListExtra(PlacesSearchConstants.MAP_PINS_EXTRA)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        this.googleMap = googleMap

        val latLngBounds = addMarkersToMap()

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        googleMap.setInfoWindowAdapter(InfoWindowAdapter())

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        googleMap.setContentDescription("Search Results")

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50))
    }

    /**
     * Adds marker pins to the map.
     * @return
     */
    private fun addMarkersToMap(): LatLngBounds {
        val boundsBuilder = LatLngBounds.Builder()
        for (i in mapPins.indices) {
            val mapPin = mapPins[i]
            val latLng = LatLng(mapPin.lat, mapPin.lng)
            googleMap.addMarker(MarkerOptions()
                    .position(latLng)
                    .title(mapPin.pinName)
                    .snippet(mapPin.venueId)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .infoWindowAnchor(0.5f, 0.5f))
            boundsBuilder.include(latLng)
            Timber.d("Added pin for venue %s", mapPin.pinName)
        }

        googleMap.setOnInfoWindowClickListener { marker -> onMarkerInfoWindowClicked(marker) }

        return boundsBuilder.build()
    }

    private fun onMarkerInfoWindowClicked(marker: Marker) {
        val intent = Intent(this, VenueDetailsActivity::class.java)
        intent.putExtra(PlacesSearchConstants.VENUE_ID_EXTRA, marker.snippet)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    private inner class InfoWindowAdapter internal constructor() : GoogleMap.InfoWindowAdapter {

        private val mWindow: View = layoutInflater.inflate(R.layout.map_marker_info_window, null)
        private val mContents: View = layoutInflater.inflate(R.layout.map_marker_info_contents, null)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, mWindow)
            return mWindow
        }

        override fun getInfoContents(marker: Marker): View {
            render(marker, mContents)
            return mContents
        }

        private fun render(marker: Marker, view: View) {
            val title = marker.title
            val titleUi = view.findViewById<TextView>(R.id.title)
            titleUi.text = title
        }
    }

}