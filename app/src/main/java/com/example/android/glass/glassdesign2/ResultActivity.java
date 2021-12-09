package com.example.android.glass.glassdesign2;

import android.os.Bundle;
import android.widget.TextView;

import com.example.android.glass.glassdesign2.data.DataMCQ;

import java.util.ArrayList;

public class ResultActivity extends BaseActivity {

    private int count=0;
    private TextView textView, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ArrayList<DataMCQ> mcqs = TestActivity.arrayList;
        ArrayList<Integer> ans = TestActivity.responses;
        textView = findViewById(R.id.activity_result_textview);
        tvStatus = findViewById(R.id.activity_result_tvStatus);

        for (int i=0; i<mcqs.size(); i++){
            if (mcqs.get(i).getAnsID() == ans.get(i)){
                count++;
            }
        }

        textView.setText("Marks: " + count);

        if (count <4){
            tvStatus.setText("Take the test Again");
        }else {
            tvStatus.setText("Congrats, You have Passed this test");
        }

    }
}