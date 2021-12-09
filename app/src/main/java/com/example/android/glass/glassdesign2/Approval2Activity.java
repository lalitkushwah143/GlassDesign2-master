package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.glass.glassdesign2.data.DataApproval;
import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.data.DataModuleNew;
import com.example.android.glass.glassdesign2.fragments.ApprovalFragment;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
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

public class Approval2Activity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime, tvPage;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private ArrayList<DataApproval> arrayList = new ArrayList<>();
    private ApprovalAdapter adapter;
    private int currentMenuItemIndex;

    private String title, key, update_key;
    private int type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval2);

        recyclerView = findViewById(R.id.activity_approval2_rcView);
        tvPage = findViewById(R.id.activity_approval2_tvPage);
        tvData = findViewById(R.id.activity_approval2_tvData);
        tvTitle = findViewById(R.id.activity_approval2_tvTitle);
        tvTime = findViewById(R.id.activity_approval2_tvTime);

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
        type = getIntent().getIntExtra("type", 0);

        if (TextUtils.isEmpty(key)){
            Toast.makeText(this, "No key", Toast.LENGTH_SHORT).show();
            Log.e("Approval", "No key");
            finish();
        }else {

            adapter = new ApprovalAdapter(this, arrayList);
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
                        tvPage.setText(" ");
                        return;
                    }
                    currentMenuItemIndex = layoutManager.getPosition(foundView);
                    tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                }
            });
            switch (type){
                case 0:

                    tvTitle.setText("Vendor Approvals");
                    firestore.collection("DQNewReport").document(key).collection("content").document("approval").collection("vendor")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    assert value != null;
                                    arrayList.clear();
                                    for (QueryDocumentSnapshot snapshot : value) {

                                        if (snapshot.getData().get("name") != null && snapshot.getTimestamp("timestamp") != null && snapshot.getData().get("url") != null) {

                                            arrayList.add(new DataApproval(snapshot.getId(), snapshot.getData().get("name").toString(), snapshot.getData().get("url").toString(), snapshot.getTimestamp("timestamp")));
                                            adapter.notifyDataSetChanged();
                                         //   Log.e("sample", snapshot.getData().toString());
                                        }else {
                                            Log.e("Approval2", "Missing Parameters");
                                        }
                                    }
                                    if (arrayList.size() == 0){
                                        recyclerView.setVisibility(View.GONE);
                                        tvPage.setVisibility(View.GONE);
                                        tvData.setVisibility(View.VISIBLE);
                                    }else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvPage.setVisibility(View.VISIBLE);
                                        tvData.setVisibility(View.GONE);
                                        tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                                    }
                                }
                            });
                    break;

                case 1:
                    tvTitle.setText("Customer Approvals");

                    firestore.collection("DQNewReport").document(key).collection("content").document("approval").collection("customer")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    assert value != null;
                                    arrayList.clear();
                                    for (QueryDocumentSnapshot snapshot : value) {

                                        if (snapshot.getData().get("name") != null && snapshot.getTimestamp("timestamp") != null && snapshot.getData().get("url") != null) {

                                            arrayList.add(new DataApproval(snapshot.getId(), snapshot.getData().get("name").toString(), snapshot.getData().get("url").toString(), snapshot.getTimestamp("timestamp")));
                                            adapter.notifyDataSetChanged();
                                        }else {
                                            Log.e("Approval2", "Missing Parameters");
                                        }
                                    }
                                    if (arrayList.size() == 0){
                                        recyclerView.setVisibility(View.GONE);
                                        tvPage.setVisibility(View.GONE);
                                        tvData.setVisibility(View.VISIBLE);
                                    }else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvPage.setVisibility(View.VISIBLE);
                                        tvData.setVisibility(View.GONE);
                                        tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                                    }
                                }
                            });
                    break;
            }
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
             //   if (tvData.getVisibility() != View.VISIBLE){
                if (arrayList.size() < 3) {
                    Intent intent = new Intent(Approval2Activity.this, AddApprovalActivity.class);
                    intent.putExtra("key", key);
                    switch (type) {
                        case 0:
                            intent.putExtra("type", 0);
                            startActivity(intent);

                            break;

                        case 1:
                            intent.putExtra("type", 1);
                            startActivity(intent);
                            break;
                    }
                }else {
                    Toast.makeText(this, "Cant add more than 3 Approvals", Toast.LENGTH_SHORT).show();
                }

            //    }
                return  true;

            default:

                return super.onGesture(gesture);
        }
    }

    public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataApproval> list=new ArrayList<>();

        public ApprovalAdapter(Context context, ArrayList<DataApproval> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_approval, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ApprovalAdapter.ViewHolder holder, final int position) {

            holder.tvName.setText("Name: " + list.get(position).getName());
            holder.tvDate.setText("Date: " + list.get(position).getTimestamp().toDate().toString().substring(0, 20));
            Glide.with(context).load(list.get(position).getUrl()).into(holder.ivApproval);

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate;
            ImageView ivApproval;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.list_approval_tvName);
                tvDate = itemView.findViewById(R.id.list_approval_tvDate);
                ivApproval = itemView.findViewById(R.id.list_approval_imageview);
            }
        }
    }
}