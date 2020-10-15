package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Icon {

    @SerializedName("prefix")
    @Expose
    var prefix: String? = null
    @SerializedName("suffix")
    @Expose
    var suffix: String? = null

}