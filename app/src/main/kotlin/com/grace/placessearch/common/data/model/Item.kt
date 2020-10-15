package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Item {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("categories")
    @Expose
    var categories: List<Category> = ArrayList()
    @SerializedName("allDay")
    @Expose
    var isAllDay: Boolean = false
    @SerializedName("date")
    @Expose
    var date: Long = 0
    @SerializedName("timeZone")
    @Expose
    var timeZone: String? = null
    @SerializedName("summary")
    @Expose
    var summary: String? = null
    @SerializedName("text")
    @Expose
    var text: String? = null
    @SerializedName("images")
    @Expose
    var images: List<String> = ArrayList()
    @SerializedName("stats")
    @Expose
    var stats: Stats? = null
    @SerializedName("participants")
    @Expose
    var participants: List<Participant> = ArrayList()
    @SerializedName("genres")
    @Expose
    var genres: String? = null
    @SerializedName("rating")
    @Expose
    var rating: String? = null
    @SerializedName("runningTime")
    @Expose
    var runningTime: String? = null

}