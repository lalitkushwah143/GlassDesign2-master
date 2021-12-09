package com.example.android.glass.glassdesign2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataCheck;
import com.example.android.glass.glassdesign2.data.DataStep;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;

import com.example.android.glass.glassdesign2.fragments.NewReportFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Report extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    public static CustomViewPager viewPager;
    private TabLayout indicator;

    private ArrayList<DataStep> arrayList = new ArrayList<>();
    public static ArrayList<DataCheck> dataChecks = new ArrayList<>();
    private TextView tvTime,textView;
    private FirebaseFirestore firestore;
    //private TextView tvData;

    final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
            getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        viewPager = findViewById(R.id.report_viewPager);
        //tvData = findViewById(R.id.report_tvData);

        tvTime = findViewById(R.id.report_tvTime);
        textView = findViewById(R.id.report_tvPage);
        arrayList=new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);
        // manual_key = getIntent().getStringExtra("manual_id");
        //manual_name = getIntent().getStringExtra("manual_name");
        //manual_key = "5sVLWlomDedmDWFR2xzj";
        //manual_name = "Batch Manual";

        indicator = findViewById(R.id.report_page_indicator);
        indicator.setupWithViewPager(viewPager, true);

        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                textView.setText((indicator.getSelectedTabPosition()+1) + " of "+ indicator.getTabCount());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//.whereEqualTo("manual_id", manual_key)
        firestore.collection("stepData")
                .addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        fragments.clear();
                        dataChecks.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("index") != null && snapshot.getData().get("title") != null &&
                                    snapshot.getData().get("desc") != null && snapshot.getData().get("format") != null &&
                                    snapshot.getData().get("url") != null && snapshot.getData().get("type") != null ) {
                                arrayList.add(new DataStep(snapshot.getId(),
                                        Integer.parseInt(snapshot.getData().get("index").toString()),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("format").toString(),
                                        snapshot.getData().get("url").toString(),
                                        snapshot.getData().get("type").toString(),
                                        snapshot.getData().get("manual_id").toString()));
                            }
                        }
                        arrayList.sort(new Comparator<DataStep>() {
                            @Override
                            public int compare(DataStep dataStep, DataStep dataStep1) {
                                return Integer.compare(dataStep.getIndex(), dataStep1.getIndex());
                            }
                        });

                        for ( int i =0; i<arrayList.size(); i++){
                            DataStep dataStep = arrayList.get(i);

                            fragments.add(NewReportFragment.newInstance(dataStep.getIndex(),
                                    dataStep.getType(),
                                    dataStep.getTitle(),
                                    dataStep.getFormat(),
                                    dataStep.getUrl(),
                                    dataStep.getDesc()));
                           // screenSlidePagerAdapter.notifyDataSetChanged();
                            viewPager.setAdapter(screenSlidePagerAdapter);
                        }



                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

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
