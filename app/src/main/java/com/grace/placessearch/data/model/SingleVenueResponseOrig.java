package com.grace.placessearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class SingleVenueResponseOrig {

    @SerializedName("venue")
    @Expose
    private VenueOrig venue;

    public VenueOrig getVenue() {
        return venue;
    }

    public void setVenue(VenueOrig venue) {
        this.venue = venue;
    }
}