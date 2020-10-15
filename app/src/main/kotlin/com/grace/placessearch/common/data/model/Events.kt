package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Events {

    @SerializedName("count")
    @Expose
    var count: Long = 0
    @SerializedName("summary")
    @Expose
    var summary: String? = null
    @SerializedName("items")
    @Expose
    var items: List<Item> = ArrayList()

}