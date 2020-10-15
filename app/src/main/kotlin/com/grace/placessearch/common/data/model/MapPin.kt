package com.grace.placessearch.common.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by vicsonvictor on 4/23/18.
 */
@Parcelize
data class MapPin(val venueId: String,
                           val pinName: String,
                           val lat: Double,
                           val lng: Double,
                           val imgUrl: String) : Parcelable

