package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SingleVenueResponse {

    @SerializedName("venue")
    @Expose
    var venue: Venue? = null
}