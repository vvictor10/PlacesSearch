package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Stats {

    @SerializedName("checkinsCount")
    @Expose
    var checkinsCount: Long = 0
    @SerializedName("usersCount")
    @Expose
    var usersCount: Long = 0

}