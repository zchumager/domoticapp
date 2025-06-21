package com.mazatlab.domotic_app.api;

import com.mazatlab.domotic_app.api.json.AboutResponse;
import com.mazatlab.domotic_app.api.json.DeviceInfoResponse;
import com.mazatlab.domotic_app.api.json.FormJson;
import com.mazatlab.domotic_app.api.json.Join2FamilyPayload;
import com.mazatlab.domotic_app.api.json.MsgResponse;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface Service {
    @GET("/about")
    Call<AboutResponse> getAbout();

    @POST("/join2home")
    Call<FormJson> postJoin2Home(@Body FormJson body);

    @POST("/login")
    Call<LoginResponse> postLogin(@Body LoginPayload body);

    @POST("/join2family")
    Call<MsgResponse> postJoin2Family(@Body Join2FamilyPayload body);

    @GET("/device_info")
    Call<DeviceInfoResponse> getDeviceInfo(@Query("partial_mac") String partialMac);

    @PUT("/update_preferences")
    Call<FormJson> putUpdatePreferences(@Body FormJson body);

    @PUT("/update_expiration")
    Call<String> putUpdateExpiration(@Body LoginPayload body);

    @GET("/all_connected_macs")
    Call<ArrayList<String>> getAllConnectedMacs();

    @GET("/registered_connected_macs")
    Call<ArrayList<String>> getRegisteredConnectedMacs();

    @GET("/get_cronjob")
    Call<String> getCronjob();

    @GET("/crontab")
    Call<String> getCrontab();

    @GET("/quitcron")
    Call<String> quitCron();
}
