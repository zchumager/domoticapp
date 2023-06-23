package com.mazatlab.domotic_app.api.json;

import com.google.gson.annotations.SerializedName;

public class AboutResponse {
    @SerializedName("application")
    public String application;

    @SerializedName("author")
    public String author;

    @SerializedName("version")
    public int version;
}
