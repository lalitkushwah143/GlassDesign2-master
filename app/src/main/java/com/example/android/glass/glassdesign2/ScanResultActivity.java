package com.example.android.glass.glassdesign2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.glass.ui.GlassGestureDetector;

public class ScanResultActivity extends BaseActivity {

    private TextView textView;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        textView = findViewById(R.id.activity_result_textview);

        result = getIntent().getStringExtra("key");

        if(result != null){
            textView.setText(result);
        }else {
            textView.setText(R.string.error_string);
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (result.equals("defrost")){
                    Intent intent= new Intent(ScanResultActivity.this, ManualActivity.class);
                    intent.putExtra("manual_type", result);
                    startActivity(intent);
                } else if (result.equals("batch")) {
                    Intent intent= new Intent(ScanResultActivity.this, ManualActivity.class);
                    intent.putExtra("manual_type", result);
                    startActivity(intent);
                }
                break;
        }

        return super.onGesture(gesture);
    }
}