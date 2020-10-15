package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Venue {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("canonicalUrl")
    @Expose
    var canonicalUrl: String? = null
    @SerializedName("location")
    @Expose
    var location: Location? = null
    @SerializedName("categories")
    @Expose
    var categories: List<Category> = ArrayList()
    @SerializedName("venuePage")
    @Expose
    var venuePage: VenuePage? = null
    @SerializedName("delivery")
    @Expose
    var delivery: Delivery? = null
    @SerializedName("events")
    @Expose
    var events: Events? = null

    val listImgUrl: String?
        get() = if (categories.isEmpty()) {
            null
        } else categories[0].listImgUrl

    val mapPinUrl: String?
        get() = if (categories.isEmpty()) {
            null
        } else categories[0].mapPinUrl

}