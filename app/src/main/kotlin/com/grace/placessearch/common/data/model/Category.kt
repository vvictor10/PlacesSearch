package com.grace.placessearch.common.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Category {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("pluralName")
    @Expose
    var pluralName: String? = null
    @SerializedName("shortName")
    @Expose
    var shortName: String? = null
    @SerializedName("icon")
    @Expose
    var icon: Icon? = null
    @SerializedName("primary")
    @Expose
    var isPrimary: Boolean = false

    val listImgUrl: String?
        get() = if (icon == null || icon!!.prefix == null || icon!!.suffix == null) {
            null
        } else icon!!.prefix + "bg_88" + icon!!.suffix

    val mapPinUrl: String?
        get() = if (icon == null || icon!!.prefix == null || icon!!.suffix == null) {
            null
        } else icon!!.prefix + "32" + icon!!.suffix
}