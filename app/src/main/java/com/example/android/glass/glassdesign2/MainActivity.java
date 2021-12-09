/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.glass.glassdesign2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.android.glass.glassdesign2.data.DataHome;
import com.example.android.glass.glassdesign2.adapter.HomeCardAdapter;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.android.glass.glassdesign2.transformer.HomeTransformer;
import com.example.glass.ui.GlassGestureDetector.Gesture;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Main activity of the application. It provides viewPager to move between fragments.
 */
public class MainActivity extends BaseActivity {

    private String MENU_KEY="menu_key";
    protected static final int REQUEST_CODE = 200;
    private static final int DQ_REQUEST_CODE = 201;
    private static final int REQUEST_VOICE_CODE = 999;
    private int menu;

    private HomeCardAdapter homeCardAdapter;
    private HomeTransformer homeTransformer;

    private ViewPager viewPager;
    private TabLayout indicator;
    private ArrayList<DataHome> arrayList = new ArrayList<>();
    private TextView tvTime, tvDate, tvBattery, tvuser, tvMachine;
    private ConstraintLayout layout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.activity_main_tvTime);
        tvBattery = findViewById(R.id.activity_main_tvBattery);
        tvDate = findViewById(R.id.activity_main_tvDate);
        tvMachine = findViewById(R.id.activity_main_tvMachineTitle);
        tvuser = findViewById(R.id.activity_main_tvUser);
        layout = findViewById(R.id.activity_main_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        menu = getIntent().getIntExtra(MENU_KEY, 0);
        Log.e("Mainactivity: ", String.valueOf(menu));

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
      //  tvDate.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()));
        tvDate.setText(now.toString());
        tvDate.setText(new SimpleDateFormat("MMM dd, EEEE", Locale.ENGLISH).format(new Date()));
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        tvBattery.setText(batLevel + " %");

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        viewPager = findViewById(R.id.activity_main_viewpager);
      //  indicator = findViewById(R.id.activity_contact_indicator);
        arrayList = new ArrayList<>();
        homeCardAdapter = new HomeCardAdapter(this);

        if (homeCardAdapter.getCount() == 0){
            String role = getIntent().getStringExtra("role");

            prepareData(role);
        }

      /*  if (homeCardAdapter.getCount() == 0){
            homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
            homeCardAdapter.addCardItem(new DataHome("Jobs", R.drawable.baseline_work_24));
            homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
            homeCardAdapter.addCardItem(new DataHome("Design Q", R.drawable.ic_baseline_class_24));
            homeCardAdapter.addCardItem(new DataHome("Scan Manual QR", R.drawable.baseline_qr_code_24));
            homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
            homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
            homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
            homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
            homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
            homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
        }


       */

     //   indicator.setupWithViewPager(viewPager, true);

    }

    private void prepareData(String role){

     //   homeCardAdapter.empty();
     //   homeCardAdapter.notifyDataSetChanged();

        switch (role){

            case "Admin":
                homeCardAdapter.addCardItem(new DataHome("Report", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Skill Test", R.drawable.ic_baseline_import_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Jobs", R.drawable.ic_baseline_work_24));
                homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
           //     homeCardAdapter.addCardItem(new DataHome("Design Q", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("New DQ", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Instrumentation", R.drawable.ic_baseline_analytics_24));
                homeCardAdapter.addCardItem(new DataHome("Change Machine", R.drawable.modules_color));
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
             //   homeCardAdapter.addCardItem(new DataHome("Voice Command", R.drawable.ic_baseline_audiotrack_24));
                homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));

                break;

            case "Trainee":
                homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Skill Test", R.drawable.ic_baseline_import_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
                homeCardAdapter.addCardItem(new DataHome("Change Machine", R.drawable.modules_color));
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
             //   homeCardAdapter.addCardItem(new DataHome("Voice Command", R.drawable.ic_baseline_audiotrack_24));
                homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));

                break;

            case "Validator":
                homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Jobs", R.drawable.ic_baseline_work_24));
                homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
              //  homeCardAdapter.addCardItem(new DataHome("Design Q", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("New DQ", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Change Machine", R.drawable.modules_color));
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
            //    homeCardAdapter.addCardItem(new DataHome("Voice Command", R.drawable.ic_baseline_audiotrack_24));
                homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));

                break;

            case "Operator":
                homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Jobs", R.drawable.ic_baseline_work_24));
                homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
                homeCardAdapter.addCardItem(new DataHome("Change Machine", R.drawable.modules_color));
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
             //   homeCardAdapter.addCardItem(new DataHome("Voice Command", R.drawable.ic_baseline_audiotrack_24));
                homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));

                break;

            case "Maintenance":
                homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
                homeCardAdapter.addCardItem(new DataHome("Change Machine", R.drawable.modules_color));
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
              //  homeCardAdapter.addCardItem(new DataHome("Voice Command", R.drawable.ic_baseline_audiotrack_24));
                homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));

                break;

            case "Supervisor":
                homeCardAdapter.addCardItem(new DataHome("Manual", R.drawable.ic_baseline_class_24));
                homeCardAdapter.addCardItem(new DataHome("Jobs", R.drawable.ic_baseline_work_24));
                homeCardAdapter.addCardItem(new DataHome("Monitor", R.drawable.ic_baseline_tv_24));
                homeCardAdapter.addCardItem(new DataHome("Change Machine", R.drawable.modules_color));
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
             //   homeCardAdapter.addCardItem(new DataHome("Voice Command", R.drawable.ic_baseline_audiotrack_24));
                homeCardAdapter.addCardItem(new DataHome("Help",  R.drawable.ic_baseline_help_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Help Video", R.drawable.ic_baseline_play_circle_outline_24));
                homeCardAdapter.addCardItem(new DataHome("Video Call", R.drawable.ic_baseline_videocam_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));
                break;

            default:
                // do nothing and show basic things
                homeCardAdapter.addCardItem(new DataHome("Account", R.drawable.ic_baseline_account_circle_24));
                homeCardAdapter.addCardItem(new DataHome("Contacts", R.drawable.ic_baseline_contacts_24));
                homeCardAdapter.addCardItem(new DataHome("Background Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Front Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Reset Theme",  R.drawable.ic_baseline_palette_24));
                homeCardAdapter.addCardItem(new DataHome("Log Out", R.drawable.baseline_logout_24));
                break;
        }

        homeCardAdapter.notifyDataSetChanged();
        homeTransformer = new HomeTransformer(this, viewPager, homeCardAdapter);
        homeTransformer.enableScaling(true);

        viewPager.setAdapter(homeCardAdapter);
        viewPager.setPageTransformer(false, homeTransformer);
    }

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, this.REQUEST_VOICE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VOICE_CODE &&  resultCode == RESULT_OK && data != null){
            List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                for (int i=0; i<results.size() ; i++){
                    Log.d("VoiceActivity", "result: " + String.valueOf(results.get(i).toString()));
                    CharSequence var5 = (CharSequence)results.get(i).toString();
                    if (var5.length() != 0) {
                        Log.e("Result", ((String)results.get(i)));

                        switch (results.get(i).toString().toLowerCase()){
                            case "monitor":
                            case "monika":
                            case "monster":
                            case "wonder":
                            case "mulder":
                            case "hunter":
                                startActivity(new Intent(MainActivity.this, MonitorActivity.class));
                                break;

                            case "manual":
                            case "manuals":
                            case "manuel":
                            case "amen":
                            case "emmanuel":
                            case "anyone":
                                startActivity(new Intent(MainActivity.this, LyoManualActivity.class));
                                break;

                            case "design":
                            case "designs":
                            case "designed":
                            case "dijon":
                            case "zyan":
                            case "done":
                                startActivity(new Intent(MainActivity.this, DQActivity.class));
                                break;

                            case "machine":
                            case "motion":
                            case "motions":
                            case "machines":
                            case "mushroom":
                            case "watching":
                                startActivity(new Intent(MainActivity.this, MachinesActivity.class));
                                break;

                            case "job":
                            case "jab":
                            case "jobs":
                            case "yes":
                            case "chap":
                            case "japs":
                                startActivity(new Intent( MainActivity.this, JobActivity.class));
                                break;

                            case "return":
                            case "returned":
                            case "returns":
                            case "dunst":
                            case "written":
                            case "freedom":
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

        }else if (requestCode == DQ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            switch (id) {
                case R.id.bDQSelect1:
                    Intent intent = new Intent(MainActivity.this, DQReportActivity.class);
                    startActivity(intent);
                    break;
                case R.id.bDQSelect2:
                    Intent intent1 = new Intent(MainActivity.this, DQNewActivity.class);
                    startActivity(intent1);
                    break;
            }
        }else  {
            Log.d("VoiceActivity", "Result not OK");
        }
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        switch (gesture) {
            case TAP:
                if (!hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                }else{
             //       Intent intent = new Intent(MainActivity.this, MenuActivity.class);
              //      intent.putExtra(MENU_KEY, menu);
               //     startActivityForResult(intent, REQUEST_CODE );
                    switch (homeCardAdapter.getDataHome(viewPager.getCurrentItem()).getTitle()){
                        case "Report":
                            startActivity(new Intent(MainActivity.this, Report.class));
                            //    startActivity(new Intent(MainActivity.this, VideoPlayground.class));
                            break;

                        case "Manual":
                            startActivity(new Intent(MainActivity.this, LyoManualActivity.class));
                        //    startActivity(new Intent(MainActivity.this, VideoPlayground.class));
                        break;

                        case "Skill Test":
                            startActivity(new Intent(MainActivity.this, TestActivity.class));
                            break;

                        case "Instrumentation":
                            startActivity(new Intent(MainActivity.this, InstrumentActivity.class));
                            break;

                        case "Jobs":
                            startActivity(new Intent(MainActivity.this, JobActivity.class));
                            break;

                        case "Monitor":
                            startActivity(new Intent(MainActivity.this, MonitorActivity.class));
                            break;

                        case "Design Q":
                            startActivity(new Intent(MainActivity.this, DQActivity.class));
                            break;

                        case "New DQ":
                          //  startActivity(new Intent(MainActivity.this, DQNewActivity.class));
                            Intent dqintent = new Intent(MainActivity.this, MenuActivity.class);
                            dqintent.putExtra(MENU_KEY, R.menu.menu_dq_selector);
                            startActivityForResult(dqintent, DQ_REQUEST_CODE);

                            break;

                        case "Change Machine":
                            startActivity(new Intent(MainActivity.this, MachinesActivity.class));

                            break;

                        case "Scan Manual QR":
                            startActivity(new Intent(MainActivity.this, ScannerActivity.class));

                            break;

                        case "Help":
                            startActivity(new Intent(MainActivity.this, TutorialActivity.class));

                            break;

                        case "Help Video":
                            startActivity(new Intent(MainActivity.this, VideoActivity.class));

                            break;

                        case "Account":

                            startActivity(new Intent(MainActivity.this, AccountActivity.class));
                            break;

                        case "Video Call":
                            Intent intents = new Intent(this.getApplicationContext(), VideoPrefsActivity.class);
                            intents.putExtra("step", "General");
                            intents.putExtra("manual", "General");
                            intents.putExtra("manual_name", "General");
                            intents.putExtra("user", "admin@gmail.com");
                            startActivity(intents);

                       //     Intent intentx = new Intent(this.getApplicationContext(), VideoCallActivity.class);
                        //    intentx.putExtra("step", "General");
                         //   intentx.putExtra("manual", "General");
                          //  startActivity(intentx);


                            break;

                        case "Contacts":

                            Intent contactIntent = new Intent(this.getApplicationContext(), ContactActivity.class);
                            startActivity(contactIntent);

                            break;

                        case "Video Call 2":
                            Intent intentx = new Intent(this.getApplicationContext(), VideoCallActivity.class);
                            intentx.putExtra("step", "General");
                            intentx.putExtra("manual", "General");
                            startActivity(intentx);

                            break;

                        case "Background Theme":
                            Intent intent1 = new Intent(MainActivity.this, SelectActivity.class);
                            intent1.putExtra("select_code", 1);
                            startActivity(intent1);
                            break;
                        case "Front Theme":
                            Intent intent2 = new Intent(MainActivity.this, SelectActivity.class);
                            intent2.putExtra("select_code", 2);
                            startActivity(intent2);
                            break;
                        case "Reset Theme":
                            Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                            intent.putExtra("select_code", 0);
                            startActivity(intent);

                            break;

                        case "Voice Command":
                            Intent iCommand = new Intent(MainActivity.this, CommandActivity.class);
                            startActivity(iCommand);
                            break;

                        case "Log Out":

                            firebaseAuth.signOut();
                            SplashActivity.login_status = false;
                            SplashActivity.machine_id = "";
                            SplashActivity.role = "";

                            SharedPreferences user_prefs= getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor  = user_prefs.edit();
                            editor.putBoolean("login_status", false);
                            editor.putString("machine_id", "");
                            editor.putString("role", "");
                            editor.commit();
                            editor.apply();

                            startActivity(new Intent(MainActivity.this, SplashActivity.class));
                            finish();

                            break;
                    }
                }
                return true;

            case SWIPE_UP:

                requestVoiceRecognition();

                return true;

            case SWIPE_DOWN:
                SharedPreferences user_prefs = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = user_prefs.edit();
                editor.putString("machine_id_temp", "");
                editor.commit();
                editor.apply();
                SplashActivity.machine_id_temp = "";

                return super.onGesture(gesture);
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
            String selectedOption = "";
            switch (id) {
                case R.id.bManual:
                    selectedOption = getString(R.string.lyo_manual);
                    startActivity(new Intent(this.getApplicationContext(), LyoManualActivity.class));
                    break;
                case R.id.bMonitor:
                    selectedOption = getString(R.string.monitor);
                    startActivity(new Intent(this.getApplicationContext(), MonitorActivity.class));
                    break;
                case R.id.bVideoCall:
                    selectedOption = getString(R.string.video_call);
                    Intent intent = new Intent(this.getApplicationContext(), MultiVideoCallActivity.class);
                    intent.putExtra("step", "General");
                    intent.putExtra("manual", "General");
                    intent.putExtra("manual_name", "General");
                    startActivity(intent);
                    break;

                case R.id.bHelp:
                    selectedOption = getString(R.string.help);
                    startActivity(new Intent(this.getApplicationContext(), TutorialActivity.class));
                    break;
                case R.id.bPlayVideo:
                    selectedOption = getString(R.string.play_video);
                    startActivity(new Intent(this.getApplicationContext(), VideoActivity.class));
                    break;
                case R.id.bScanner:
                    selectedOption = getString(R.string.qr_scanner);
                    startActivity(new Intent(this.getApplicationContext(), ScannerActivity.class));
                    break;

                case R.id.bJob:
                    selectedOption = "Jobs Module";
                    startActivity(new Intent(this.getApplicationContext(), JobActivity.class));
                    break;

                case R.id.bIQ:
                    selectedOption = "Quality Module";
                    startActivity(new Intent(this.getApplicationContext(), DQActivity.class));
                    break;

                case R.id.bDQ:
                    selectedOption = "Quality Module";
                    startActivity(new Intent(this.getApplicationContext(), DQActivity.class));
                    break;

                case R.id.bOQ:
                    selectedOption = "Quality Module";
                    startActivity(new Intent(this.getApplicationContext(), DQActivity.class));
                    break;
          //      case R.id.bTracker:
           //         selectedOption = "Live Tracking";
            //        startActivity(new Intent(this.getApplicationContext(), SimpleImageTrackingActivity.class));
             //       break;

                case R.id.bTheme:
                    selectedOption = "Theme";
                    startActivity(new Intent(this.getApplicationContext(), ThemeActivity.class));
                    break;
            }
            Toast.makeText(this.getApplicationContext(),  "Opening "+selectedOption, Toast.LENGTH_SHORT)
                    .show();
        }
    }


 */


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        layout.setBackgroundColor(SplashActivity.back_color);

        if (firebaseAuth.getCurrentUser() != null) {
            firestore.collection("users")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail().toString())
                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("firstName") != null) {
                                    tvuser.setText("Welcome, " + snapshot.getData().get("firstName"));
                                }

                            }
                        }
                    });

        }
        if (SplashActivity.machine_id_temp != null && !SplashActivity.machine_id_temp.equals("")){
            documentReference = firestore.collection("machineData").document(SplashActivity.machine_id_temp);

            documentReference.addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {

                    if (snapshot != null && snapshot.exists() && snapshot.get("title") != null) {
                        tvMachine.setText("Machine: " + snapshot.get("title").toString());
                    } else {
                        Log.d("MainActivity", "Current machine data: null");
                    }
                }
            });
        }else {
            if (SplashActivity.machine_id != null){
                documentReference = firestore.collection("machineData").document(SplashActivity.machine_id);

                documentReference.addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        if (snapshot != null && snapshot.exists() && snapshot.get("title") != null) {
                            tvMachine.setText("Machine: " + snapshot.get("title").toString());
                        } else {
                            Log.d("MainActivity", "Current machine data: null");
                        }
                    }
                });
            }
        }

    }
}
