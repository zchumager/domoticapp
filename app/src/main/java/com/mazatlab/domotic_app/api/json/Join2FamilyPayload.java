package com.mazatlab.domotic_app.api.json;

import com.google.gson.annotations.SerializedName;

public class Join2FamilyPayload {
    @SerializedName("habitant_partial_mac")
    private String habitantPartialMac;

    @SerializedName("visitor_partial_mac")
    private String visitorPartialMac;

    public Join2FamilyPayload(String habitantPartialMac, String visitorPartialMac) {
        this.habitantPartialMac = habitantPartialMac;
        this.visitorPartialMac = visitorPartialMac;
    }
}
