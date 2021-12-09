package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;

import com.example.android.glass.glassdesign2.adapter.CardPagerAdapter;
import com.example.android.glass.glassdesign2.data.DataContact;
import com.example.android.glass.glassdesign2.data.DataModule;
import com.example.android.glass.glassdesign2.data.DataUsers;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.transformer.DQTransformer;
import com.example.android.glass.glassdesign2.transformer.ShadowTransformer;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
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

public class ContactActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    private ArrayList<DataUsers> arrayList = new ArrayList<>();



    private static final int REQUEST_CODE = 301;
    private String MENU_KEY="menu_key";
    public static int page_num = 0;
    public static int num_all = 3;

    private CardPagerAdapter cardPagerAdapter;
    private ShadowTransformer shadowTransformer;
    private TextView tvTime;
    private ConstraintLayout layout;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private TextView tvData;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        viewPager = findViewById(R.id.activity_contact_viewpager);
        indicator = findViewById(R.id.activity_contact_indicator);
        tvTime = findViewById(R.id.activity_contact_tvTime);
        layout = findViewById(R.id.activity_contact_layout);
        tvData = findViewById(R.id.activity_contacts_tvData);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);
        arrayList = new ArrayList<>();
        cardPagerAdapter = new CardPagerAdapter();
        /*
        if (fragments.size()==0){
            fragments.add(ContactFragment.newInstance("akjshsaf", "Operator 1", "4865461", R.drawable.random_face1 , null));
            fragments.add(ContactFragment.newInstance("ajgskdkjasfkhja", "Supervisor",  "86864525", R.drawable.random_face2 , null));
            fragments.add(ContactFragment.newInstance("ajgskdkjasfkhja", "Supervisor",  "86864525", R.drawable.random_face2 , null));
        }



        if (cardPagerAdapter.getCount() == 0){

            cardPagerAdapter.addCardItem(new DataContact("akjshsaf", "Operator 1", "4865461", R.drawable.random_face));
            cardPagerAdapter.addCardItem(new DataContact("ajgskdkjasfkhja", "Supervisor",  "86864525", R.drawable.random_face2));
            cardPagerAdapter.addCardItem(new DataContact("ajgskdkjasfkhja", "Supervisor 2",  "86864525", R.drawable.random_face3));
        }

         */



        firestore.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                assert value != null;
                arrayList.clear();
                fragments.clear();

                for (QueryDocumentSnapshot snapshot : value) {
                    if (snapshot.getData().get("email") != null && snapshot.getData().get("password") != null &&
                            snapshot.getData().get("username") != null && snapshot.getData().get("firstName") != null &&
                            snapshot.getData().get("lastName") != null && snapshot.getData().get("role") != null &&
                            snapshot.getData().get("admin") != null && snapshot.getData().get("phone") != null) {

                        if (snapshot.getData().get("url") != null){
                            DataUsers dataUsers = new DataUsers(snapshot.getId(), snapshot.getData().get("email").toString() , snapshot.getData().get("password").toString(),
                                    snapshot.getData().get("username").toString(), snapshot.getData().get("firstName").toString(), snapshot.getData().get("lastName").toString(),
                                    snapshot.getData().get("phone").toString(), snapshot.getData().get("role").toString(), snapshot.getData().get("url").toString(), snapshot.getBoolean("admin"));
                            arrayList.add(dataUsers);
                            cardPagerAdapter.addCardItem(dataUsers);

                        }else{
                            DataUsers dataUsers = new DataUsers(snapshot.getId(), snapshot.getData().get("email").toString() , snapshot.getData().get("password").toString(),
                                    snapshot.getData().get("username").toString(), snapshot.getData().get("firstName").toString(), snapshot.getData().get("lastName").toString(),
                                    snapshot.getData().get("phone").toString(), snapshot.getData().get("role").toString(), "", snapshot.getBoolean("admin"));
                            arrayList.add(dataUsers);
                            cardPagerAdapter.addCardItem(dataUsers);
                        }

                    }else {
                        Log.e("DQ", "Missing Parameters");
                    }
                }

                if (arrayList.size() == 0){
                    viewPager.setVisibility(View.GONE);
                    //  indicator.setVisibility(View.GONE);
                    tvData.setVisibility(View.VISIBLE);
                }else {
                    shadowTransformer = new ShadowTransformer(ContactActivity.this, viewPager, cardPagerAdapter, "Contact");
                    shadowTransformer.enableScaling(true);

                    viewPager.setAdapter(cardPagerAdapter);
                    viewPager.setPageTransformer(false, shadowTransformer);
                    viewPager.setOffscreenPageLimit(3);
                    indicator.setupWithViewPager(viewPager, true);
                    screenSliderPagerAdapter.notifyDataSetChanged();
                    viewPager.setVisibility(View.VISIBLE);
                    //   indicator.setVisibility(View.VISIBLE);
                    tvData.setVisibility(View.GONE);
                }
            }
        });

    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

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

                if (tvData.getVisibility() != View.VISIBLE) {

                    Intent intent = new Intent(this.getApplicationContext(), VideoPrefsActivity.class);
                    intent.putExtra("step", "General");
                    intent.putExtra("manual", "General");
                    intent.putExtra("manual_name", "General");
                    intent.putExtra("user", arrayList.get(indicator.getSelectedTabPosition()).getEmail());
                    startActivity(intent);
                }
                return true;

            default:

                return super.onGesture(gesture);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.setBackgroundColor(SplashActivity.back_color);
    }
}