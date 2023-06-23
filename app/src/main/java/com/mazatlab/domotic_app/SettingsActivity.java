package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;
import com.mazatlab.domotic_app.utils.Network;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button connectedDevicesBtn;
    Button connectedUsersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        connectedDevicesBtn = findViewById(R.id.connectedDevicesBtn);
        connectedDevicesBtn.setOnClickListener(this);

        connectedUsersBtn = findViewById(R.id.connectedUsersBtn);
        connectedUsersBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.connectedDevicesBtn) {
            Toast.makeText(this, "Escaneando la red", Toast.LENGTH_SHORT).show();

            Intent connectedDevices = new Intent(getApplicationContext(), ConnectedDevicesActivity.class);
            startActivity(connectedDevices);
        }

        if(v.getId() == R.id.connectedUsersBtn) {
            Toast.makeText(this, "Connected Users", Toast.LENGTH_SHORT).show();
        }
    }
}
