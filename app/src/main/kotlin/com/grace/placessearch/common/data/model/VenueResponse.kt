package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VenueResponse {

    @SerializedName("meta")
    @Expose
    var meta: Meta? = null
    @SerializedName("response")
    @Expose
    var singleVenueResponse: SingleVenueResponse? = null
}