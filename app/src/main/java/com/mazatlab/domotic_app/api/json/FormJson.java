package com.mazatlab.domotic_app.api.json;

import com.google.gson.annotations.SerializedName;

public class FormJson {

    @SerializedName("partial_mac")
    private String partialMac;

    @SerializedName("device_name")
    private String deviceName;

    @SerializedName("email")
    private String email;

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("desired_temperature")
    private int desireTemperature;

    @SerializedName("medical_condition")
    private boolean medicalCondition;

    @SerializedName("medical_condition_level")
    private String medicalConditionLevel;

    // used to build payload for calling a service method
    public FormJson() {}

    // used to map the response handle by Retrofit
    public FormJson(String partialMac,
                    String deviceName,
                    String email,
                    String firstname,
                    String lastname,
                    int desireTemperature,
                    boolean medicalCondition,
                    String medicalConditionLevel) {

        this.partialMac = partialMac;
        this.deviceName = deviceName;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.desireTemperature = desireTemperature;
        this.medicalCondition = medicalCondition;
        this.medicalConditionLevel = medicalConditionLevel;
    }

    public void setPartialMac(String partialMac) {
        this.partialMac = partialMac;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setDesireTemperature(int desireTemperature) {
        this.desireTemperature = desireTemperature;
    }

    public void setMedicalCondition(boolean medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public void setMedicalConditionLevel(String medicalConditionLevel) {
        this.medicalConditionLevel = medicalConditionLevel;
    }
}
