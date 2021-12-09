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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VideoPrefsActivity extends BaseActivity {


    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    private ConstraintLayout layout;

    private static final int REQUEST_CODE = 301;
    private String MENU_KEY="menu_key";

    private FirebaseFirestore firestore;
    private DocumentReference documentReference, refRelayed, refRouted;
    private ArrayList<String> arrayList = new ArrayList<>();
    private String step, manual_name, user;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_prefs);

        firestore = FirebaseFirestore.getInstance();

        viewPager = findViewById(R.id.activity_prefs_viewpager);
        indicator = findViewById(R.id.activity_prefs_indicator);

        manual_name = getIntent().getStringExtra("manual_name");
        step = getIntent().getStringExtra("step");
        user = getIntent().getStringExtra("user");


        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);

        if (fragments.size()==0){
            fragments.add(MainLayoutFragment.newInstance("Relayed", "", "", R.drawable.ic_baseline_class_24 , null));
            fragments.add(MainLayoutFragment.newInstance("Routed", "", "", R.drawable.ic_baseline_class_24 , null));
            arrayList.add(0, "Relayed");
            arrayList.add(1, "Routed");
        }

        screenSliderPagerAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                int index = viewPager.getCurrentItem();

                switch (index){

                    case 0:
                        refRelayed = firestore.collection("OpenTokConfig").document("relayed");
                        refRelayed.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot snapshot = task.getResult();
                                    assert snapshot != null;
                                    if (snapshot.exists()){
                                        if (snapshot.getData().get("api_key") != null && snapshot.getData().get("session_id") != null && snapshot.getData().get("token") != null){
                                            documentReference = firestore.collection("OpenTokConfig").document("BkEGCdgSefXrFkEmzcCG");
                                            documentReference.update("api_key", snapshot.getData().get("api_key"));
                                            documentReference.update("session_id", snapshot.getData().get("session_id"));
                                            documentReference.update("token", snapshot.getData().get("token"));

                                            Intent intent = new Intent(VideoPrefsActivity.this, VideoQualityActivity.class);
                                            intent.putExtra("pref_id", arrayList.get(index).toString());
                                            intent.putExtra("step", step);
                                            intent.putExtra("manual", "manual");
                                            intent.putExtra("manual_name", manual_name);
                                            intent.putExtra("user", user);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(VideoPrefsActivity.this, "Selected :  " + arrayList.get(index).toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    }else {
                                        Toast.makeText(VideoPrefsActivity.this, "No data exists", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(VideoPrefsActivity.this, "Unable to fetch Token", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case 1:
                        refRouted = firestore.collection("OpenTokConfig").document("routed");
                        refRouted.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot snapshot = task.getResult();
                                    assert snapshot != null;
                                    if (snapshot.exists()){
                                        if (snapshot.getData().get("api_key") != null && snapshot.getData().get("session_id") != null && snapshot.getData().get("token") != null){
                                            documentReference = firestore.collection("OpenTokConfig").document("BkEGCdgSefXrFkEmzcCG");
                                            documentReference.update("api_key", snapshot.getData().get("api_key"));
                                            documentReference.update("session_id", snapshot.getData().get("session_id"));
                                            documentReference.update("token", snapshot.getData().get("token"));

                                            Intent intent = new Intent(VideoPrefsActivity.this, VideoQualityActivity.class);
                                            intent.putExtra("pref_id", arrayList.get(index).toString());
                                            intent.putExtra("step", step);
                                            intent.putExtra("manual", "manual");
                                            intent.putExtra("manual_name", manual_name);
                                            intent.putExtra("user", user);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(VideoPrefsActivity.this, "Selected :  " + arrayList.get(index).toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    }else {
                                        Toast.makeText(VideoPrefsActivity.this, "No data exists", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(VideoPrefsActivity.this, "Unable to fetch Token", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    default:
                        finish();
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