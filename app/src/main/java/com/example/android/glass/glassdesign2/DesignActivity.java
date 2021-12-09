package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataApproval;
import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.data.DataTitle;
import com.example.android.glass.glassdesign2.fragments.ApprovalFragment;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DesignActivity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataTitle> arrayList = new ArrayList<>();

    private String title, key, update_key;


    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        viewPager = findViewById(R.id.activity_design_viewpager);
        indicator = findViewById(R.id.activity_design_indicator);
        tvData = findViewById(R.id.activity_design_tvData);
        tvTitle = findViewById(R.id.activity_design_tvTitle);
        tvTime = findViewById(R.id.activity_design_tvTime);

        firestore = FirebaseFirestore.getInstance();

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        key = getIntent().getStringExtra("key");

        if (TextUtils.isEmpty(key)){
            Toast.makeText(this, "No key", Toast.LENGTH_SHORT).show();
            Log.e("Approval", "No key");
            finish();
        }else {
            indicator.setupWithViewPager(viewPager, true);
            viewPager.setAdapter(screenSliderPagerAdapter);

            firestore.collection("DQNewReport").document(key).collection("content").document("designSpecs").collection("title")
                    .orderBy("index")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("title") != null  && snapshot.getData().get("index") != null) {

                                    arrayList.add(new DataTitle(snapshot.getId(),snapshot.getData().get("title").toString(), snapshot.get("index", Integer.class)));
                                    fragments.add(SingleFragment.newInstance(snapshot.getId(), snapshot.getData().get("title").toString()));
                                    screenSliderPagerAdapter.notifyDataSetChanged();
                                    Log.e("sample", snapshot.getData().toString());
                                }else {
                                    Log.e("Approval2", "Missing Parameters");
                                }
                            }
                            if (arrayList.size() == 0){
                                viewPager.setVisibility(View.GONE);
                                indicator.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                            }else {
                                viewPager.setVisibility(View.VISIBLE);
                                indicator.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                            }
                        }
                    });
            screenSliderPagerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE){
                    Intent intent = new Intent(DesignActivity.this, Design2Activity.class);
                    intent.putExtra("key", key);
                    intent.putExtra( "tid" , arrayList.get(indicator.getSelectedTabPosition()).getKey());
                    intent.putExtra("title", arrayList.get(indicator.getSelectedTabPosition()).getTitle());
                    startActivity(intent);
                }
                return  true;

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