package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataComponent;
import com.example.android.glass.glassdesign2.data.DataDQ;
import com.example.android.glass.glassdesign2.data.DataIssue;
import com.example.android.glass.glassdesign2.data.DataModule;
import com.example.android.glass.glassdesign2.data.DataModule2;
import com.example.android.glass.glassdesign2.data.DataSingle;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.QualityFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QualityActivity extends BaseActivity {

    private String MENU_KEY="menu_key";
    protected static final int REQUEST_CODE = 900;
    DatabaseReference reference, ref1;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    public static ArrayList<DataComponent> arrayList = new ArrayList<>();
    private TextView tvPage;
    private String moduleKey;
    private TextView tvTitle;
    private ArrayList<Integer> status = new ArrayList<Integer>();
    private String module_name;
    private ImageView imageView;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private TextView tvData;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        moduleKey = getIntent().getStringExtra("module_key");
        module_name = getIntent().getStringExtra("module_title");

        viewPager = findViewById(R.id.activity_quality_viewpager);
        indicator = findViewById(R.id.activity_quality_indicator);
        tvTitle = findViewById(R.id.activity_quality_tvTitle);
        tvPage = findViewById(R.id.activity_quality_tvPage);
        tvData = findViewById(R.id.activity_quality_tvData);

        tvTitle.setText(module_name);

        arrayList = new ArrayList<>();
        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);
        imageView = findViewById(R.id.activity_quality_imageview);
        if (moduleKey != null){
            reference = FirebaseDatabase.getInstance().getReference().child("DQ").child(moduleKey);

        }else {
            Log.e("QualityActivity", "Null module Key");
            Toast.makeText(this, "Null Parameter", Toast.LENGTH_SHORT).show();
            finish();
        }
        ref1 = FirebaseDatabase.getInstance().getReference().child("validator");
