    package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataMCQ;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.ColorFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    public static ArrayList<DataMCQ> arrayList = new ArrayList<>();
    private TextView tvPage;

    private static final int REQUEST_CODE = 601;
    private String MENU_KEY="menu_key";

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_theme);

        viewPager = findViewById(R.id.activity_theme_viewpager);
        indicator = findViewById(R.id.activity_theme_indicator);

        fragments.add(ColorFragment.newInstance("Green Theme", 1));
        fragments.add(ColorFragment.newInstance("Red Theme", 2));
        fragments.add(ColorFragment.newInstance("Yellow Theme", 3));
        fragments.add(ColorFragment.newInstance("Blue Theme", 4));
        fragments.add(ColorFragment.newInstance("Orange Theme", 5));
        fragments.add(ColorFragment.newInstance("Purple Theme", 6));
        screenSliderPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(screenSliderPagerAdapter);
        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
            /*    Intent intent = new Intent(ThemeActivity.this, MenuActivity.class);
                intent.putExtra(MENU_KEY, R.menu.menu_test);
                startActivityForResult(intent, REQUEST_CODE );
                break;

             */
                SharedPreferences sharedPreferences = getSharedPreferences("glass_prefs", Context.MODE_PRIVATE);

                if (sharedPreferences == null){
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt("theme_code", 1);
                    editor.putInt("color_code", 1);
                    editor.commit();
                    editor.apply();
                }else {
                 /*   switch (indicator.getSelectedTabPosition()){
                        case  0:
                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putInt("theme_code", 1);
                            editor.commit();
                            editor.apply();
                            Toast.makeText(this, "black Is selected", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
                            SharedPreferences.Editor editor1= sharedPreferences.edit();
                            editor1.putInt("theme_code", 2);
                            editor1.commit();
                            editor1.apply();
                            // this.setTheme(R.style.AppThemeWhite);
                            Toast.makeText(this, "white Is selected", Toast.LENGTH_SHORT).show();
                            break;
                    }

                  */
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    switch (indicator.getSelectedTabPosition()){
                        case 0:
                            editor.putInt("color_code", 1);
                            editor.commit();
                            editor.apply();
                            SplashActivity.color_code = 1;
                            Toast.makeText(this, "Green Selected", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
                            editor.putInt("color_code", 2);
                            editor.commit();
                            editor.apply();
                            SplashActivity.color_code = 2;
                            Toast.makeText(this, "Red Selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            editor.putInt("color_code", 3);
                            editor.commit();
                            editor.apply();
                            SplashActivity.color_code = 3;
                            Toast.makeText(this, "Yellow Selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            editor.putInt("color_code", 4);
                            editor.commit();
                            editor.apply();
                            SplashActivity.color_code = 4;
                            Toast.makeText(this, "Blue Selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            editor.putInt("color_code", 5);
                            editor.commit();
                            editor.apply();
                            SplashActivity.color_code = 5;
                            Toast.makeText(this, "Orange Selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            editor.putInt("color_code", 6);
                            editor.commit();
                            editor.apply();
                            SplashActivity.color_code = 6;
                            Toast.makeText(this, "Purple Selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

        }

        return super.onGesture(gesture);
    }
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        int mPendingIntentId = 300;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}