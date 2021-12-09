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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.data.DataManuals;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.MainLayoutFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
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

public class DQNewActivity extends BaseActivity {

    private TextView tvData, tvTime;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataDQNew> arrayList = new ArrayList<>();

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dqnew);

        viewPager = findViewById(R.id.activity_dqnew_viewpager);
        indicator = findViewById(R.id.activity_dqnew_indicator);
        tvData = findViewById(R.id.activity_dqnew_tvData);
        tvTime = findViewById(R.id.activity_dqnew_tvTime);

        firestore = FirebaseFirestore.getInstance();

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);


        firestore.collection("DQNew")
                .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        fragments.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            Log.e("MachineActivity: ", snapshot.getData().toString());
                            if (snapshot.getData().get("name") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("mid") != null) {
                                arrayList.add(new DataDQNew(snapshot.getId(), snapshot.getData().get("mid").toString(),
                                                snapshot.getData().get("name").toString(), snapshot.getData().get("desc").toString()));
                                fragments.add(SingleFragment.newInstance( snapshot.getId(), snapshot.getData().get("name").toString()));
                                screenSliderPagerAdapter.notifyDataSetChanged();
                            }else {
                                Log.e("DQNew", "Missing Parameters");
                            }
                        }
                        if (arrayList.size()==0){
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
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE){
                    Intent intent = new Intent(DQNewActivity.this, ContentActivity.class);
                    intent.putExtra("title", arrayList.get(indicator.getSelectedTabPosition()).getName());
                    intent.putExtra("key", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                    intent.putExtra("action", 0);
                    startActivity(intent);
                    Log.e("key", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                }
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