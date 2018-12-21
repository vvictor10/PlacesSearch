package com.grace.placessearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class VenueListResponseOrig {

    @SerializedName("venues")
    @Expose
    private List<VenueOrig> venues = new ArrayList<>();

    public List<VenueOrig> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueOrig> venues) {
        this.venues = venues;
    }

}