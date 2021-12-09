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
import android.graphics.PorterDuff;
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

import com.example.android.glass.glassdesign2.data.DataIssue;
import com.example.android.glass.glassdesign2.data.DataSafety;
import com.example.android.glass.glassdesign2.data.DataTitle;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SafetyFragment;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SafetyActivity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime, tvPage;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private ArrayList<DataSafety> arrayList = new ArrayList<>();
    private SafetyAdapter adapter;
    private int currentMenuItemIndex;

    private String title, key, update_key;

    private String MENU_KEY="menu_key";
    protected static final int REQUEST_CODE = 900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        tvData = findViewById(R.id.activity_safety_tvData);
        tvTitle = findViewById(R.id.activity_safety_tvTitle);
        tvTime = findViewById(R.id.activity_safety_tvTime);
        recyclerView = findViewById(R.id.activity_safety_rcView);
        tvPage = findViewById(R.id.activity_safety_tvPage);

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

        if (TextUtils.isEmpty(key)){
            Toast.makeText(this, "No key", Toast.LENGTH_SHORT).show();
            Log.e("Approval", "No key");
            finish();
        }else {

            adapter = new SafetyAdapter(this, arrayList);
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

            firestore.collection("DQNewReport").document(key).collection("content").document("safety").collection("details")
                    .orderBy("index")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("desc") != null  && snapshot.getData().get("action") != null &&
                                        snapshot.getData().get("cause") != null  && snapshot.getData().get("index") != null &&
                                        snapshot.getData().get("response") != null && snapshot.getData().get("issue_id") != null) {
                                    arrayList.add(new DataSafety(snapshot.getId(), snapshot.getData().get("desc").toString(), snapshot.getData().get("cause").toString(), snapshot.getData().get("action").toString(), snapshot.get("index", Integer.class), snapshot.get("response", Integer.class), snapshot.getData().get("issue_id").toString()));
                                    adapter.notifyDataSetChanged();
                                    Log.e("sample", snapshot.getData().toString());
                                }else {
                                    Log.e("Safety", "Missing Parameters");
                                }
                            }
                            if (arrayList.size() == 0){
                                tvPage.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView.setVisibility(View.VISIBLE);
                                tvPage.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                                tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(SafetyActivity.this, MenuActivity.class);
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
                            .collection("content").document("safety")
                            .collection("details").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("response", 1);
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("safety")
                            .collection("details").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("issue_id", "");
                    arrayList.get(currentMenuItemIndex).setResponse(1);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bReject:
                    selectedOption = "Rejected";
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("safety")
                            .collection("details").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("response", 2);
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("safety")
                            .collection("details").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("issue_id", "");
                    arrayList.get(currentMenuItemIndex).setResponse(2);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bIssue:

                    //    String key = ref1.push().getKey();
                    DataIssue dataIssue = new DataIssue("Hard-Wired Safety" ,  arrayList.get(currentMenuItemIndex).getDesc(), "", false);

                    firestore.collection("issueData")
                            .add(dataIssue)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(SafetyActivity.this, "Issue Created, Check TestApp", Toast.LENGTH_SHORT).show();
                                    arrayList.get(currentMenuItemIndex).setResponse(3);
                                    arrayList.get(currentMenuItemIndex).setIssue_id(documentReference.getId());
                                    firestore.collection("DQNewReport").document(key)
                                            .collection("content").document("safety")
                                            .collection("details").document(arrayList.get(currentMenuItemIndex).getKey())
                                            .update("response", 3);
                                    firestore.collection("DQNewReport").document(key)
                                            .collection("content").document("safety")
                                            .collection("details").document(arrayList.get(currentMenuItemIndex).getKey())
                                            .update("issue_id", documentReference.getId());
                                    Intent intent = new Intent(SafetyActivity.this, CommentActivity.class);
                                    intent.putExtra("reference", documentReference.getId().toString());
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SafetyActivity.this, "Failed to create issue", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }

        }
    }


    public class SafetyAdapter extends RecyclerView.Adapter<SafetyAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataSafety> list=new ArrayList<>();

        public SafetyAdapter(Context context, ArrayList<DataSafety> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public SafetyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_safety, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SafetyAdapter.ViewHolder holder, final int position) {

            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvCause.setText(list.get(position).getCause());
            holder.tvAction.setText(list.get(position).getAction());
            switch (list.get(position).getResponse()){
                case 0:
                    holder.tvResponse.setText("Not Updated");
                    holder.imageView.setImageResource(R.drawable.ic_baseline_info_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_blue));
                    break;
                case 1:
                    holder.tvResponse.setText("Accepted");
                    holder.imageView.setImageResource(R.drawable.ic_round_check_circle_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_green));
                    break;
                case 2:
                    holder.tvResponse.setText("Rejected");
                    holder.imageView.setImageResource(R.drawable.ic_baseline_cancel_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_red));
                    break;
                case 3:
                    holder.tvResponse.setText("Issued");
                    holder.imageView.setImageResource(R.drawable.ic_baseline_help_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_yellow));
                    break;
            }

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc, tvCause, tvAction, tvResponse;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDesc = itemView.findViewById(R.id.list_safety_tvDesc);
                tvAction = itemView.findViewById(R.id.list_safety_tvAction);
                tvCause = itemView.findViewById(R.id.list_safety_tvCause);
                tvResponse = itemView.findViewById(R.id.list_safety_tvComment);
                imageView = itemView.findViewById(R.id.list_safety_icon);

            }
        }
    }

}