package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;
import com.mazatlab.domotic_app.utils.Network;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectedDevicesActivity extends AppCompatActivity {

    ListView connectedDevicesList;
    ArrayList<String> devices;

    Service loginService;
    LoginPayload loginPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_devices);

        devices = new ArrayList<>(
                Arrays.asList("Cargando ...")
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                devices
        );

        connectedDevicesList = findViewById(R.id.connectedDevicesList);
        connectedDevicesList.setAdapter(adapter);

        String partialMac = Network.getHostAddress(getApplicationContext());
        loginPayload = new LoginPayload(partialMac);

        loginService = Client.getClient().create(Service.class);
        Call<LoginResponse> loginCall = loginService.postLogin(loginPayload);

        loginCall.enqueue(new Callback<LoginResponse>() {
            Service jwtService;

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.code() == 200) {

                    String accessToken = response.body().accessToken;

                    jwtService = Client.getClient(
                            accessToken, Network.RETRY_TIMES, Network.RETRY_TIMES).create(Service.class);

                    Call<ArrayList<String>> connectedDevicesCall = jwtService.getConnectedDevices();
                    connectedDevicesCall.enqueue(new Callback<ArrayList<String>>() {
                        @Override
                        public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                            if(response.code() == 200) {
                                Toast.makeText(ConnectedDevicesActivity.this, "Escaneo Completo", Toast.LENGTH_SHORT).show();

                                // reseting array and adapter
                                devices.clear();
                                adapter.clear();

                                // updating array and adapter
                                devices.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                            // reseting array and adapter
                            devices.clear();
                            adapter.clear();

                            // updating array and adapter
                            devices.addAll(Arrays.asList(t.getMessage()));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(ConnectedDevicesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}