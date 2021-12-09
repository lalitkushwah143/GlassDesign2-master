package com.example.android.glass.glassdesign2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.util.DropDownAlert;
import com.google.zxing.Result;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private DropDownAlert dropDownAlert;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setContentView(mScannerView);
        dropDownAlert = new DropDownAlert(this);
        dropDownAlert.setText("Scan Manual QR Code");
        dropDownAlert.setTextWeight(0.5f);
        //  dropDownAlert.addImages("surfer.png", "bike.png");
        dropDownAlert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Prints scan results

        Log.e("result", rawResult.getText());
        // Prints the scan format (qrcode, pdf417 etc.)
        Log.e("result", rawResult.getBarcodeFormat().toString());
        //If you would like to resume scanning, call this method below:
      //  mScannerView.resumeCameraPreview(this);
        String result = rawResult.getText();
        if (result.equals("defrost")){
            Intent intent= new Intent(ScannerActivity.this, ManualActivity.class);
            intent.putExtra("manual_type", result);
            Toast.makeText(this, "Opening Defrost Manual", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else if (result.equals("batch")) {
            Intent intent= new Intent(ScannerActivity.this, ManualActivity.class);
            intent.putExtra("manual_type", result);
            startActivity(intent);
            Toast.makeText(this, "Opening Batch Manual", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "No Manual Found", Toast.LENGTH_SHORT).show();
        }
    /*    Intent intent = new Intent(ScannerActivity.this, ScanResultActivity.class);
        intent.putExtra("key", rawResult.getText());
        setResult(RESULT_OK, intent);
        startActivity(intent);
        
     */
        finish();
    }
}