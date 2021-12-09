package com.example.android.glass.glassdesign2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.firebase.database.DatabaseReference;

public class SelectActivity extends BaseActivity {

    private TextView tvRed, tvGreen, tvBlue, tvAlpha, tvCurrent;
    private ConstraintLayout layout;
    private TextView tvTitle;
    private ConstraintLayout layout2;

    private static int REQUEST_CODE = 799;
    private String MENU_KEY="menu_key";

    private int selection = 0;
    private int rValue =0, rGreen = 0, rBlue =0, ralpha =255;
    private int rValue2 =0, rGreen2 = 0, rBlue2 =0, ralpha2 =255;
    public static int color;
    public static int color2;
    private int select_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        tvRed = findViewById(R.id.activity_select_tvRed);
        tvGreen = findViewById(R.id.activity_select_tvGreen);
        tvBlue = findViewById(R.id.activity_select_tvBlue);
        tvAlpha = findViewById(R.id.activity_select_tvAlpha);
        tvCurrent = findViewById(R.id.activity_select_tvCurrent);
        tvTitle = findViewById(R.id.activity_select_tvTitle);
        layout2 = findViewById(R.id.activity_select_layout2);
        layout = findViewById(R.id.activity_select_previewLayout);

        select_code = getIntent().getIntExtra("select_code", 0);

        SharedPreferences sharedPreferences = getSharedPreferences("glass_prefs", Context.MODE_PRIVATE);

        switch (select_code){
            case 0:
                tvTitle.setText("Setting default theme");
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putInt("red_value", 89);
                editor.putInt("green_value", 89);
                editor.putInt("blue_value", 89);
                editor.putInt("alpha_value", 255);
                editor.putInt("red_value2", 73);
                editor.putInt("green_value2", 73);
                editor.putInt("blue_value2", 73);
                editor.putInt("alpha_value2", 255);
                editor.putBoolean("color_default", true);
                editor.putBoolean("color_default2", true);

                SplashActivity.back_color = Color.argb(255, 89, 89, 89);
                SplashActivity.other_color = Color.argb(255, 73, 73, 73);
                SplashActivity.color_deafult = true;
                SplashActivity.color_default2 = true;

                editor.commit();
                editor.apply();
                finish();
                break;
            case 1:
                /*
                rValue = sharedPreferences.getInt("red_value",  89);
                rGreen = sharedPreferences.getInt("green_value",  89);
                rBlue= sharedPreferences.getInt("blue_value",  89);
                ralpha = sharedPreferences.getInt("alpha_value",  255);
                SplashActivity.back_color = Color.argb(ralpha, rValue, rGreen, rBlue);

                rValue2 = sharedPreferences.getInt("red_value2",  73);
                rGreen2 = sharedPreferences.getInt("green_value2",  73);
                rBlue2= sharedPreferences.getInt("blue_value2",  73);
                ralpha2 = sharedPreferences.getInt("alpha_value2",  255);
                SplashActivity.other_color = Color.argb(ralpha2, rValue2, rGreen2, rBlue2);

                 */
                tvTitle.setText("Background Color");
                if (SplashActivity.color_deafult){
                    rValue = 0;
                    rGreen = 0;
                    rBlue= 0;
                    ralpha = 255;
                }else {
                    rValue = sharedPreferences.getInt("red_value",  89);
                    rGreen = sharedPreferences.getInt("green_value",  89);
                    rBlue= sharedPreferences.getInt("blue_value",  89);
                    ralpha = sharedPreferences.getInt("alpha_value",  255);
                }
                color = Color.argb(rValue, rGreen, rBlue, ralpha);

                rValue2 = sharedPreferences.getInt("red_value2",  73);
                rGreen2 = sharedPreferences.getInt("green_value2",  73);
                rBlue2= sharedPreferences.getInt("blue_value2",  73);
                ralpha2 = sharedPreferences.getInt("alpha_value2",  255);
                color2 = Color.argb(ralpha2, rValue2, rGreen2, rBlue2);

                tvRed.setText("Red: " +rValue);
                tvGreen.setText("Green: " +rGreen);
                tvBlue.setText("Blue: " +rBlue);
                tvAlpha.setText("Alpha: " +ralpha);
                break;
            case 2:
                tvTitle.setText("Other Color");
                if (SplashActivity.color_default2){
                    rValue2 = 0;
                    rGreen2 = 0;
                    rBlue2 = 0;
                    ralpha2 = 255;
                }else {
                    rValue2 = sharedPreferences.getInt("red_value2",  73);
                    rGreen2 = sharedPreferences.getInt("green_value2",  73);
                    rBlue2 = sharedPreferences.getInt("blue_value2",  73);
                    ralpha2 = sharedPreferences.getInt("alpha_value2",  255);
                }
                color2 = Color.argb(rValue2, rGreen2, rBlue2, ralpha2);

                rValue = sharedPreferences.getInt("red_value",  89);
                rGreen = sharedPreferences.getInt("green_value",  89);
                rBlue= sharedPreferences.getInt("blue_value",  89);
                ralpha = sharedPreferences.getInt("alpha_value",  255);
                color = Color.argb(ralpha, rValue, rGreen, rBlue);

                tvRed.setText("Red: " +rValue2);
                tvGreen.setText("Green: " +rGreen2);
                tvBlue.setText("Blue: " +rBlue2);
                tvAlpha.setText("Alpha: " +ralpha2);
                break;
        }
        layout.setBackgroundColor(color);
        layout2.setBackgroundColor(color2);

/*
        Log.e("Colors: R ", rValue+ "");
        Log.e("Colors: G ", rGreen+ "");

        Log.e("Colors: B ", rBlue+ "");
        Log.e("Colors: A ", ralpha+ "");


 */


    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                Intent intent = new Intent(SelectActivity.this, MenuActivity.class);
                intent.putExtra(MENU_KEY, R.menu.menu_select);
                startActivityForResult(intent, REQUEST_CODE );
                break;

