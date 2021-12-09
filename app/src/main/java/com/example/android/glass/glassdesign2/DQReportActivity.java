package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.glass.glassdesign2.data.DataDQReport;
import com.example.glass.ui.GlassGestureDetector;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DQReportActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvTime, tvPage, tvData;
    private FirebaseFirestore firestore;
    private int currentMenuItemIndex;
    private ArrayList<DataDQReport> arrayList = new ArrayList<>();
    private DQNewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dqreport);

        tvPage = findViewById(R.id.activity_dqreport_tvPage);
        tvTime = findViewById(R.id.activity_dqreport_tvTime);
        tvData = findViewById(R.id.activity_dqreport_tvData);
        recyclerView = findViewById(R.id.activity_dqreport_rcView);

        firestore = FirebaseFirestore.getInstance();

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        adapter = new DQNewAdapter(this, arrayList);
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

        firestore.collection("DQNewReport")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("desc") != null && snapshot.getData().get("name") != null &&
                                    snapshot.getData().get("mid") != null && snapshot.getData().get("timestamp") != null) {
                                arrayList.add(new DataDQReport(snapshot.getId(), snapshot.getData().get("mid").toString(),
                                        snapshot.getData().get("name").toString(), snapshot.getData().get("desc").toString(),
                                        snapshot.getTimestamp("timestamp")));
                                adapter.notifyDataSetChanged();

                            }else {
                                Log.e("Machines", "Missing Parameters");
                            }
                        }

                        if (arrayList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            tvData.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvData.setVisibility(View.GONE);
                        }
                    }
                });

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){

            case TAP:

                if (tvData.getVisibility() != View.VISIBLE){
                    Intent intent = new Intent(DQReportActivity.this, ContentActivity.class);
                    intent.putExtra("title", arrayList.get(currentMenuItemIndex).getName());
                    intent.putExtra("key", arrayList.get(currentMenuItemIndex).getKey());
                    intent.putExtra("action", 1);
                    startActivity(intent);
                    Log.e("key", arrayList.get(currentMenuItemIndex).getKey());
                }

                return true;

            default:

                return super.onGesture(gesture);
        }
    }

    public class DQNewAdapter extends RecyclerView.Adapter<DQNewAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataDQReport> list=new ArrayList<>();

        public DQNewAdapter(Context context, ArrayList<DataDQReport> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public DQNewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dqreport, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull DQNewAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText(list.get(position).getName());
            holder.tvDesc.setText(list.get(position).getDesc());
            String date = list.get(position).getTimestamp().toDate().toString().substring(0,20);
            holder.tvTime.setText(date + "");


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvTime, tvDesc;
            CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_dqreport_tvTitle);
                tvTime = itemView.findViewById(R.id.list_dqreport_tvTime);
                tvDesc = itemView.findViewById(R.id.list_dqreport_tvDesc);
            }
        }
    }

}