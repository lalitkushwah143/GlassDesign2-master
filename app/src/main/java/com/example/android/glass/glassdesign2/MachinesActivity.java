package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataMachines;
import com.example.android.glass.glassdesign2.data.DataManuals;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.MachineFragment;
import com.example.android.glass.glassdesign2.fragments.MainLayoutFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
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

public class MachinesActivity extends BaseActivity {

    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    private ConstraintLayout layout;
    private static final int REQUEST_VOICE_CODE = 999;

    private static final int REQUEST_CODE = 349;
    private String MENU_KEY="menu_key";

    private FirebaseFirestore firestore;
    private ArrayList<DataMachines> arrayList = new ArrayList<>();
    private TextView tvTime;
    private TextView tvData;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machines);

        firestore = FirebaseFirestore.getInstance();

        viewPager = findViewById(R.id.activity_machines_viewpager);
        indicator = findViewById(R.id.activity_machines_indicator);
        layout = findViewById(R.id.activity_machines_layout);
        tvTime = findViewById(R.id.activity_machines_tvTime);
        tvData = findViewById(R.id.activity_machines_tvData);

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

        screenSliderPagerAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.setBackgroundColor(SplashActivity.back_color);

        firestore.collection("machineData")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        fragments.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null &&
                                    snapshot.getData().get("location") != null && snapshot.getData().get("createdBy") != null) {

                                arrayList.add(new DataMachines(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("location").toString(),
                                        snapshot.getData().get("createdBy").toString()));
                                fragments.add(MachineFragment.newInstance(snapshot.getId(), snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("desc").toString(), snapshot.getData().get("location").toString()));
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
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                if (tvData.getVisibility() !=  View.VISIBLE) {
                    Intent intent = new Intent(MachinesActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_machines);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                return true;

            case SWIPE_UP:
                if (tvData.getVisibility() != View.VISIBLE) {
                    requestVoiceRecognition();
                }
                return true;
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
                case R.id.bSetDefault:
                    // change shared prefs
                    
                    if (!arrayList.get(indicator.getSelectedTabPosition()).getKey().equals("")) {
                        SharedPreferences user_prefs = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = user_prefs.edit();
                        editor.putString("machine_id", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                        editor.commit();
                        editor.apply();

                        SplashActivity.machine_id = arrayList.get(indicator.getSelectedTabPosition()).getKey();
                        Toast.makeText(this, "Changed default machine", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.bSetCurrent:

                    if (!arrayList.get(indicator.getSelectedTabPosition()).getKey().equals("")) {
                        SharedPreferences user_prefs = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = user_prefs.edit();
                        editor.putString("machine_id_temp", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                        editor.commit();
                        editor.apply();

                        SplashActivity.machine_id_temp = arrayList.get(indicator.getSelectedTabPosition()).getKey();
                        Toast.makeText(this, "Changed current machine", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }

        }else if (requestCode == REQUEST_VOICE_CODE && resultCode == RESULT_OK && data != null){

            List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                for (int i=0; i<results.size() ; i++){
                    Log.d("VoiceActivity", "result: " + String.valueOf(results.get(i).toString()));
                    CharSequence var5 = (CharSequence)results.get(i).toString();
                    if (var5.length() != 0) {
                        Log.e("Result", ((String)results.get(i)));

                        switch (results.get(i).toString().toLowerCase()){
                            case "set default":
                            case "default":
                            case "set as default":
                            case "say default":
                            case "it is default":
                            case "say it default":
                                if (!arrayList.get(indicator.getSelectedTabPosition()).getKey().equals("")) {
                                    SharedPreferences user_prefs = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = user_prefs.edit();
                                    editor.putString("machine_id", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                                    editor.commit();
                                    editor.apply();

                                    SplashActivity.machine_id = arrayList.get(indicator.getSelectedTabPosition()).getKey();
                                    Toast.makeText(this, "Changed default machine", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case "set current":
                            case "sit current":
                            case "say it current":
                            case "it is current":
                            case "say current":
                            case "current":

                                if (!arrayList.get(indicator.getSelectedTabPosition()).getKey().equals("")) {
                                    SharedPreferences user_prefs = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = user_prefs.edit();
                                    editor.putString("machine_id_temp", arrayList.get(indicator.getSelectedTabPosition()).getKey());
                                    editor.commit();
                                    editor.apply();

                                    SplashActivity.machine_id_temp = arrayList.get(indicator.getSelectedTabPosition()).getKey();
                                    Toast.makeText(this, "Changed current machine", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case "return":
                                finish();
                                break;

                            default:
                                // Do nothing
                                break;

                        }
                    }
                }
            }else {
                Log.e("Result", "Size 0");
            }

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

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, this.REQUEST_VOICE_CODE);
    }


}