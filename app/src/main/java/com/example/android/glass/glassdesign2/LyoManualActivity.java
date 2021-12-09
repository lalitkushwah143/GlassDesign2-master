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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataLyo;
import com.example.android.glass.glassdesign2.data.DataManuals;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.MainLayoutFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LyoManualActivity extends Base2Activity {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    private ConstraintLayout layout;
    private TextView tvData;

    private static final int REQUEST_CODE = 301;
    private String MENU_KEY="menu_key";

    private FirebaseFirestore firestore;
    private ArrayList<DataManuals> arrayList = new ArrayList<>();

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lyo_manual);

        firestore = FirebaseFirestore.getInstance();

        viewPager = findViewById(R.id.activity_lyo_viewpager);
        indicator = findViewById(R.id.activity_lyo_indicator);
        layout = findViewById(R.id.activity_lyo_manual_layout);
        tvData = findViewById(R.id.activity_lyo_manual_tvData);


        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);


        ManualActivity.FLAG5 = false;
        ManualActivity.FLAG8 = false;
/*
        if (fragments.size()==0){
            fragments.add(MainLayoutFragment.newInstance(getString(R.string.batch_manual), "", "", R.drawable.baseline_class_24 , null));
            fragments.add(MainLayoutFragment.newInstance(getString(R.string.defrost_manual), "", "", R.drawable.baseline_class_24 , null));
            fragments.add(MainLayoutFragment.newInstance(getString(R.string.skill_test), "", "", R.drawable.baseline_import_contacts_24 , null));
        }

 */
        screenSliderPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:

                if (tvData.getVisibility() != View.VISIBLE) {
                    int index = viewPager.getCurrentItem();

                    Intent intent = new Intent(this.getApplicationContext(), ViewManualActivity.class);
                    intent.putExtra("manual_id", arrayList.get(index).getKey());
                    intent.putExtra("manual_name", arrayList.get(index).getTitle());
                    Log.e("Cameras", arrayList.get(index).getTitle());
                    startActivity(intent);
                    Toast.makeText(this, "Opening " + arrayList.get(index).getTitle().toString(), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onGesture(gesture);
        }
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            switch (id) {
                case R.id.bBatchManual:
                    Intent intent = new Intent(this.getApplicationContext(), ManualActivity.class);
                    intent.putExtra("manual_type", "batch");
                    startActivity(intent);
                    break;
                case R.id.bDefrostManual:
                    Intent intent1 = new Intent(this.getApplicationContext(), ManualActivity.class);
                    intent1.putExtra("manual_type", "defrost");
                    startActivity(intent1);
                    break;
            }
        }
    }


 */

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

        if (SplashActivity.machine_id_temp != null && !SplashActivity.machine_id_temp.equals("")){
            firestore.collection("manualData")
                    .whereEqualTo("mid", SplashActivity.machine_id_temp)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                Log.e("MachineActivity: ", snapshot.getData().toString());
                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("mid") != null) {
                                    arrayList.add(new DataManuals(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString()));
                                    fragments.add(MainLayoutFragment.newInstance(snapshot.getData().get("title").toString(), "", "", R.drawable.ic_baseline_class_24, null));
                                    screenSliderPagerAdapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Manual", "Missing Parameters");
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
        }else {
            firestore.collection("manualData")
                    .whereEqualTo("mid", SplashActivity.machine_id)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                Log.e("MachineActivity: ", snapshot.getData().toString());
                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("mid") != null) {
                                    arrayList.add(new DataManuals(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString()));
                                    fragments.add(MainLayoutFragment.newInstance(snapshot.getData().get("title").toString(), "", "", R.drawable.ic_baseline_class_24, null));
                                    screenSliderPagerAdapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Manual", "Missing Parameters");
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


    }
}