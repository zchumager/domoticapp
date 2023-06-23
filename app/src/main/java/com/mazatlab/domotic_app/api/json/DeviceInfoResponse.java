package com.mazatlab.domotic_app.api.json;

import com.google.gson.annotations.SerializedName;

public class DeviceInfoResponse {

    @SerializedName("email")
    public String email;

    @SerializedName("firstname")
    public String firstname;

    @SerializedName("lastname")
    public String lastname;

    @SerializedName("desired_temperature")
    public int desireTemperature;

    @SerializedName("medical_condition")
    public boolean medicalCondition;

    @SerializedName("medical_condition_level")
    public String medicalConditionLevel;

}
