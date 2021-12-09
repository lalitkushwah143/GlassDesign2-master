package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataManuals;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.MainLayoutFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VideoQualityActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    private ConstraintLayout layout;

    private static final int REQUEST_CODE = 301;
    private String MENU_KEY="menu_key";

    private FirebaseFirestore firestore;
    private ArrayList<String> arrayList = new ArrayList<>();
    private String pref_id, manual_name, step, user;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_quality);

        firestore = FirebaseFirestore.getInstance();

        pref_id = getIntent().getStringExtra("pref_id");
        manual_name = getIntent().getStringExtra("manual_name");
        user = getIntent().getStringExtra("user");
        step = getIntent().getStringExtra("step");

        viewPager = findViewById(R.id.activity_video_quality_viewpager);
        indicator = findViewById(R.id.activity_video_quality_indicator);


        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);

        if (fragments.size()==0){
            fragments.add(MainLayoutFragment.newInstance("AUTO", "", "", R.drawable.ic_baseline_class_24 , null));
            fragments.add(MainLayoutFragment.newInstance("LOW", "", "", R.drawable.ic_baseline_class_24 , null));
            fragments.add(MainLayoutFragment.newInstance("MEDIUM", "", "", R.drawable.ic_baseline_class_24 , null));
            fragments.add(MainLayoutFragment.newInstance("HIGH", "", "", R.drawable.ic_baseline_class_24 , null));
            arrayList.clear();
            arrayList.add(0, "AUTO");
            arrayList.add(1, "LOW");
            arrayList.add(2, "MEDIUM");
            arrayList.add(3, "HIGH");

        }
        screenSliderPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                int index = viewPager.getCurrentItem();

                Intent intent = new Intent(this.getApplicationContext(), MultiVideoCallActivity.class);
                intent.putExtra("pref_id", pref_id);
                intent.putExtra("step", step);
                intent.putExtra("user", user);
                intent.putExtra("manual", "General");
                intent.putExtra("manual_name", manual_name);
                intent.putExtra("resolution", arrayList.get(index).toString());
                startActivity(intent);
                finish();
                Toast.makeText(this, "Selected :  " + arrayList.get(index).toString(), Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onGesture(gesture);
        }
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
}