            case SWIPE_FORWARD:
                if (select_code ==1){
                    changeForward();


                }else if (select_code ==2){
                    changeForward2();

                }
                break;

            case SWIPE_BACKWARD:
                if (select_code ==1){
                    changeBackward();


                }else if (select_code ==2){
                    changeBackward2();

                }
                break;

            case SWIPE_DOWN:
                SharedPreferences sharedPreferences = getSharedPreferences("glass_prefs", Context.MODE_PRIVATE);

                if (select_code ==1){
                    if (rValue != 0 || rGreen != 0 || rBlue != 0 || ralpha != 255){
                        SharedPreferences.Editor editor= sharedPreferences.edit();
                        SplashActivity.back_color = Color.argb(ralpha, rValue, rGreen, rBlue);
                        editor.putInt("red_value", rValue);
                        editor.putInt("green_value", rGreen);
                        editor.putInt("blue_value", rBlue);
                        editor.putInt("alpha_value", ralpha);
                        editor.putBoolean("color_default", false);
                        editor.commit();
                        editor.apply();
                    }else {
                        //Nothing changed
                    }

                    finish();

                }else if (select_code == 2){
                    if (rValue2 != 0 || rGreen2 != 0 || rBlue2 != 0 || ralpha2 != 255) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        SplashActivity.other_color = Color.argb(ralpha2, rValue2, rGreen2, rBlue2);
                        editor.putInt("red_value2", rValue2);
                        editor.putInt("green_value2", rGreen2);
                        editor.putInt("blue_value2", rBlue2);
                        editor.putInt("alpha_value2", ralpha2);
                        editor.putBoolean("color_default2", false);
                        editor.commit();
                        editor.apply();
                    }else {
                        //Nothing Chnaged
                    }
                        finish();
                    }

                break;
        }

        color = Color.argb(ralpha, rValue, rGreen, rBlue);
        color2 = Color.argb(ralpha2, rValue2, rGreen2, rBlue2);
        layout.setBackgroundColor(color);
        layout2.setBackgroundColor(color2);
        if (select_code ==1){
            tvRed.setText("Red: " +rValue);
            tvGreen.setText("Green: " +rGreen);
            tvBlue.setText("Blue: " +rBlue);
            tvAlpha.setText("Alpha: " +ralpha);

        }else if (select_code == 2){
            tvRed.setText("Red: " +rValue2);
            tvGreen.setText("Green: " +rGreen2);
            tvBlue.setText("Blue: " +rBlue2);
            tvAlpha.setText("Alpha: " +ralpha2);
        }


        return super.onGesture(gesture);
    }

    private void changeForward(){
        switch (selection){

            case 0:
                if (rValue<250){
                    rValue = rValue+25;
                }
                break;
            case 1:
                if (rGreen<250){
                    rGreen = rGreen+25;
                }
                break;
            case 2:
                if (rBlue<250){
                    rBlue = rBlue+25;
                }
                break;

            case 3:
                if (ralpha<250){
                    ralpha = ralpha+25;
                }
                break;
        }

    }

    private void changeBackward(){
        switch (selection){

            case 0:
                if (rValue>=25){
                    rValue = rValue-25;
                }
                break;
            case 1:
                if (rGreen>=25){
                    rGreen = rGreen-25;
                }
                break;
            case 2:
                if (rBlue>=25){
                    rBlue = rBlue-25;
                }
                break;

            case 3:
                if (ralpha>=25){
                    ralpha = ralpha-25;
                }
                break;
        }
    }

    private void changeForward2(){
        switch (selection){

            case 0:
                if (rValue2<250){
                    rValue2 = rValue2+25;
                }
                break;
            case 1:
                if (rGreen2<250){
                    rGreen2 = rGreen2+25;
                }
                break;
            case 2:
                if (rBlue2<250){
                    rBlue2 = rBlue2+25;
                }
                break;

            case 3:
                if (ralpha2<250){
                    ralpha2 = ralpha2+25;
                }
                break;
        }

    }

    private void changeBackward2(){
        switch (selection){

            case 0:
                if (rValue2>=25){
                    rValue2 = rValue2-25;
                }
                break;
            case 1:
                if (rGreen2>=25){
                    rGreen2 = rGreen2-25;
                }
                break;
            case 2:
                if (rBlue2>=25){
                    rBlue2 = rBlue2-25;
                }
                break;

            case 3:
                if (ralpha2>=25){
                    ralpha2 = ralpha2-25;
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);

            switch (id) {
                case R.id.bRed:
                    selection = 0;
                    tvCurrent.setText("Currently Changing: Red");

                    break;
                case R.id.bGreen:
                    selection =1;
                    tvCurrent.setText("Currently Changing: Green");

                    break;
                case R.id.bBlue:
                    selection = 2;
                    tvCurrent.setText("Currently Changing: Blue");

                    break;
                case R.id.bAlpha:
                    selection = 3;
                    tvCurrent.setText("Currently Changing: Alpha");

                    break;
            }
        }
    }
}