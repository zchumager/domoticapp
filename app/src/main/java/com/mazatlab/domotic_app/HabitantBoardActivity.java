package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.mazatlab.domotic_app.utils.Network;

public class HabitantBoardActivity extends AppCompatActivity
        implements View.OnClickListener {

    Button preferencesBtn, addHabitantBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitant_board);

        preferencesBtn = findViewById(R.id.PreferencesBtn);
        preferencesBtn.setOnClickListener(this);

        addHabitantBtn = findViewById(R.id.addHabitantBtn);
        addHabitantBtn.setOnClickListener(this);

        settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.PreferencesBtn) {
            Intent updatePreferences = new Intent(getApplicationContext(), PreferencesFormActivity.class);
            updatePreferences.putExtra("partialMac", Network.getHostAddress(getApplicationContext()));
            startActivity(updatePreferences);
        }

        if (v.getId() == R.id.addHabitantBtn) {
            Intent handShakeGenerator = new Intent(getApplicationContext(), HanshakeGeneratorActivity.class);
            startActivity(handShakeGenerator);
        }

        if (v.getId() == R.id.settingsBtn) {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
        }
    }
}
