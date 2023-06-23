package com.mazatlab.domotic_app.api.json.login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("role")
    public String role;
}
