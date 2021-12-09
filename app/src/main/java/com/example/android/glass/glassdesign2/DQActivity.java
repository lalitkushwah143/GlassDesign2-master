package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.glass.glassdesign2.adapter.CardAdapter2;
import com.example.android.glass.glassdesign2.adapter.DQPagerAdapter;
import com.example.android.glass.glassdesign2.data.DataDQ;
import com.example.android.glass.glassdesign2.data.DataModule;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.DQFragment;
import com.example.android.glass.glassdesign2.transformer.DQTransformer;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DQActivity extends BaseActivity {

    DatabaseReference reference, ref1;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    public static ArrayList<DataModule> arrayList = new ArrayList<>();
    private TextView tvPage;
    private ProgressBar progressBar;
    private int index =0;
    private TextView tvTime;
    private ConstraintLayout layout;
    private float mLastOffset;

    private DQTransformer dqTransformer;
    private DQPagerAdapter dqPagerAdapter;

    public static DataModule selectedDataModule = new DataModule();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private TextView tvData;


  //  final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_q);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        viewPager = findViewById(R.id.activity_dq_viewpager);
        indicator = findViewById(R.id.activity_dq_indicator);
        tvPage = findViewById(R.id.activity_dq_tvPage);
        tvData = findViewById(R.id.activity_dq_tvData);

        arrayList = new ArrayList<>();
       // indicator.setupWithViewPager(viewPager, true);
   //     viewPager.setAdapter(screenSliderPagerAdapter);
        progressBar = findViewById(R.id.activity_dq_progressbar);
        tvTime = findViewById(R.id.activity_dq_tvTime);
        layout = findViewById(R.id.activity_dq_layout);
        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        dqPagerAdapter = new DQPagerAdapter(this);

        index =0;
        reference = FirebaseDatabase.getInstance().getReference().child("DQ");
        dqPagerAdapter = new DQPagerAdapter(DQActivity.this);


/*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(progressBar.getVisibility()==View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
                DataDQ dataDQ = snapshot.getValue(DataDQ.class);
                arrayList.add(dataDQ);
                dqPagerAdapter.addCardItem(dataDQ);

                dqTransformer = new DQTransformer(DQActivity.this, viewPager, dqPagerAdapter);
                dqTransformer.enableScaling(true);
                viewPager.setAdapter(dqPagerAdapter);
                viewPager.setPageTransformer(false, dqTransformer);
                viewPager.setOffscreenPageLimit(3);
                indicator.setupWithViewPager(viewPager, true);
          //      fragments.add(DQFragment.newInstance(index, dataDQ.getKey(), dataDQ.getTitle()));
                index++;
              //  screenSliderPagerAdapter.notifyDataSetChanged();
              //  viewPager.setAdapter(screenSliderPagerAdapter);

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


 */
        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tvPage.setText("Module: " +(indicator.getSelectedTabPosition()+ 1) + " of "+ indicator.getTabCount());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (firebaseAuth.getCurrentUser() !=  null){
            if (SplashActivity.machine_id_temp != null && !SplashActivity.machine_id_temp.equals("")){
                firestore.collection("moduleData")
                        .whereEqualTo("mid", SplashActivity.machine_id_temp)
                        .addSnapshotListener( new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                assert value != null;
                                arrayList.clear();
                                fragments.clear();
                                //     viewPager.setSaveFromParentEnabled(false);
                                if(progressBar.getVisibility()==View.VISIBLE){
                                    progressBar.setVisibility(View.GONE);
                                }
                                for (QueryDocumentSnapshot snapshot : value) {
                                    if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("mid") != null) {
                                        arrayList.add(new DataModule(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("desc").toString(),
                                                snapshot.getData().get("mid").toString()));
                                        dqPagerAdapter.addCardItem(new DataModule(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("desc").toString(),
                                                snapshot.getData().get("mid").toString()));
                                        dqTransformer = new DQTransformer(DQActivity.this, viewPager, dqPagerAdapter);
                                        dqTransformer.enableScaling(true);
                                        viewPager.setAdapter(dqPagerAdapter);
                                        viewPager.setPageTransformer(false, dqTransformer);
                                        viewPager.setOffscreenPageLimit(3);
                                        indicator.setupWithViewPager(viewPager, true);
                                        dqPagerAdapter.notifyDataSetChanged();
                                    }else {
                                        Log.e("DQ", "Missing Parameters");
                                    }
                                }


                                if (arrayList.size() == 0){
                                    viewPager.setVisibility(View.GONE);
                                    //  indicator.setVisibility(View.GONE);
                                    tvData.setVisibility(View.VISIBLE);
                                }else {
                                    viewPager.setVisibility(View.VISIBLE);
                                    //   indicator.setVisibility(View.VISIBLE);
                                    tvData.setVisibility(View.GONE);
                                }
                            }
                        });
            }else {
                firestore.collection("moduleData")
                        .whereEqualTo("mid", SplashActivity.machine_id)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                assert value != null;
                                arrayList.clear();
                                fragments.clear();
                                //       viewPager.setSaveFromParentEnabled(false);

                                if(progressBar.getVisibility()==View.VISIBLE){
                                    progressBar.setVisibility(View.GONE);
                                }
                                for (QueryDocumentSnapshot snapshot : value) {
                                    if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("mid") != null) {
                                        arrayList.add(new DataModule(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("desc").toString(),
                                                snapshot.getData().get("mid").toString()));
                                        dqPagerAdapter.addCardItem(new DataModule(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("desc").toString(),
                                                snapshot.getData().get("mid").toString()));

                                        dqTransformer = new DQTransformer(DQActivity.this, viewPager, dqPagerAdapter);
                                        dqTransformer.enableScaling(true);
                                        viewPager.setAdapter(dqPagerAdapter);
                                        viewPager.setPageTransformer(false, dqTransformer);
                                        viewPager.setOffscreenPageLimit(3);
                                        indicator.setupWithViewPager(viewPager, true);
                                        dqPagerAdapter.notifyDataSetChanged();
                                    }else {
                                        Log.e("DQ", "Missing Parameters");
                                    }
                                }

                                if (arrayList.size() == 0){
                                    viewPager.setVisibility(View.GONE);
                                    //   indicator.setVisibility(View.GONE);
                                    tvData.setVisibility(View.VISIBLE);
                                }else {
                                    viewPager.setVisibility(View.VISIBLE);
                                    //  indicator.setVisibility(View.VISIBLE);
                                    tvData.setVisibility(View.GONE);
                                }
                            }
                        });
            }

        }

    }



    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:

                if (tvData.getVisibility() != View.VISIBLE){
                    selectedDataModule = arrayList.get(indicator.getSelectedTabPosition());
                    Intent intent = new Intent(DQActivity.this, QualityActivity.class);
                    //   intent.putExtra("module_key", arrayList.get(indicator.getSelectedTabPosition()).getKey());

                    intent.putExtra("module_key", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                    intent.putExtra("module_title", arrayList.get(indicator.getSelectedTabPosition()).getTitle());
                    Log.e("status", "id" + indicator.getSelectedTabPosition() + "content : " + arrayList.get(indicator.getSelectedTabPosition()).getTitle().toString());
                    Log.e("status", "id" + indicator.getSelectedTabPosition() + "content : " + arrayList.get(indicator.getSelectedTabPosition()).getKey().toString());

                    //  selectedDataModule.setKey(null);
                    startActivity(intent);
                }
/*
                Intent intent = new Intent(DQActivity.this, QualityActivity.class);
                intent.putExtra("module_key", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                startActivity(intent);


 */
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
    @Override
    protected void onResume() {
        super.onResume();
        layout.setBackgroundColor(SplashActivity.back_color);


    }

}