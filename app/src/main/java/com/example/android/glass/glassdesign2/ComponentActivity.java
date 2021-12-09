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
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataComponent2;
import com.example.android.glass.glassdesign2.data.DataComponentNew;
import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.data.DataIssue;
import com.example.android.glass.glassdesign2.data.DataLyo;
import com.example.android.glass.glassdesign2.data.DataModule;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.QualityFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComponentActivity extends BaseActivity {

    private TextView tvData, tvTime;
    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataComponentNew> arrayList = new ArrayList<>();

    private String key, module_id, module_title;
    private int type;
    private String MENU_KEY="menu_key";
    protected static final int REQUEST_CODE = 900;


    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);


        viewPager = findViewById(R.id.activity_component_viewpager);
        indicator = findViewById(R.id.activity_component_indicator);
        tvData = findViewById(R.id.activity_component_tvData);
        tvTime = findViewById(R.id.activity_component_tvTime);
        constraintLayout = findViewById(R.id.activity_component_layout);

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
        module_id = getIntent().getStringExtra("module_id");
        module_title = getIntent().getStringExtra("module_title");
        type = getIntent().getIntExtra("type", -1);
        Log.e("module Id", module_id + module_title);

        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(module_id) && !TextUtils.isEmpty(module_title) && type != -1){
            firestore.collection("DQNewReport").document(key).collection("content").document("config").collection("components")
                    .whereEqualTo("module_id", module_id)
                   // .orderBy("index")
                    .addSnapshotListener( new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                 if (snapshot.getData().get("title") != null && snapshot.getData().get("module_id") != null &&
                                            snapshot.getData().get("index") != null && snapshot.getData().get("value") != null  &&
                                            snapshot.getData().get("response") != null && snapshot.getData().get("issue_id") != null) {
                                        arrayList.add(new DataComponentNew(snapshot.getId(), snapshot.get("index", Integer.class) ,
                                                snapshot.getData().get("title").toString(), snapshot.getData().get("value").toString(),
                                                snapshot.getData().get("module_id").toString(), snapshot.get("response", Integer.class),
                                                snapshot.getData().get("issue_id").toString()));

                                    }else {
                                        Log.e("Machines", "Missing Parameters");
                                    }
                            }
                            if (arrayList.size() == 0){
                                viewPager.setVisibility(View.GONE);
                                indicator.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                            }else {

                                arrayList.sort(new Comparator<DataComponentNew>() {
                                    @Override
                                    public int compare(DataComponentNew comp1, DataComponentNew compo2) {
                                        return Integer.compare(comp1.getIndex(), compo2.getIndex());
                                        //  return Double.compare(comp1.getIndex(), compo2.getIndex());
                                    }
                                });
                                for (int i = 0; i<arrayList.size(); i++){
                                    fragments.add(QualityFragment.newInstance(arrayList.get(i).getTitle(), arrayList.get(i).getValue()));
                                    screenSliderPagerAdapter.notifyDataSetChanged();
                                }
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
                if (tvData.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(ComponentActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_quality);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                return  true;

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
            String selectedOption = "";
            switch (id) {
                case R.id.bAccept:
                    selectedOption = "Accepted";
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("config")
                            .collection("components").document(arrayList.get(indicator.getSelectedTabPosition()).getKey())
                            .update("response", 1);
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("config")
                            .collection("components").document(arrayList.get(indicator.getSelectedTabPosition()).getKey())
                            .update("issue_id", "");
                    arrayList.get(indicator.getSelectedTabPosition()).setResponse(1);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_round_check_circle_24));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_normal), PorterDuff.Mode.SRC_IN);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bReject:
                    selectedOption = "Rejected";
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("config")
                            .collection("components").document(arrayList.get(indicator.getSelectedTabPosition()).getKey())
                            .update("response", 2);
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("config")
                            .collection("components").document(arrayList.get(indicator.getSelectedTabPosition()).getKey())
                            .update("issue_id", "");
                    arrayList.get(indicator.getSelectedTabPosition()).setResponse(2);
                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_reject_round));
                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_critical), PorterDuff.Mode.SRC_IN);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bIssue:

                    //    String key = ref1.push().getKey();
                    DataIssue dataIssue = new DataIssue(module_title ,  arrayList.get(indicator.getSelectedTabPosition()).getTitle(), "", false);

                    firestore.collection("issueData")
                            .add(dataIssue)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(ComponentActivity.this, "Issue Created, Check TestApp", Toast.LENGTH_SHORT).show();
                                    arrayList.get(indicator.getSelectedTabPosition()).setResponse(3);
                                    indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_baseline_help_outline_24));
                                    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_camera), PorterDuff.Mode.SRC_IN);
                                    arrayList.get(indicator.getSelectedTabPosition()).setIssue_id(documentReference.getId());
                                    firestore.collection("DQNewReport").document(key)
                                            .collection("content").document("config")
                                            .collection("components").document(arrayList.get(indicator.getSelectedTabPosition()).getKey())
                                            .update("response", 3);
                                    firestore.collection("DQNewReport").document(key)
                                            .collection("content").document("config")
                                            .collection("components").document(arrayList.get(indicator.getSelectedTabPosition()).getKey())
                                            .update("issue_id", documentReference.getId());
                                    Intent intent = new Intent(ComponentActivity.this, CommentActivity.class);
                                    intent.putExtra("reference", documentReference.getId().toString());
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ComponentActivity.this, "Failed to create issue", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        constraintLayout.setBackgroundColor(SplashActivity.back_color);
    }
}