package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.adapter.MonitorPagerAdapter;
import com.example.android.glass.glassdesign2.data.DataLoad;
import com.example.android.glass.glassdesign2.data.DataRecipe;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.BatchFragment;
import com.example.android.glass.glassdesign2.transformer.DQTransformer;
import com.example.android.glass.glassdesign2.transformer.MonitorTransformer;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonitorActivity extends BaseActivity {

    private DatabaseReference reference;
    private ArrayList<DataLoad> arrayList = new ArrayList<>();
    public static ArrayList<DataRecipe> recipeArrayList = new ArrayList<>();
    private List<BaseFragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout indicator;
    private TextView tvPage;
    private int index =0;
    private ProgressBar progressBar;
    private ConstraintLayout layout;

    private MonitorTransformer monitorTransformer;
    private MonitorPagerAdapter monitorPagerAdapter;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    private TextView tvTime;
    private TextView tvData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        viewPager = findViewById(R.id.activity_monitor_viewpager);
        indicator = findViewById(R.id.activity_monitor_indicator);
        tvPage = findViewById(R.id.activity_monitor_tvPage);
        tvTime = findViewById(R.id.activity_monitor_tvTime);
        layout = findViewById(R.id.activity_monitor_layout);
        tvData = findViewById(R.id.activity_monitor_tvData);

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

       // indicator.setupWithViewPager(viewPager, true);
      //  viewPager.setAdapter(screenSliderPagerAdapter);
        progressBar = findViewById(R.id.activity_monitor_progressbar);

        index =0;

        monitorPagerAdapter = new MonitorPagerAdapter(this);


        reference = FirebaseDatabase.getInstance().getReference().child("recipes");
        if (firebaseAuth.getCurrentUser() != null && monitorPagerAdapter.getCount() ==0){

            if (SplashActivity.machine_id_temp != null && !SplashActivity.machine_id_temp.equals("")){
                firestore.collection("recipes")
                        .whereEqualTo("mid", SplashActivity.machine_id_temp)
                        .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                assert value != null;
                                arrayList.clear();
                                fragments.clear();
                                viewPager.setSaveFromParentEnabled(false);
                                monitorPagerAdapter = new MonitorPagerAdapter(MonitorActivity.this);

                                if(progressBar.getVisibility()==View.VISIBLE){
                                    progressBar.setVisibility(View.GONE);
                                }

                                for (QueryDocumentSnapshot snapshot : value) {
                                    if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {
                                        ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                                        dataRecipes = getRecipeData(snapshot.getId());
                                        arrayList.add(new DataLoad(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("mid").toString(),
                                                dataRecipes));
                                        monitorPagerAdapter.addCardItem(new DataLoad(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("mid").toString(),
                                                dataRecipes));
                                        monitorTransformer = new MonitorTransformer(MonitorActivity.this, viewPager, monitorPagerAdapter);
                                        monitorTransformer.enableScaling(true);
                                        viewPager.setAdapter(monitorPagerAdapter);
                                        viewPager.setPageTransformer(false, monitorTransformer);
                                        viewPager.setOffscreenPageLimit(3);
                                        indicator.setupWithViewPager(viewPager, true);
                                        monitorPagerAdapter.notifyDataSetChanged();
                                        Log.e("Sample", snapshot.getData().get("title").toString());

                                    }else {
                                        Log.e("Monitor", "Missing Parameters");
                                    }
                                }
                                if (arrayList.size() == 0){
                                    viewPager.setVisibility(View.GONE);
                               //     indicator.setVisibility(View.GONE);
                                    tvData.setVisibility(View.VISIBLE);
                                }else {
                                    viewPager.setVisibility(View.VISIBLE);
                                //    indicator.setVisibility(View.VISIBLE);
                                    tvData.setVisibility(View.GONE);
                                }

                            }
                        });
            }else {
                firestore.collection("recipes")
                        .whereEqualTo("mid", SplashActivity.machine_id)
                        .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                assert value != null;
                                arrayList.clear();
                                fragments.clear();
                                viewPager.setSaveFromParentEnabled(false);
                                monitorPagerAdapter = new MonitorPagerAdapter(MonitorActivity.this);
                                if(progressBar.getVisibility()==View.VISIBLE){
                                    progressBar.setVisibility(View.GONE);
                                }

                                for (QueryDocumentSnapshot snapshot : value) {
                                    if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {

                                        ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                                        dataRecipes = getRecipeData(snapshot.getId());
                                        arrayList.add(new DataLoad(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("mid").toString(),
                                                dataRecipes));
                                        monitorPagerAdapter.addCardItem(new DataLoad(snapshot.getId(),
                                                snapshot.getData().get("title").toString(),
                                                snapshot.getData().get("mid").toString(),
                                                dataRecipes));
                                        monitorTransformer = new MonitorTransformer(MonitorActivity.this, viewPager, monitorPagerAdapter);
                                        monitorTransformer.enableScaling(true);
                                        viewPager.setAdapter(monitorPagerAdapter);
                                        viewPager.setPageTransformer(false, monitorTransformer);
                                        viewPager.setOffscreenPageLimit(3);
                                        indicator.setupWithViewPager(viewPager, true);
                                        monitorPagerAdapter.notifyDataSetChanged();
                                        Log.e("Sample", snapshot.getData().get("title").toString());

                                    } else {
                                        Log.e("Monitor", "Missing Parameters");
                                    }
                                }
                                if (arrayList.size() == 0){
                                    viewPager.setVisibility(View.GONE);
                                 //   indicator.setVisibility(View.GONE);
                                    tvData.setVisibility(View.VISIBLE);
                                }else {
                                    viewPager.setVisibility(View.VISIBLE);
                                 //   indicator.setVisibility(View.VISIBLE);
                                    tvData.setVisibility(View.GONE);
                                }

                            }
                        });
            }


        }

