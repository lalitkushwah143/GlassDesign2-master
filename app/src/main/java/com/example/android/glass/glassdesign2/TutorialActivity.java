package com.example.android.glass.glassdesign2;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glass.ui.GlassGestureDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class TutorialActivity extends Base2Activity {

    private ArrayList<String> arrayList= new ArrayList();
    private TextView textView;
    private int count =0;
    private ConstraintLayout layout;
    String currentPhotoPath;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        prepareArraylist();

        textView = findViewById(R.id.activity_tutorial_textview);
        imageView = findViewById(R.id.activity_tutorial_imageview);
        textView.setText(arrayList.get(count));
        imageView.setImageResource(R.drawable.ic_baseline_touch_app_24);
        layout = findViewById(R.id.activity_tutorial_layout2);
       /*
        switch (SplashActivity.color_code){

            case 1:
                layout.setBackgroundColor(getResources().getColor(R.color.design_green));
                textView.setTextColor(getResources().getColor(R.color.design_green));
                break;

            case 2:
                layout.setBackgroundColor(getResources().getColor(R.color.design_red));
                textView.setTextColor(getResources().getColor(R.color.design_red));

                break;

            case 3:
                layout.setBackgroundColor(getResources().getColor(R.color.design_yellow));
                textView.setTextColor(getResources().getColor(R.color.design_yellow));

                break;
            case 4:
                layout.setBackgroundColor(getResources().getColor(R.color.design_blue));
                textView.setTextColor(getResources().getColor(R.color.design_blue));

                break;
            case 5:
                layout.setBackgroundColor(getResources().getColor(R.color.design_orange));
                textView.setTextColor(getResources().getColor(R.color.design_orange));

                break;
            case 6:
                layout.setBackgroundColor(getResources().getColor(R.color.design_purple));
                textView.setTextColor(getResources().getColor(R.color.design_purple));

                break;

        }

        */

    }

    private void prepareArraylist() {
        arrayList.add(arrayList.size(), getString(R.string.tap));
        arrayList.add(arrayList.size(), getString(R.string.swipe_f));
        arrayList.add(arrayList.size(), getString(R.string.swipe_b));
        arrayList.add(arrayList.size(), getString(R.string.swipe_d));
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (count == 0) {
                    count++;
                    Toast.makeText(this.getApplicationContext(), "Step " + count + "Completed", Toast.LENGTH_SHORT).show();
                  //  takeScreenshot();
                    textView.setText(arrayList.get(count));
                    imageView.setImageResource(R.drawable.baseline_swipe_24);
                }
                return true;

            case SWIPE_FORWARD:
                if (count == 1){
                    count++;
                    Toast.makeText(this.getApplicationContext(), "Step " + count + " Completed", Toast.LENGTH_SHORT).show();
                    textView.setText(arrayList.get(count));
                    imageView.setImageResource(R.drawable.baseline_swipe_24);
                }
                return true;

            case SWIPE_BACKWARD:
                if (count == 2){
                    count++;
                    Toast.makeText(this.getApplicationContext(), "Step " + count + " Completed", Toast.LENGTH_SHORT).show();
                    textView.setText(arrayList.get(count));
                    imageView.setImageResource(R.drawable.ic_baseline_play_for_work_24);
                }
                return true;

            case SWIPE_DOWN:
                if (count == 3){
                    count++;
                    Toast.makeText(this.getApplicationContext(), "Step " + count + " Completed", Toast.LENGTH_SHORT).show();
                    finish();
                    return super.onGesture(gesture);
                }else {
                    return  true;
                }



            default:
                return super.onGesture(gesture);

        }

    }


}