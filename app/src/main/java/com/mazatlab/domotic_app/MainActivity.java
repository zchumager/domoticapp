package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;
import com.mazatlab.domotic_app.utils.Network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
MainActivity extends AppCompatActivity {
    Service loginService;
    LoginPayload loginPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiServerUrl = Network.getApiServerUrl(getApplicationContext());
        String partialMac = Network.getHostAddress(getApplicationContext());

        loginPayload = new LoginPayload(partialMac);
        loginService = Client.getClient(apiServerUrl).create(Service.class);

        Call<LoginResponse> loginCall = loginService.postLogin(loginPayload);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 404) {
                    Intent registration = new Intent(getApplicationContext(), PreferencesFormActivity.class);
                    startActivity(registration);
                    finish();
                }

                if(response.code() == 200) {
                    if (response.body().role.equalsIgnoreCase("visitor")) {
                        Intent visitorBoard = new Intent(getApplicationContext(), VisitorBoardActivity.class);
                        startActivity(visitorBoard);
                        finish();
                    }

                    if (response.body().role.equalsIgnoreCase("habitant")) {
                        Intent habitantBoard = new Intent(getApplicationContext(), HabitantBoardActivity.class);
                        habitantBoard.putExtra("accessToken", response.body().accessToken);
                        startActivity(habitantBoard);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Intent failure = new Intent(getApplicationContext(), FailureActivity.class);
                startActivity(failure);
                finish();
            }
        });
    }
}
