package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Location {

    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("crossStreet")
    @Expose
    var crossStreet: String? = null
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.toDouble()
    @SerializedName("lng")
    @Expose
    var lng: Double = 0.toDouble()
    @SerializedName("distance")
    @Expose
    var distance: Long = 0
    @SerializedName("postalCode")
    @Expose
    var postalCode: String? = null
    @SerializedName("cc")
    @Expose
    var cc: String? = null
    @SerializedName("neighborhood")
    @Expose
    var neighborhood: String? = null
    @SerializedName("city")
    @Expose
    var city: String? = null
    @SerializedName("state")
    @Expose
    var state: String? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("formattedAddress")
    @Expose
    var formattedAddress: List<String> = ArrayList()
    @SerializedName("labeledLatLngs")
    @Expose
    var labeledLatLngs: List<LabeledLatLng> = ArrayList()

}