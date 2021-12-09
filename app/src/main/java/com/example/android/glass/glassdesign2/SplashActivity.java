package com.example.android.glass.glassdesign2;

import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class
SplashActivity extends BaseActivity {

    ContentLoadingProgressBar bar;
    public static int color_code;
    public static int back_color, other_color;
    private int red_value, blue_value, green_value, alpha_value;
    private int red_value2, blue_value2, green_value2, alpha_value2;
    public static Boolean color_deafult = true, color_default2 = true;


    //for new method for machines
    FirebaseAuth firebaseAuth;
    public static String machine_id, role, machine_id_temp;
    public static Boolean login_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

   //     bar=findViewById(R.id.activity_splash_bar);
     //   bar.show();
        SharedPreferences sharedPreferences = getSharedPreferences("glass_prefs", Context.MODE_PRIVATE);

        if (sharedPreferences == null){
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putBoolean("color_default", true);
            editor.putBoolean("color_default2", true);
            editor.putInt("theme_code", 1);
            editor.putInt("color_code", 1);
            editor.putInt("red_value", 89);
            editor.putInt("green_value", 89);
            editor.putInt("blue_value", 89);
            editor.putInt("alpha_value", 255);
            editor.putInt("red_value2", 73);
            editor.putInt("green_value2", 73);
            editor.putInt("blue_value2", 73);
            editor.putInt("alpha_value2", 255);
            back_color = Color.argb(255, 89, 89, 89);
            other_color = Color.argb(255, 73, 73, 73);
            editor.commit();
            editor.apply();
            color_code = 1;
        }else {
            color_deafult =sharedPreferences.getBoolean("color_default", true);
            color_default2 = sharedPreferences.getBoolean("color_default2", true);

            color_code = sharedPreferences.getInt("color_code", 1);

            red_value = sharedPreferences.getInt("red_value",  89);
            green_value = sharedPreferences.getInt("green_value",  89);
            blue_value= sharedPreferences.getInt("blue_value",  89);
            alpha_value = sharedPreferences.getInt("alpha_value",  255);
            back_color = Color.argb(alpha_value, red_value, green_value, blue_value);

            red_value2 = sharedPreferences.getInt("red_value2",  73);
            green_value2 = sharedPreferences.getInt("green_value2",  73);
            blue_value2= sharedPreferences.getInt("blue_value2",  73);
            alpha_value2 = sharedPreferences.getInt("alpha_value2",  255);
            other_color = Color.argb(alpha_value2, red_value2, green_value2, blue_value2);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.e("In Splash", "now");

                firebaseAuth = FirebaseAuth.getInstance();

                if (firebaseAuth.getCurrentUser() == null){
                    SharedPreferences user_prefs= getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor  = user_prefs.edit();
                    editor.putBoolean("login_status", false);
                    editor.putString("machine_id", "");
                    editor.putString("machine_id_temp", "");
                    editor.putString("role", "");
                    editor.commit();
                    editor.apply();

                    login_status = false;
                    role = "";
                    machine_id = "";
                    machine_id_temp = "";
                }else {
                    SharedPreferences user_prefs = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                    if (user_prefs == null){
                        firebaseAuth.signOut();
                        SharedPreferences.Editor editor  = user_prefs.edit();
                        editor.putBoolean("login_status", false);
                        editor.putString("machine_id", "");
                        editor.putString("machine_id_temp", "");
                        editor.putString("role", "");
                        editor.commit();
                        editor.apply();
                        login_status = false;
                        role = "";
                        machine_id = "";
                        machine_id_temp = "";
                    }else {
                        machine_id = user_prefs.getString("machine_id", "");
                        machine_id_temp = "";
                        role = user_prefs.getString("role", "");
                        login_status = true;
                    }
                }

                if (login_status){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
                    intent.putExtra("machine_id", machine_id);
                    intent.putExtra("machine_id_temp", "");
                    intent.putExtra("role", role);
                    startActivity(intent);
                    Log.e("SplashActivity", "initiated");

                    finish();

                }else {

                //    startActivity(new Intent(SplashActivity.this, PrepareActivity.class));
                 //   finish();

                    // for scan Qr Code
                    Intent i = new Intent(SplashActivity.this, ScanActivity.class);
                    startActivity(i);
                    finish();
                }

                //For testing purpose only
                //   Intent intent    = new Intent(SplashActivity.this, MainActivity.class);
                //   intent.putExtra("menu_key", R.menu.menu_admin);
                //   startActivity(intent);

            }
        }, 3000);




      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Fixed activity is this
           //     Intent i = new Intent(SplashActivity.this, ScanActivity.class);
            //    startActivity(i);

                //For testing purpose only
            //    Intent intent    = new Intent(SplashActivity.this, MainActivity.class);
             //   intent.putExtra("menu_key", R.menu.menu_operator);
                Intent intent    = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 3000);

       */
    }

}