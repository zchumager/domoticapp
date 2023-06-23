package com.mazatlab.domotic_app;

import com.journeyapps.barcodescanner.CaptureActivity;

public class PortraitCaptureActivity extends CaptureActivity {
    /**
     * This class is being used just as a reference to set
     * screen orientation as portrait on AndroidManifest.xml
     * and use this reference on the instance of IntentIntegrator
     * */

    /**

        AndroidManifest.xml settings
     <activity
     android:name=".PortraitCaptureActivity"
     android:screenOrientation="portrait"
     android:exported="false" />

     * */

    /**

        IntentIntegrator instance settings
     IntentIntegrator qrScan = new IntentIntegrator(this);
     qrScan.setPrompt("Enfoque el Codigo QR");
     qrScan.setBeepEnabled(true);
     qrScan.setCaptureActivity(PortraitCaptureActivity.class);
     qrScan.setOrientationLocked(true);
     qrScan.initiateScan();

     * */
}