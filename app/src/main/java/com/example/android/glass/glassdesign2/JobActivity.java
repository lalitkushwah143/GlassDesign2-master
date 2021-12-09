package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataJob;
import com.example.android.glass.glassdesign2.data.DataLoad;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.JobFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
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

public class JobActivity extends BaseActivity {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private TextView textView;
    private ArrayList<DataJob> arrayList = new ArrayList<>();
    private ArrayList<DataLoad> recipeArrayList = new ArrayList<>();
    private static int REQUEST_CODE = 789;
    private String MENU_KEY="menu_key";
    private ImageView icon;
    private TextView tvTime;
    private TextView tvData;
    private int currentMenuItemIndex;
    private JobAdapter adapter;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.activity_job_rcView);
        textView = findViewById(R.id.activity_job_tvCount);
        icon = findViewById(R.id.activity_job_imageview);
        tvTime = findViewById(R.id.activity_job_tvTime);
        tvData = findViewById(R.id.activity_job_tvData);

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);
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
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_orange));
                break;
            case 6:
                imageView.setBackground(getResources().getDrawable(R.drawable.back_circle_purple));
                break;

        }


 */

        adapter = new JobAdapter(this, arrayList);
        SnapHelper snapHelper = new PagerSnapHelper();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                final View foundView = snapHelper.findSnapView(layoutManager);
                if (foundView == null) {
                    textView.setText(" ");
                    return;
                }
                currentMenuItemIndex = layoutManager.getPosition(foundView);
                textView.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
            }
        });

        if (SplashActivity.machine_id_temp != null && !SplashActivity.machine_id_temp.equals("")){
            firestore.collection("jobData")
                    .whereEqualTo("mid", SplashActivity.machine_id_temp)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("title") != null && snapshot.getData().get("rid") != null
                                        && snapshot.getData().get("mid") != null && snapshot.getData().get("desc") != null
                                        &&  snapshot.getData().get("status") != null) {
                                    arrayList.add(new DataJob(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("rid").toString(),
                                            snapshot.getData().get("mid").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getTimestamp("date"),
                                            (Boolean) snapshot.getData().get("status")));
                                    adapter.notifyDataSetChanged();
                                    Log.e("Sample", snapshot.getData().get("title").toString());
                                }else {
                                    Log.e("JObs", "Missing Parameters");
                                }
                            }

                            firestore.collection("recipes")
                                    .whereEqualTo("mid", SplashActivity.machine_id)
                                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            assert value != null;
                                            recipeArrayList.clear();
                                            for (QueryDocumentSnapshot snapshot : value) {
                                                if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {
                                                    recipeArrayList.add(new DataLoad(snapshot.getId().toString(),
                                                            snapshot.getData().get("title").toString(),
                                                            snapshot.getData().get("mid").toString(),
                                                            null));
                                                    Log.e("recipe:", snapshot.getData().get("title").toString());
                                                }else {
                                                    Log.e("JObs", "Missing Parameters");
                                                }
                                            }
                                        }
                                    });

                            if (arrayList.size() == 0){
                                recyclerView.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                                textView.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                            }
                        }
                    });
        }else {
            firestore.collection("jobData")
                    .whereEqualTo("mid", SplashActivity.machine_id)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("title") != null && snapshot.getData().get("rid") != null && snapshot.getData().get("email") != null
                                        && snapshot.getData().get("mid") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("date") != null
                                        && snapshot.getData().get("status") != null) {
                                    arrayList.add(new DataJob(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("rid").toString(),
                                            snapshot.getData().get("mid").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("email").toString(),
                                            snapshot.getTimestamp("date"),
                                            (Boolean) snapshot.getData().get("status")));
                                    adapter.notifyDataSetChanged();
                                    Log.e("Sample", snapshot.getData().get("title").toString());
                                }else {
                                    Log.e("JObs", "Missing Parameters");

                                }
                            }

                            firestore.collection("recipes")
                                    .whereEqualTo("mid", SplashActivity.machine_id)
                                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            assert value != null;
                                            recipeArrayList.clear();
                                            for (QueryDocumentSnapshot snapshot : value) {
                                                if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {
                                                    recipeArrayList.add(new DataLoad(snapshot.getId().toString(),
                                                            snapshot.getData().get("title").toString(),
                                                            snapshot.getData().get("mid").toString(),
                                                            null));
                                                    Log.e("recipe:", snapshot.getData().get("title").toString());
                                                }else {
                                                    Log.e("JObs", "Missing Parameters");
                                                }
                                            }
                                        }
                                    });

                            arrayList.sort(new Comparator<DataJob>() {
                                @Override
                                public int compare(DataJob o1, DataJob o2) {
                                    return o2.getDate().compareTo(o1.getDate());
                                }
                            });
                            if (arrayList.size() == 0){
                                recyclerView.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                                textView.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                            }
                        }
                    });
        }
        /*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DataJob dataJob = snapshot.getValue(DataJob.class);
                arrayList.add(dataJob);
                Log.e("Sample: ", dataJob.getTitle().toString());
                fragments.add(JobFragment.newInstance(dataJob.getKey(), dataJob.getTitle(), dataJob.getRecipe_title(), dataJob.getDesc(), dataJob.getStatus()));
                screenSlidePagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(screenSlidePagerAdapter);
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
     /*   for (int i=0; i<arrayList.size(); i++){
            DataJob dataJob = arrayList.get(i);
            fragments.add(JobFragment.newInstance(dataJob.getKey(), dataJob.getTitle(), dataJob.getRecipe_title(), dataJob.getDesc(), dataJob.getStatus()));
            screenSlidePagerAdapter.notifyDataSetChanged();
            viewPager.setAdapter(screenSlidePagerAdapter);
        }

      */

    }

    public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataJob> list=new ArrayList<>();

        public JobAdapter(Context context, ArrayList<DataJob> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public JobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_job, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull JobAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText(list.get(position).getTitle());
            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvUser.setText(list.get(position).getEmail());
            holder.tvDate.setText(list.get(position).getDate().toDate().toString().substring(0, 10));
            if (list.get(position).getStatus()){
                holder.tvStatus.setText("Completed");
                holder.imageView.setColorFilter(getResources().getColor(R.color.color_normal));
            }else {
                holder.tvStatus.setText("Not Completed");
                holder.imageView.setColorFilter(getResources().getColor(R.color.color_critical));
            }
            holder.layout.setBackgroundColor(SplashActivity.back_color);

                String title = "";
                for (int j = 0; j<recipeArrayList.size(); j++){
                    if (recipeArrayList.get(j).getKey().equals(arrayList.get(position).getRid())){
                        title = recipeArrayList.get(j).getTitle();
                        holder.tvRecipe.setText(title);
                    }
                }
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc, tvStatus, tvRecipe, tvDate, tvUser;
            ImageView imageView;
            ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_job_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_job_tvDesc);
                tvStatus = itemView.findViewById(R.id.list_job_tvStatus);
                tvRecipe = itemView.findViewById(R.id.list_job_tvRecipe);
                tvDate = itemView.findViewById(R.id.list_job_tvDate);
                tvUser = itemView.findViewById(R.id.list_job_tvUser);
                imageView = itemView.findViewById(R.id.list_job_status);
                layout = itemView.findViewById(R.id.list_job_layout);
            }
        }
    }


    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:

                if (tvData.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(JobActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_job);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
        }

        return super.onGesture(gesture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final int id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY,
                    MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE);

            switch (id) {
                case R.id.bMarkComplete:

                    DocumentReference documentReference = firestore.collection("jobData").document(arrayList.get(currentMenuItemIndex).getKey());

                    documentReference.update("status", true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("JobActivity", "DocumentSnapshot successfully updated!");
                                    Toast.makeText(JobActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("JobActivity", "Error updating document " + e);
                                }
                            });


                    //       reference1.child("status").setValue(true);
                    //      arrayList.get(indicator.getSelectedTabPosition()).setStatus(true);
                    //     screenSlidePagerAdapter.notifyDataSetChanged();
                    break;
                case R.id.bMarkUnComplete:
                    DocumentReference documentReference1 = firestore.collection("jobData").document(arrayList.get(currentMenuItemIndex).getKey());

                    documentReference1.update("status", false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("JobActivity", "DocumentSnapshot successfully updated!");
                                    Toast.makeText(JobActivity.this, "UnCompleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("JobActivity", "Error updating document " + e);


                                }
                            });

                    //          reference1.child("status").setValue(false);
                    //         arrayList.get(indicator.getSelectedTabPosition()).setStatus(false);
                    //        screenSlidePagerAdapter.notifyDataSetChanged();
                    break;
            }

        }
    }
}