package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;
import com.mazatlab.domotic_app.utils.Network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Button connectedDevicesBtn;
    Button connectedUsersBtn;
    Switch cronjobActivationSwitch;
    String switchLabelText;

    Service loginService;
    LoginPayload loginPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        connectedDevicesBtn = findViewById(R.id.connectedDevicesBtn);
        connectedDevicesBtn.setOnClickListener(this);

        connectedUsersBtn = findViewById(R.id.connectedUsersBtn);
        connectedUsersBtn.setOnClickListener(this);

        cronjobActivationSwitch = findViewById(R.id.cronjobActivationSwitch);
        cronjobActivationSwitch.setOnCheckedChangeListener(this);

        Service service = Client.getClient(
                Network.getApiServerUrl(getApplicationContext())).create(Service.class);


        Call<String> getCrontab = service.getCronjob();
        getCrontab.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200) {
                    if(!response.body().isEmpty()) {
                        cronjobActivationSwitch.setChecked(response.body().charAt(0) != '#');
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });

        String switchLabelText = cronjobActivationSwitch.isChecked()?"Cronjob Activado": "Cronjob Desactivado";
        cronjobActivationSwitch.setText(switchLabelText);
    }

    @Override
    public void onClick(View v) {
        Intent connectedDevices = new Intent(getApplicationContext(), ConnectedMacsActivity.class);

        if(v.getId() == R.id.connectedDevicesBtn) {
            Toast.makeText(this, "Escaneando la red", Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putBoolean("registered", false);
            connectedDevices.putExtras(bundle);

            startActivity(connectedDevices);
        }

        if(v.getId() == R.id.connectedUsersBtn) {
            Toast.makeText(this, "Usuarios conectados", Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putBoolean("registered", true);
            connectedDevices.putExtras(bundle);

            startActivity(connectedDevices);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        cronjobActivationSwitch.setEnabled(false);
        cronjobActivationSwitch.setText("Cargando...");

        String apiServerUrl = Network.getApiServerUrl(getApplicationContext());
        String partialMac = Network.getHostAddress(getApplicationContext());
        loginPayload = new LoginPayload(partialMac);

        loginService = Client.getClient(apiServerUrl).create(Service.class);
        Call<LoginResponse> loginCall = loginService.postLogin(loginPayload);

        loginCall.enqueue(new Callback<LoginResponse>() {
            Service jwtService;

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.code() == 200) {
                    String accessToken = response.body().accessToken;

                    jwtService = Client.getClient(apiServerUrl,
                                    accessToken,
                                    Network.RETRY_TIMES,
                                    Network.RETRY_MILLISECONDS_TIME)
                            .create(Service.class);

                    Call<String> cronjobCall;

                    if (isChecked) {
                        cronjobCall = jwtService.getCrontab();
                    } else {
                        cronjobCall = jwtService.quitCron();
                    }

                    cronjobCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            switchLabelText =  buttonView.isChecked()? "Cronjob Activado": "Cronjob desactivado";
                            buttonView.setText(switchLabelText);
                            buttonView.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(SettingsActivity.this,
                                    "El cronjob no pudo ser activado",
                                    Toast.LENGTH_SHORT).show();

                            buttonView.setText("Cronjob Desactivado");
                            buttonView.setChecked(false);
                            buttonView.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                buttonView.setEnabled(true);
            }
        });
    }
}
