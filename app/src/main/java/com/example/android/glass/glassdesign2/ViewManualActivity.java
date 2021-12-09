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
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataCheck;
import com.example.android.glass.glassdesign2.data.DataStep;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.NewLyoFragment;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewManualActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    public static CustomViewPager viewPager;
    private TabLayout indicator;

    private ArrayList<DataStep> arrayList = new ArrayList<>();
    public static ArrayList<DataCheck> dataChecks = new ArrayList<>();

    private static final int REQUEST_CODE = 201;
    private String MENU_KEY="menu_key";

    public static Boolean FLAG5 = false;
    public static Boolean FLAG8 = false;
    private TextView textView;
    private String manual_key, manual_name;
    private FirebaseFirestore firestore;
    private MediaPlayer mPlayer = new MediaPlayer();

    private TextView tvData;

    final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
            getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_manual);

        viewPager = findViewById(R.id.activity_view_manual_viewPager);
     //   viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        tvData = findViewById(R.id.activity_view_manual_tvData);


        textView = findViewById(R.id.activity_view_manual_tvPage);
        arrayList=new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

       // manual_key = getIntent().getStringExtra("manual_id");
        //manual_name = getIntent().getStringExtra("manual_name");
        manual_key = "5sVLWlomDedmDWFR2xzj";
        manual_name = "Batch Manual";

        indicator = findViewById(R.id.activity_view_manual_page_indicator);
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

        firestore.collection("stepData")
                .whereEqualTo("manual_id", manual_key)
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
                                    snapshot.getData().get("url") != null && snapshot.getData().get("type") != null &&
                                    snapshot.getData().get("manual_id") != null) {
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
                            if (dataStep.getType().equals("camera")){
                                dataChecks.add(new DataCheck(i, false));
                                Log.e("View", i + "index");
                            }
                            fragments.add(NewLyoFragment.newInstance(dataStep.getIndex(),
                                    dataStep.getType(),
                                    dataStep.getTitle(),
                                    dataStep.getFormat(),
                                    dataStep.getUrl(),
                                    dataStep.getDesc()));
                            screenSlidePagerAdapter.notifyDataSetChanged();
                            viewPager.setAdapter(screenSlidePagerAdapter);
                        }
                        for (int i =0; i < dataChecks.size(); i++){
                            Log.e("Views", dataChecks.get(i).getIndex() + "and" + dataChecks.size());
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
                        //  viewPager.setPagingEnabled(false);
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i =0; i<dataChecks.size(); i++){
                    Log.e("View", "index in array" + i);
                    if (position == dataChecks.get(i).getIndex() && !dataChecks.get(i).getStatus()){
                        viewPager.setPagingEnabled(false);
                        break;
                    }else {
                        viewPager.setPagingEnabled(true);
                    }
                }

                if (mPlayer != null && mPlayer.isPlaying()){

                    mPlayer.stop();
                }

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

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case SWIPE_UP:
                Intent intent = new Intent(ViewManualActivity.this, MenuActivity.class);
                intent.putExtra(MENU_KEY, R.menu.menu_call);
                startActivityForResult(intent, REQUEST_CODE );
                return true;

            case TAP:
                if (tvData.getVisibility() != View.VISIBLE) {
                    if (arrayList.get(viewPager.getCurrentItem()).getFormat().equals("audio")) {

                        Intent audioIntent = new Intent(ViewManualActivity.this, MediaActivity.class);
                        audioIntent.putExtra("type", "audio");
                        audioIntent.putExtra("url", arrayList.get(viewPager.getCurrentItem()).getUrl());
                        startActivity(audioIntent);
                    /*
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                    } else {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        if (!arrayList.get(viewPager.getCurrentItem()).getUrl().equals("")) {
                            try {
                                mPlayer.setDataSource(arrayList.get(viewPager.getCurrentItem()).getUrl());
                                mPlayer.prepare();
                                mPlayer.start();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                     */
                    } else if (arrayList.get(viewPager.getCurrentItem()).getFormat().equals("video")) {

                        Intent videoIntent = new Intent(ViewManualActivity.this, MediaActivity.class);
                        videoIntent.putExtra("type", "video");
                        videoIntent.putExtra("url", arrayList.get(viewPager.getCurrentItem()).getUrl());
                        startActivity(videoIntent);
/*
                    if (videoView.isPlaying()){
                        videoView.stopPlayback();
                        videoView.setVisibility(View.GONE);
                    }else {
                        videoView.setVideoPath(arrayList.get(viewPager.getCurrentItem()).getUrl());
                        videoView.start();
                      //  MediaController mediaController = new MediaController(this);
                       // mediaController.setMediaPlayer(videoView);
                      //  videoView.setMediaController(mediaController);
                        videoView.requestFocus();
                        videoView.setVisibility(View.VISIBLE);
                    }

 */

                    } else {
                        for (int i = 0; i < dataChecks.size(); i++) {
                            if (viewPager.getCurrentItem() == dataChecks.get(i).getIndex()) {
                                if (!dataChecks.get(i).getStatus()) {
                                    Intent cameraIntent = new Intent(this.getApplicationContext(), CameraActivity.class);
                                    cameraIntent.putExtra("step", viewPager.getCurrentItem() + 1);
                                    cameraIntent.putExtra("manual_id", manual_key);
                                    cameraIntent.putExtra("manual_name", manual_name);
                                    Log.e("Camera", manual_name);
                                    startActivity(cameraIntent);
                                    break;
                                }
                            }
                        }
                    }
                }
                return true;

            case SWIPE_FORWARD:
/*
                for (int i =0; i<dataChecks.size(); i++){
                    Log.e("Viewsss", dataChecks.get(i).getIndex() +"and" + viewPager.getCurrentItem());
                    if (viewPager.getCurrentItem() == dataChecks.get(i).getIndex()){
                        if (dataChecks.get(i).getStatus()){
                            Log.e("Viewss", "can forward");
                        }else {
                            Toast.makeText(this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

 */

                if (viewPager.getPagingEnabled()){
                    return super.onGesture(gesture);
                }else {
                    Toast.makeText(this, "Please do camera confirmation", Toast.LENGTH_SHORT).show();
                    return true;
                }

            default:
                return super.onGesture(gesture);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            switch (id) {
                case R.id.bVideoCall2:
                    Intent intent = new Intent(this.getApplicationContext(), VideoPrefsActivity.class);
                    intent.putExtra("step", arrayList.get(viewPager.getCurrentItem()).getTitle());
                    intent.putExtra("manual", manual_key);
                    intent.putExtra("manual_name", manual_name);
                    intent.putExtra("user", "admin@gmail.com");
                    startActivity(intent);
                    break;
            }
        }
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
