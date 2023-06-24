package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FailureActivity extends AppCompatActivity implements View.OnClickListener {

    TextView ipAddr;
    Button domoticServerConnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure);

        this.initComponent();
    }

    protected void initComponent() {
        this.ipAddr = findViewById(R.id.ipaddrTxt);
        this.domoticServerConnBtn = findViewById(R.id.domoticServerConnBtn);
        this.domoticServerConnBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.domoticServerConnBtn) {
            String ipAddress = this.ipAddr.getText().toString();

            // Verifying IP Address provided is valid
            if(Patterns.IP_ADDRESS.matcher(ipAddress).matches()) {

            } else {
                Toast.makeText(this, "The value provided is not a valid IP Address", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
