package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Provider {

    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("icon")
    @Expose
    var icon: Icon? = null

}