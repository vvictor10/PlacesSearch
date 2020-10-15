package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class VenueListResponse {

    @SerializedName("venues")
    @Expose
    var venues: List<Venue> = ArrayList()

}