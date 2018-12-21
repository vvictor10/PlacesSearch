package com.grace.placessearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class EventsOrig {

    @SerializedName("count")
    @Expose
    private long count;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("items")
    @Expose
    private List<ItemOrig> items = new ArrayList<>();

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ItemOrig> getItems() {
        return items;
    }

    public void setItems(List<ItemOrig> items) {
        this.items = items;
    }

}