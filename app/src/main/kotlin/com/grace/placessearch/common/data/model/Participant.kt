package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Participant {

    @SerializedName("participant")
    @Expose
    var participant: Participant? = null
    @SerializedName("role")
    @Expose
    var role: String? = null

}