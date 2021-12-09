package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.data.DataMachines;
import com.example.android.glass.glassdesign2.data.DataModule;
import com.example.android.glass.glassdesign2.data.DataModuleNew;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.MachineFragment;
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

public class ModuleActivity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime;
    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataModuleNew> arrayList = new ArrayList<>();

    private String title, key;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        tvData = findViewById(R.id.activity_module_tvData);
        tvTitle = findViewById(R.id.activity_module_tvTitle);
        tvTime = findViewById(R.id.activity_module_tvTime);
        viewPager = findViewById(R.id.activity_module_viewpager);
        indicator = findViewById(R.id.activity_module_indicator);
        constraintLayout = findViewById(R.id.activity_module_layout);

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

        if (!TextUtils.isEmpty(key)){
            firestore.collection("DQNewReport").document(key).collection("content").document("config").collection("modules")
                    .orderBy("index")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("type") != null && snapshot.getData().get("index") != null) {

                                    arrayList.add(new DataModuleNew(snapshot.getId(),snapshot.getData().get("title").toString(), snapshot.getData().get("desc").toString(), snapshot.get("index", Integer.class), snapshot.get("type", Integer.class)));
                                    fragments.add(SingleFragment.newInstance(snapshot.getId(), snapshot.getData().get("title").toString()));
                                    screenSliderPagerAdapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Machines", "Missing Parameters");
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
        }else {
            finish();
            Toast.makeText(this, "No data Received", Toast.LENGTH_SHORT).show();
        }

        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);
        screenSliderPagerAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE){
                    if (arrayList.get(indicator.getSelectedTabPosition()).getType() == 0){
                        Intent intent = new Intent(ModuleActivity.this, ComponentActivity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("type", arrayList.get(indicator.getSelectedTabPosition()).getType());
                        intent.putExtra("module_id", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                        intent.putExtra("module_title", arrayList.get(indicator.getSelectedTabPosition()).getTitle());
                        startActivity(intent);
                    }else  if (arrayList.get(indicator.getSelectedTabPosition()).getType() == 1){
                        Intent intent = new Intent(ModuleActivity.this, Component2Activity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("type", arrayList.get(indicator.getSelectedTabPosition()).getType());
                        intent.putExtra("module_id", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                        intent.putExtra("module_title", arrayList.get(indicator.getSelectedTabPosition()).getTitle());
                        startActivity(intent);
                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        constraintLayout.setBackgroundColor(SplashActivity.back_color);
    }
}