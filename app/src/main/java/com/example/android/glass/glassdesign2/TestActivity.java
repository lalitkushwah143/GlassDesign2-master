package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.android.glass.glassdesign2.data.DataMCQ;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.TestFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    public static ArrayList<DataMCQ> arrayList = new ArrayList<>();
    private TextView tvPage, tvTime;
    private ConstraintLayout layout;
    private RecyclerView recyclerView;


    private final String ACCESS_TOKEN="ds7P8ue7X0PmwaS-pSYAIZqbCZ1ZhrwO4iASYCmsTJY";
    private final String SPACE_ID= "h7copa94aofe";

    private static final int REQUEST_CODE = 701;
    private String MENU_KEY="menu_key";
    private DatabaseReference reference, ref1;
    public static ArrayList<Integer> responses = new ArrayList<>();
    private Boolean FLAG = false;
    private TextView tvResponse;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        viewPager = findViewById(R.id.activity_test_viewpager);
        indicator = findViewById(R.id.activity_test_indicator);
        tvPage = findViewById(R.id.activity_test_tvPage);
        tvResponse = findViewById(R.id.activity_test_tvResponse);
        arrayList = new ArrayList<>();
        responses = new ArrayList<>(Arrays.asList(4,4,4,4,4));
        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);
        tvTime = findViewById(R.id.activity_test_tvTime);
        layout = findViewById(R.id.activity_test_layout);

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        reference = FirebaseDatabase.getInstance().getReference().child("skilltest");

        reference.orderByChild("index").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DataMCQ dataMCQ = snapshot.getValue(DataMCQ.class);
                arrayList.add(dataMCQ);
                fragments.add(TestFragment.newInstance(dataMCQ.getKey(),dataMCQ.getIndex(), dataMCQ.getQuestion(), dataMCQ.getAns(), dataMCQ.getAnsID(), 4));
                screenSliderPagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(screenSliderPagerAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tvPage.setText("Question: " + (indicator.getSelectedTabPosition()+1) + " of "+ indicator.getTabCount());
                switch (responses.get(indicator.getSelectedTabPosition())){

                    case 0:
                        tvResponse.setText("Selected answer: A");
                        break;
                    case 1:
                        tvResponse.setText("Selected answer: B");
                        break;
                    case 2:
                        tvResponse.setText("Selected answer: C");
                        break;
                    case 3:
                        tvResponse.setText("Selected answer: D");
                        break;
                    case 4:
                        tvResponse.setText("Not Answered");
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                Intent intent = new Intent(TestActivity.this, MenuActivity.class);
                intent.putExtra(MENU_KEY, R.menu.menu_test);
                startActivityForResult(intent, REQUEST_CODE );
                break;
        }

        return super.onGesture(gesture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);

            int response = 4;

            switch (id) {
                case R.id.bOptionA:
                    responses.set(indicator.getSelectedTabPosition(), 0);
                    response = 0;
                    break;
                case R.id.bOptionB:
                    responses.set(indicator.getSelectedTabPosition(), 1);
                    response = 1;
                    break;
                case R.id.bOptionC:
                    responses.set(indicator.getSelectedTabPosition(), 2);
                    response = 2;
                    break;
            /*    case R.id.bOptionD:
                    responses.set(indicator.getSelectedTabPosition(), 3);
                    response = 3;
                    break;

             */
            }
            indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_round_check_circle_24));
            indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_normal), PorterDuff.Mode.SRC_IN);

            DataMCQ dataMCQ = arrayList.get(indicator.getSelectedTabPosition());
            fragments.set(indicator.getSelectedTabPosition(), TestFragment.newInstance(dataMCQ.getKey(), dataMCQ.getIndex(), dataMCQ.getQuestion(), dataMCQ.getAns(), dataMCQ.getAnsID(), response));
            screenSliderPagerAdapter.notifyDataSetChanged();


            Log.e("Arraylist: ", responses.toString());
            for (int i=0; i< arrayList.size(); i++){
                if (responses.get(i) != 4){
                    FLAG = true;
                }else {
                    FLAG = false;
                    break;
                }
            }
            if (FLAG){
                startActivity(new Intent(TestActivity.this, ResultActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.setBackgroundColor(SplashActivity.back_color);
    }
}