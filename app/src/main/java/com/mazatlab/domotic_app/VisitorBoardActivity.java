package com.mazatlab.domotic_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.Join2FamilyPayload;
import com.mazatlab.domotic_app.api.json.MsgResponse;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;
import com.mazatlab.domotic_app.utils.Network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorBoardActivity extends AppCompatActivity
        implements View.OnClickListener {

    Button visitorPreferences, join2FamilyBtn;

    protected void scanQrCode() {
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("Enfoque el Codigo QR");
        qrScan.setBeepEnabled(true);
        qrScan.setCaptureActivity(PortraitCaptureActivity.class);
        qrScan.setOrientationLocked(true);
        qrScan.initiateScan();
    }

    protected void doHandshake(String habitantPartialMac) {
        String partialMac = Network.getHostAddress(getApplicationContext());
        LoginPayload loginPayload = new LoginPayload(habitantPartialMac);

        String apiServerUrl = Network.getApiServerUrl(getApplicationContext());
        Service loginService = Client.getClient(apiServerUrl).create(Service.class);
        Call<LoginResponse> loginCall = loginService.postLogin(loginPayload);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Service jwtService;

                if (response.code() == 200) {
                    String apiServerUrl = Network.getApiServerUrl(getApplicationContext());
                    String accessToken = response.body().accessToken;

                    jwtService = Client.getClient(apiServerUrl,
                            accessToken,
                            Network.RETRY_TIMES,
                            Network.RETRY_MILLISECONDS_TIME).create(Service.class);

                    Join2FamilyPayload join2FamilyPayload = new Join2FamilyPayload(habitantPartialMac, partialMac);
                    Call<MsgResponse> join2FamilyCall = jwtService.postJoin2Family(join2FamilyPayload);
                    join2FamilyCall.enqueue(new Callback<MsgResponse>() {
                        @Override
                        public void onResponse(Call<MsgResponse> call, Response<MsgResponse> response) {
                            Toast.makeText(VisitorBoardActivity.this, "", Toast.LENGTH_SHORT).show();
                            if(response.code() == 200) {
                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(main);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<MsgResponse> call, Throwable t) {
                            Toast.makeText(VisitorBoardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(VisitorBoardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String partialMac = result.getContents();
            Toast.makeText(this, "Actualizando Perfil", Toast.LENGTH_LONG).show();
            doHandshake(partialMac);
        } else {
            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_board);

        visitorPreferences = findViewById(R.id.visitorPreferencesBtn);
        visitorPreferences.setOnClickListener(this);

        join2FamilyBtn = findViewById(R.id.join2FamilyBtn);
        join2FamilyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.visitorPreferencesBtn) {
            Intent preferencesForm = new Intent(getApplicationContext(), PreferencesFormActivity.class);
            preferencesForm.putExtra("partialMac", Network.getHostAddress(getApplicationContext()));
            startActivity(preferencesForm);
        }

        if (v.getId() == R.id.join2FamilyBtn) {
            scanQrCode();
        }
    }
}
