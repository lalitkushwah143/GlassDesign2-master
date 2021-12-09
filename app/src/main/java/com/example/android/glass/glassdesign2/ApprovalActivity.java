package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
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
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApprovalActivity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataDQNew> arrayList = new ArrayList<>();

    private String title, key, update_key;


    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        viewPager = findViewById(R.id.activity_approval_viewpager);
        indicator = findViewById(R.id.activity_approval_indicator);
        tvData = findViewById(R.id.activity_approval_tvData);
        tvTitle = findViewById(R.id.activity_approval_tvTitle);
        tvTime = findViewById(R.id.activity_approval_tvTime);

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

            if (fragments.size() == 0){
                fragments.add(SingleFragment.newInstance("One", "Vendor - Lyophilization System Inc."));
                fragments.add(SingleFragment.newInstance("Two", "Customer"));
            }
            screenSliderPagerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                switch (indicator.getSelectedTabPosition()){
                    case 0:
                        Intent intent = new Intent(ApprovalActivity.this, Approval2Activity.class);
                        intent.putExtra("key", key);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                        break;

                    case 1:
                        Intent intent1 = new Intent(ApprovalActivity.this, Approval2Activity.class);
                        intent1.putExtra("key", key);
                        intent1.putExtra("type", 1);
                        startActivity(intent1);
                        break;

                    default:
                        // do nothing
                        break;
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