package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.DeviceInfoResponse;
import com.mazatlab.domotic_app.api.json.FormJson;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;
import com.mazatlab.domotic_app.api.json.login.LoginResponse;
import com.mazatlab.domotic_app.utils.Network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreferencesFormActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

    // Widgets
    TextView emailTxt, firstnameTxt, lastnameTxt;
    NumberPicker temperatureNumber;
    Switch medicalConditionSwitch;
    String[] medicalConditionLevels;
    Spinner medicalConditionLevel;
    Button saveRequestBtn;

    String partialMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_form);

        initComponents();
        main();
    }

    protected void initComponents() {

        medicalConditionLevels = new String[]{"ESTABLE", "SERIO", "GRAVE"};

        emailTxt = findViewById(R.id.emailTxt);
        firstnameTxt = findViewById(R.id.firstnameTxt);
        lastnameTxt = findViewById(R.id.lastnameTxt);

        temperatureNumber = findViewById(R.id.temperatureNumber);
        temperatureNumber.setMinValue(16);
        temperatureNumber.setMaxValue(31);

        medicalConditionSwitch = findViewById(R.id.medicalConditionSwitch);
        medicalConditionSwitch.setOnCheckedChangeListener(this);

        // medicalConditionLevel spinner
        medicalConditionLevel = findViewById(R.id.medicalConditionLevel);
        medicalConditionLevel.setEnabled(medicalConditionSwitch.isChecked());
        medicalConditionLevel.setAdapter(getArrayAdapter());
        medicalConditionLevel.setOnItemSelectedListener(this);

        saveRequestBtn = findViewById(R.id.saveRequestBtn);
        saveRequestBtn.setOnClickListener(this);
    }

    protected void main() {
        /**
         * 1 - Verify extras is not null
         * 2 - Verify partial mac has been passed as an extra
         * 3 - Create a retrofit client
         * 4 - Make an HTTP request to /device_info endpoint
         * 5 - Use the response to load the views with the body data
         * */

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            partialMac = extras.getString("partialMac");

            if (partialMac != null) {

                Service service = Client.getClient().create(Service.class);
                Call<DeviceInfoResponse> deviceInfoCall = service.getDeviceInfo(partialMac);

                // Getting Device's Information
                deviceInfoCall.enqueue(new Callback<DeviceInfoResponse>() {
                    @Override
                    public void onResponse(Call<DeviceInfoResponse> call,
                                           Response<DeviceInfoResponse> response) {

                        if (response.code() == 200) {

                            // loading data from response body
                            PreferencesFormActivity.this.emailTxt.setText(response.body().email);
                            PreferencesFormActivity.this.firstnameTxt.setText(response.body().firstname);
                            PreferencesFormActivity.this.lastnameTxt.setText(response.body().lastname);
                            PreferencesFormActivity.this.temperatureNumber.setValue(response.body().desireTemperature);
                            PreferencesFormActivity.this.medicalConditionSwitch.setChecked(response.body().medicalCondition);

                            if (response.body().medicalCondition) {
                                PreferencesFormActivity.this.medicalConditionLevel.setSelection(
                                        getArrayAdapter().getPosition(
                                                response.body().medicalConditionLevel.toUpperCase()));
                            }

                            Toast.makeText(PreferencesFormActivity.this, "Preferencias Cargadas", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeviceInfoResponse> call, Throwable t) {
                        Toast.makeText(PreferencesFormActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //********** Switch Event **********************************************************************

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        medicalConditionLevel.setEnabled(medicalConditionSwitch.isChecked());
    }

    //********** Spinner Events ********************************************************************

    protected ArrayAdapter<String> getArrayAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, medicalConditionLevels);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    //********** Button Event **********************************************************************

    @Override
    public void onClick(View v) {
        FormJson formJsonPayload = new FormJson();

        formJsonPayload.setPartialMac(Network.getHostAddress(getApplicationContext()));
        formJsonPayload.setDeviceName(Network.getDeviceName(getApplicationContext()));
        formJsonPayload.setEmail(emailTxt.getText().toString());
        formJsonPayload.setFirstname(firstnameTxt.getText().toString());
        formJsonPayload.setLastname(lastnameTxt.getText().toString());
        formJsonPayload.setDesireTemperature(temperatureNumber.getValue());
        formJsonPayload.setMedicalCondition(medicalConditionSwitch.isChecked());

        if (medicalConditionSwitch.isChecked()) {
            formJsonPayload.setMedicalConditionLevel(medicalConditionLevel.getSelectedItem().toString());
        }

        if (partialMac != null) {
            PreferencesFormActivity.this.saveRequestBtn.setEnabled(false);
            PreferencesFormActivity.this.saveRequestBtn.setText("ACTUALIZANDO PREFERENCIAS");
            updatePreferencesRequest(formJsonPayload);
        } else {
            join2HomeRequest(formJsonPayload);
        }
    }

    //********** REQUESTS **************************************************************************

    protected void join2HomeRequest(FormJson payload) {

        Service service = Client.getClient().create(Service.class);
        Call<FormJson> join2HomeCall = service.postJoin2Home(payload);
        join2HomeCall.enqueue(new Callback<FormJson>() {
            @Override
            public void onResponse(Call<FormJson> call, Response<FormJson> response) {
                if (response.code() == 200) {

                    LoginPayload loginPayload = new LoginPayload(
                            Network.getHostAddress(getApplicationContext()));

                    Call<LoginResponse> loginCall = service.postLogin(loginPayload);
                    loginCall.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.code() == 200) {

                                if (response.body().role.equalsIgnoreCase("visitor")) {
                                    Intent visitorBoard = new Intent(getApplicationContext(), VisitorBoardActivity.class);
                                    startActivity(visitorBoard);
                                    finish();
                                }

                                if (response.body().role.equalsIgnoreCase("habitant")) {
                                    String accessToken = response.body().accessToken;

                                    Intent habitantBoard = new Intent(getApplicationContext(), HabitantBoardActivity.class);
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

            @Override
            public void onFailure(Call<FormJson> call, Throwable t) {
                Toast.makeText(PreferencesFormActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    protected void updatePreferencesRequest(FormJson payload) {

        LoginPayload loginPayload = new LoginPayload(partialMac);

        Service service = Client.getClient().create(Service.class);
        Call<LoginResponse> loginResponseCall = service.postLogin(loginPayload);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.code() == 200) {

                    String accessToken = response.body().accessToken;

                    Service jwtService = Client.getClient(
                                    accessToken, Network.RETRY_TIMES, Network.RETRY_MILLISECONDS_TIME)
                            .create(Service.class);

                    Call<FormJson> updateCall = jwtService.putUpdatePreferences(payload);
                    updateCall.enqueue(new Callback<FormJson>() {
                        @Override
                        public void onResponse(Call<FormJson> call, Response<FormJson> response) {

                            if (response.code() == 201) {
                                Toast.makeText(
                                        PreferencesFormActivity.this,
                                        "Preferencias actualizadas", Toast.LENGTH_SHORT).show();

                                PreferencesFormActivity.this.saveRequestBtn.setText("GUARDAR PREFERENCIAS");
                                PreferencesFormActivity.this.saveRequestBtn.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<FormJson> call, Throwable t) {
                            Toast.makeText(PreferencesFormActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                            PreferencesFormActivity.this.saveRequestBtn.setText("GUARDAR PREFERENCIAS");
                            PreferencesFormActivity.this.saveRequestBtn.setEnabled(true);
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(PreferencesFormActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
