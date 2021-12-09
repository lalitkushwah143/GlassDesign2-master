package com.example.android.glass.glassdesign2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glass.ui.GlassGestureDetector;

public class Base2Activity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener {

    private View decorView;
    private GlassGestureDetector glassGestureDetector;
    public static int theme_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  checkTheme();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            hideSystemUI();
                        }
                    }
                });
        glassGestureDetector = new GlassGestureDetector(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (glassGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case SWIPE_DOWN:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void hideSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    private void checkTheme(){

        SharedPreferences sharedPreferences = getSharedPreferences("glass_prefs", Context.MODE_PRIVATE);

        if (sharedPreferences == null){
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("theme_code", 1);
            editor.commit();
            editor.apply();
        }else {

            theme_code = sharedPreferences.getInt("theme_code", 1);
            switch (theme_code) {
                case 1:
                    this.setTheme(R.style.MenuTheme);
                    break;

                case 2:
                    this.setTheme(R.style.MenuThemeWhite);
                    break;
            }
        }
    }

}
