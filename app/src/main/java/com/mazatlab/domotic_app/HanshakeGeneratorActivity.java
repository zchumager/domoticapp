package com.mazatlab.domotic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mazatlab.domotic_app.utils.Network;

import java.util.Hashtable;

public class HanshakeGeneratorActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView qrCodeView;
    private int qrCodeWidth, qrCodeHeight;
    private Button closeQrCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hanshake_generator);

        closeQrCodeBtn = findViewById(R.id.closeQrCodeBtn);
        closeQrCodeBtn.setOnClickListener(this);

        qrCodeView = findViewById(R.id.qrCodeView);
        qrCodeWidth = getResources().getDisplayMetrics().widthPixels / 2;
        qrCodeHeight = getResources().getDisplayMetrics().widthPixels /2;

        try {
            String partialMac = Network.getHostAddress(getApplicationContext());
            generateQrCode(partialMac);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    protected void generateQrCode(String partialMac) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeEncoder = new QRCodeWriter();
        BitMatrix bitmapMatrix = qrCodeEncoder.encode(
                partialMac, BarcodeFormat.QR_CODE, this.qrCodeWidth, this.qrCodeHeight, hintMap);

        Bitmap bmp = Bitmap.createBitmap(
                bitmapMatrix.getWidth(), bitmapMatrix.getHeight(), Bitmap.Config.ARGB_8888);

        // building the QR code to be uploaded into the imageView
        for(int x=0; x < bitmapMatrix.getWidth(); x++) {
            for(int y=0; y < bitmapMatrix.getHeight(); y++) {
                bmp.setPixel(x, y, bitmapMatrix.get(x, y) ? Color.BLACK: Color.WHITE);
            }
        }

        qrCodeView.setImageBitmap(bmp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeQrCodeBtn) {
            finish();
        }
    }
}