/*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(progressBar.getVisibility()==View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
                ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                for (int i=0; i< snapshot.child("arrayList").getChildrenCount(); i++){
                    dataRecipes.add(i, snapshot.child("arrayList").child(String.valueOf(i)).getValue(DataRecipe.class));
                }
            //    arrayList.add(new DataLoad(snapshot.child("key").getValue().toString(),
               //         snapshot.child("title").getValue().toString(), dataRecipes));
           //    fragments.add(BatchFragment.newInstance(index,
             //           snapshot.child("key").getValue().toString(),
               //         snapshot.child("title").getValue().toString()));


            //    screenSliderPagerAdapter.notifyDataSetChanged();
          //      viewPager.setAdapter(screenSliderPagerAdapter);
                DataLoad dataLoad = snapshot.getValue(DataLoad.class);
                arrayList.add(dataLoad);
                monitorPagerAdapter.addCardItem(dataLoad);
                monitorTransformer = new MonitorTransformer(MonitorActivity.this, viewPager, monitorPagerAdapter);
                monitorTransformer.enableScaling(true);
                viewPager.setAdapter(monitorPagerAdapter);
                viewPager.setPageTransformer(false, monitorTransformer);
                viewPager.setOffscreenPageLimit(3);
                indicator.setupWithViewPager(viewPager, true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


 */
    }

    @Override
    protected void onResume() {
        super.onResume();

        layout.setBackgroundColor(SplashActivity.back_color);

    }

    private ArrayList<DataRecipe> getRecipeData(String recipe_id){

        Log.e("Load", recipe_id);

        ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
        firestore.collection("recipeeData")
                .whereEqualTo("rid", recipe_id)
                .addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            dataRecipes.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("index") != null && snapshot.getData().get("step") != null &&
                                        snapshot.getData().get("rid") != null && snapshot.getData().get("temp1") != null &&
                                        snapshot.getData().get("time1") != null && snapshot.getData().get("time2") != null &&
                                        snapshot.getData().get("pressure") != null) {
                                    dataRecipes.add(new DataRecipe(Integer.parseInt(snapshot.getData().get("index").toString()),
                                            snapshot.getData().get("step").toString(),
                                            snapshot.getData().get("rid").toString(),
                                            Integer.parseInt(snapshot.getData().get("temp1").toString()),
                                            Integer.parseInt(snapshot.getData().get("time1").toString()),
                                            Integer.parseInt(snapshot.getData().get("time2").toString()),
                                            Integer.parseInt(snapshot.getData().get("pressure").toString())));
                                    Log.e("Sample", snapshot.getData().get("index").toString() + snapshot.getData().get("step").toString());
                                }else {
                                    Log.e("Monitor", "Missing Parameters");
                                }
                            }
                            dataRecipes.sort(new Comparator<DataRecipe>() {
                                @Override
                                public int compare(DataRecipe dataStep, DataRecipe dataStep1) {
                                    return Integer.compare(dataStep.getIndex(), dataStep1.getIndex());
                                }
                            });

                        }
                    }
                });


        return dataRecipes;
    }


    public class LoadAdapter extends RecyclerView.Adapter<LoadAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataLoad> list=new ArrayList<>();

        public LoadAdapter(Context context, ArrayList<DataLoad> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public LoadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recipe, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull LoadAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle());
            switch (SplashActivity.color_code){

                case 1:
                    holder.tvTitle.setTextColor(getResources().getColor(R.color.design_green));
                    break;
                case 2:
                    holder.tvTitle.setTextColor(getResources().getColor(R.color.design_red));
                    break;
                case 3:
                    holder.tvTitle.setTextColor(getResources().getColor(R.color.design_yellow));
                    break;
                case 4:
                    holder.tvTitle.setTextColor(getResources().getColor(R.color.design_blue));
                    break;
                case 5:
                    holder.tvTitle.setTextColor(getResources().getColor(R.color.design_orange));
                    break;
                case 6:
                    holder.tvTitle.setTextColor(getResources().getColor(R.color.design_purple));
                    break;

            }


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_recipe_tvTitle);
            }
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
/*
        switch (gesture){
            case TAP:
                recipeArrayList = new ArrayList<>();
                for (int i =0; i<arrayList.get(0).getArrayList().size(); i++){
                    recipeArrayList.add(i, arrayList.get(0).getArrayList().get(i));
                }
                Intent intent = new Intent(MonitorActivity.this, ChartActivity.class);
                startActivity(intent);
                break;
        }

        return super.onGesture(gesture);


 */
        switch (gesture){
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE) {
                    recipeArrayList = new ArrayList<>();
                    //  Toast.makeText(this, "tab: "+ indicator.getSelectedTabPosition(), Toast.LENGTH_SHORT).show();
                    recipeArrayList = arrayList.get(indicator.getSelectedTabPosition()).getArrayList();
                    Intent intent = new Intent(MonitorActivity.this, ChartActivity.class);
                    startActivity(intent);
                }
                break;
        }

        return super.onGesture(gesture);
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