/*
        switch (SplashActivity.color_code){

            case 1:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_green));
                break;

            case 2:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_red));

                break;

            case 3:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_yellow));

                break;
            case 4:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_blue));

                break;
            case 5:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_yellow));

                break;
            case 6:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_purple));

                break;

        }


 */

        /*
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataDQ dataDQ = snapshot.getValue(DataDQ.class);
                module_name = dataDQ.getTitle().toString();
                tvTitle.setText("Module: " + dataDQ.getTitle().toString());
                for (int i=0; i< dataDQ.getArrayList().size(); i++){
                    DataSingle dataSingle = dataDQ.getArrayList().get(i);
                    arrayList.add(i, dataSingle);
                    status.add(i, 0);
                    fragments.add(QualityFragment.newInstance( i, dataSingle.getTitle(), dataSingle.getValue()));
                    screenSliderPagerAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(screenSliderPagerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tvPage.setText("" + (indicator.getSelectedTabPosition()+1) + " of "+ indicator.getTabCount());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

         */

        if (moduleKey != null && module_name != null){
            Log.e("Quality", "Ecerything is fine" + module_name + " and " + moduleKey);


            reference = FirebaseDatabase.getInstance().getReference().child("DQ").child(moduleKey);
            ref1 = FirebaseDatabase.getInstance().getReference().child("validator");

            firestore.collection("componentData")
                    .whereEqualTo("module_id", moduleKey)
                    .addSnapshotListener( MetadataChanges.INCLUDE,  new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                Log.e("QualityActivity: ", snapshot.getData().toString());

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("value") != null && snapshot.getData().get("module_id") != null) {
                                    arrayList.add(new DataComponent(
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("value").toString(),
                                            snapshot.getData().get("module_id").toString(),
                                            0, ""));
                                    fragments.add(QualityFragment.newInstance(snapshot.getData().get("title").toString(), snapshot.getData().get("value").toString()));
                                    screenSliderPagerAdapter.notifyDataSetChanged();
                                    viewPager.setAdapter(screenSliderPagerAdapter);
                                }else {
                                    Log.e("Quality", "Missing Parameters");
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

            indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tvPage.setText("" + (indicator.getSelectedTabPosition()+1) + " of "+ indicator.getTabCount());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }else {
            Log.e("Quality", "Ecerything is not fine" + module_name + " and " + moduleKey);
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(QualityActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_quality);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                return true;

            case SWIPE_DOWN:
                if (tvData.getVisibility() != View.VISIBLE) {

                    if (arrayList.size() > 0) {
                        Date time = new Date();
                        DateFormat.format("yyyy-MM-dd_hh:mm:ss", time);

                        DataModule2 module2 = new DataModule2(DQActivity.selectedDataModule.getTitle(), DQActivity.selectedDataModule.getDesc(), DQActivity.selectedDataModule.getMid(), time.toString());


                        firestore.collection("DQReport").add(module2)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.update("timestamp", Timestamp.now());
                                        for (int i = 0; i < arrayList.size(); i++) {
                                            arrayList.get(i).setModule_id(documentReference.getId().toString());
                                            firestore.collection("DQReportData")
                                                    .add(arrayList.get(i));
                                        }
                                    }
                                });

                        Toast.makeText(this, "Responses Stored", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();

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
            String selectedOption = "";
            switch (id) {
                case R.id.bAccept:
                    selectedOption = "Accepted";
                    status.set(indicator.getSelectedTabPosition(), 1);
                    updateStatus(indicator.getSelectedTabPosition(), 1);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_accept_round));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_normal), PorterDuff.Mode.SRC_IN);
                    break;
                case R.id.bReject:
                    selectedOption = "Rejected";
                    status.set(indicator.getSelectedTabPosition(), 2);
                    updateStatus(indicator.getSelectedTabPosition(), 2);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_reject_round));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_critical), PorterDuff.Mode.SRC_IN);
                    break;
                case R.id.bIssue:
                    selectedOption = "Issue Added";
                    String key = ref1.push().getKey();
                    DataIssue dataIssue = new DataIssue(key, module_name ,  arrayList.get(indicator.getSelectedTabPosition()).getTitle(), "", true);
                    ref1.child(key).setValue(dataIssue);
                    status.set(indicator.getSelectedTabPosition(), 3);
                    updateStatus(indicator.getSelectedTabPosition(), 3);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_help));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_camera), PorterDuff.Mode.SRC_IN);

                    break;
            }
            Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                    .show();
        }
    }


 */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);
            String selectedOption = "";
            switch (id) {
                case R.id.bAccept:
                    selectedOption = "Accepted";
                    arrayList.get(indicator.getSelectedTabPosition()).setResponse(1);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_round_check_circle_24));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_normal), PorterDuff.Mode.SRC_IN);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bReject:
                    selectedOption = "Rejected";
                    arrayList.get(indicator.getSelectedTabPosition()).setResponse(2);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_reject_round));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_critical), PorterDuff.Mode.SRC_IN);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bIssue:


                    //    String key = ref1.push().getKey();
                    DataIssue dataIssue = new DataIssue(module_name ,  arrayList.get(indicator.getSelectedTabPosition()).getTitle(), "", false);

                    firestore.collection("issueData")
                            .add(dataIssue)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(QualityActivity.this, "Issue Created, Check TestApp", Toast.LENGTH_SHORT).show();
                                    arrayList.get(indicator.getSelectedTabPosition()).setResponse(3);
                                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_baseline_help_outline_24));
                                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_camera), PorterDuff.Mode.SRC_IN);
                                    arrayList.get(indicator.getSelectedTabPosition()).setIssue_id(documentReference.getId());
                                    Intent intent = new Intent(QualityActivity.this, CommentActivity.class);
                                    intent.putExtra("reference", documentReference.getId().toString());
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QualityActivity.this, "Failed to create issue", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
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

    private void updateStatus(int id, int response){

    }

}