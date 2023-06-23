package com.mazatlab.domotic_app.api.json.login;

import com.google.gson.annotations.SerializedName;

public class LoginPayload {
    @SerializedName("partial_mac")
    private String partialMac;

    public LoginPayload(String partialMac) {
        this.partialMac = partialMac;
    }
}
