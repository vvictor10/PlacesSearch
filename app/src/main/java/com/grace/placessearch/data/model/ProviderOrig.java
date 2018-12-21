package com.grace.placessearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class ProviderOrig {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private IconOrig icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IconOrig getIcon() {
        return icon;
    }

    public void setIcon(IconOrig icon) {
        this.icon = icon;
    }

}