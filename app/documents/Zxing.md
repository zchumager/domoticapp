1 - Add zxing core and embedded dependencies
2 - Create PortraitCaptureActivity that extends from CaptureActivity
3 - Open AndroidManifest.xml and set android:screenOrientation="portrait"
        on .PortraitCaptureActivity <activity>
4 - Create an instance of IntentIntegrator and set PortraitCaptureActivity.class
        with method setCaptureActivity