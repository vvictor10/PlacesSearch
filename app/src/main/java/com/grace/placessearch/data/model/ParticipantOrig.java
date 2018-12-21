package com.grace.placessearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class ParticipantOrig {

    @SerializedName("participant")
    @Expose
    private ParticipantOrig participant;
    @SerializedName("role")
    @Expose
    private String role;

    public ParticipantOrig getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantOrig participant) {
        this.participant = participant;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}