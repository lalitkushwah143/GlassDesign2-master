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
import android.content.res.ColorStateList;
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

import com.example.android.glass.glassdesign2.data.DataAttach;
import com.example.android.glass.glassdesign2.data.DataComponentNew;
import com.example.android.glass.glassdesign2.data.DataDesign;
import com.example.android.glass.glassdesign2.data.DataIssue;
import com.example.android.glass.glassdesign2.data.DataPoint;
import com.example.android.glass.glassdesign2.data.DataTitle;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Design2Activity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvData, tvTitle, tvTime, tvPage;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataPoint> arrayList = new ArrayList<>();
    private DesignAdapter adapter;

    private String title, key, update_key, tid;

    private String MENU_KEY="menu_key";
    protected static final int REQUEST_CODE = 900;

    private static final int REQUEST_VOICE_CODE = 998;
    private int currentMenuItemIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design2);

        recyclerView = findViewById(R.id.activity_design2_rcView);
        tvPage = findViewById(R.id.activity_design2_tvPage);
        tvData = findViewById(R.id.activity_design2_tvData);
        tvTitle = findViewById(R.id.activity_design2_tvTitle);
        tvTime = findViewById(R.id.activity_design2_tvTime);

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
        tid = getIntent().getStringExtra("tid");
        title = getIntent().getStringExtra("title");

        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(tid) || TextUtils.isEmpty(title)){
            Toast.makeText(this, "No key", Toast.LENGTH_SHORT).show();
            Log.e("Approval", "No key");
            finish();
        }else {
            adapter = new DesignAdapter(this, arrayList);
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

            tvTitle.setText(title);

            firestore.collection("DQNewReport").document(key).collection("content").document("designSpecs").collection("points")
                    .whereEqualTo("tid", tid)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("desc") != null  && snapshot.getData().get("tid") != null && snapshot.getData().get("index") != null
                                        && snapshot.getData().get("response") != null && snapshot.getData().get("issue_id") != null) {

                                    arrayList.add(new DataPoint(snapshot.getId(), snapshot.getData().get("desc").toString(), snapshot.getData().get("tid").toString(),
                                            snapshot.get("index", Integer.class), snapshot.get("response", Integer.class), snapshot.getData().get("issue_id").toString()));
                                    adapter.notifyDataSetChanged();
                                 //   Log.e("sample", snapshot.getData().toString());
                                }else {
                                    Log.e("Approval2", "Missing Parameters");
                                }
                            }
                            if (arrayList.size() == 0){
                                recyclerView.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                                tvPage.setVisibility(View.GONE);
                            }else {

                                arrayList.sort(new Comparator<DataPoint>() {
                                    @Override
                                    public int compare(DataPoint o1, DataPoint o2) {
                                        return Integer.compare(o1.getIndex(), o2.getIndex());
                                    }
                                });
                                recyclerView.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                                tvPage.setVisibility(View.VISIBLE);
                                tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                            }
                        }
                    });
        }
    }

    public class DesignAdapter extends RecyclerView.Adapter<DesignAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataPoint> list=new ArrayList<>();

        public DesignAdapter(Context context, ArrayList<DataPoint> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public DesignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_design, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull DesignAdapter.ViewHolder holder, final int position) {

            holder.tvDesc.setText(list.get(position).getDesc());

            switch (list.get(position).getResponse()){
                case 0:
                    holder.tvComment.setText("Not Updated");
                    holder.imageView.setImageResource(R.drawable.ic_baseline_info_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_blue));
                    break;
                case 1:
                    holder.tvComment.setText("Accepted");
                    holder.imageView.setImageResource(R.drawable.ic_round_check_circle_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_green));
                    break;
                case 2:
                    holder.tvComment.setText("Rejected");
                    holder.imageView.setImageResource(R.drawable.ic_baseline_cancel_24);
                    holder.imageView.setColorFilter(getResources().getColor(R.color.design_red));
                    break;
                case 3:
                    holder.tvComment.setText("Issued");
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
            TextView tvDesc, tvComment;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDesc = itemView.findViewById(R.id.list_design_tvDesc);
                tvComment = itemView.findViewById(R.id.list_design_tvComment);
                imageView = itemView.findViewById(R.id.list_design_icon);
            }
        }
    }


    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (tvData.getVisibility() != View.VISIBLE) {
                    Intent intent = new Intent(Design2Activity.this, MenuActivity.class);
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
                            .collection("content").document("designSpecs")
                            .collection("points").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("response", 1);
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("designSpecs")
                            .collection("points").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("issue_id", "");
                //    arrayList.get(indicator.getSelectedTabPosition()).setResponse(1);
                 //   indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_round_check_circle_24));
                 //   indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_normal), PorterDuff.Mode.SRC_IN);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bReject:
                    selectedOption = "Rejected";
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("designSpecs")
                            .collection("points").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("response", 2);
                    firestore.collection("DQNewReport").document(key)
                            .collection("content").document("designSpecs")
                            .collection("points").document(arrayList.get(currentMenuItemIndex).getKey())
                            .update("issue_id", "");
                 //   arrayList.get(indicator.getSelectedTabPosition()).setResponse(2);
                 //   indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_reject_round));
                 //   indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_critical), PorterDuff.Mode.SRC_IN);
                    Toast.makeText(this.getApplicationContext(),  selectedOption, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.bIssue:

                    //    String key = ref1.push().getKey();
                    DataIssue dataIssue = new DataIssue(title ,  arrayList.get(currentMenuItemIndex).getDesc(), "", false);

                    firestore.collection("issueData")
                            .add(dataIssue)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Design2Activity.this, "Issue Created, Check TestApp", Toast.LENGTH_SHORT).show();
                              //      arrayList.get(indicator.getSelectedTabPosition()).setResponse(3);
                               //     indicator.getTabAt(indicator.getSelectedTabPosition()).setIcon(getResources().getDrawable(R.drawable.ic_baseline_help_outline_24));
                                //    indicator.getTabAt(indicator.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.color_camera), PorterDuff.Mode.SRC_IN);
                              //      arrayList.get(indicator.getSelectedTabPosition()).setIssue_id(documentReference.getId());
                                    firestore.collection("DQNewReport").document(key)
                                            .collection("content").document("designSpecs")
                                            .collection("points").document(arrayList.get(currentMenuItemIndex).getKey())
                                            .update("response", 3);
                                    firestore.collection("DQNewReport").document(key)
                                            .collection("content").document("designSpecs")
                                            .collection("points").document(arrayList.get(currentMenuItemIndex).getKey())
                                            .update("issue_id", documentReference.getId());
                                    Intent intent = new Intent(Design2Activity.this, CommentActivity.class);
                                    intent.putExtra("reference", documentReference.getId().toString());
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Design2Activity.this, "Failed to create issue", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }

        }
    }

}