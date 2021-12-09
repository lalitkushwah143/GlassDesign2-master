package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataApproval;
import com.example.android.glass.glassdesign2.data.DataAttach;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SafetyFragment;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AttachActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tvPage;
    private TextView tvData, tvTitle, tvTime;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataAttach> arrayList = new ArrayList<>();
    private AttachAdapter attachAdapter;
    private static final int REQUEST_VOICE_CODE = 998;
    private int currentMenuItemIndex;

    private String title, key, update_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach);

        tvData = findViewById(R.id.activity_attach_tvData);
        tvTitle = findViewById(R.id.activity_attach_tvTitle);
        tvTime = findViewById(R.id.activity_attach_tvTime);
        recyclerView = findViewById(R.id.activity_attach_rcView);
        tvPage = findViewById(R.id.activity_attach_tvPage);

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
          //  indicator.setupWithViewPager(viewPager, true);
           // viewPager.setAdapter(screenSliderPagerAdapter);

            attachAdapter = new AttachAdapter(this, arrayList);
            SnapHelper snapHelper = new PagerSnapHelper();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(attachAdapter);
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


            firestore.collection("DQNewReport").document(key).collection("content").document("attachments").collection("details")
                    .orderBy("index")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            fragments.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("desc") != null  && snapshot.getData().get("dno") != null &&
                                        snapshot.getData().get("rev") != null  && snapshot.getData().get("index") != null) {

                                    arrayList.add(new DataAttach(snapshot.getId(), snapshot.getData().get("desc").toString(), snapshot.getData().get("rev").toString(), snapshot.getData().get("dno").toString(), snapshot.get("index", Integer.class)));
                                    attachAdapter.notifyDataSetChanged();
                                  //  fragments.add(SafetyFragment.newInstance(snapshot.getId(), snapshot.getData().get("desc").toString(), snapshot.getData().get("rev").toString(), snapshot.getData().get("action").toString(), snapshot.get("index", Integer.class)));
                                 //   screenSliderPagerAdapter.notifyDataSetChanged();
                                    Log.e("sample", snapshot.getData().toString());
                                }else {
                                    Log.e("Safety", "Missing Parameters");
                                }
                            }
                            if (arrayList.size() == 0){
                              //  viewPager.setVisibility(View.GONE);
                               // indicator.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                tvData.setVisibility(View.VISIBLE);
                                tvPage.setVisibility(View.GONE);
                            }else {
                              //  viewPager.setVisibility(View.VISIBLE);
                               // indicator.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                tvData.setVisibility(View.GONE);
                                tvPage.setVisibility(View.VISIBLE);
                                tvPage.setText( (currentMenuItemIndex + 1) + " of " + (arrayList.size()));
                            }
                        }
                    });
          //  screenSliderPagerAdapter.notifyDataSetChanged();
        }


    }

    public class AttachAdapter extends RecyclerView.Adapter<AttachAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataAttach> list=new ArrayList<>();

        public AttachAdapter(Context context, ArrayList<DataAttach> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public AttachAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attach, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull AttachAdapter.ViewHolder holder, final int position) {

            holder.tvRev.setText(list.get(position).getRev());
            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvDno.setText( " " +list.get(position).getDno());

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc, tvRev, tvDno;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRev = itemView.findViewById(R.id.list_attach_tvRevision);
                tvDno = itemView.findViewById(R.id.list_attach_tvDrawing);
                tvDesc = itemView.findViewById(R.id.list_attach_tvDesc);
            }
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                requestVoiceRecognition();
                return  true;

            default:
               // Log.e("ID", "is: " + recyclerView.getLayoutManager().getFocusedChild().getId());

                return super.onGesture(gesture);
        }
    }

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, REQUEST_VOICE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == REQUEST_VOICE_CODE && resultCode == RESULT_OK && data != null){
            List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                String name = "";
                for (int i=0; i<results.size() ; i++){
                 //   Log.d("Add Approval", "result: " + String.valueOf(results.get(i).toString()));
                    name +=  " " + results.get(i).toString();
                    Log.e("attach", "is " + name);

                  //  arrayList.get(Objects.requireNonNull(recyclerView.getLayoutManager()).getFocusedChild().getId()).setDno(name);
                }
                firestore.collection("DQNewReport").document(key).collection("content")
                        .document("attachments").collection("details")
                        .document(arrayList.get(currentMenuItemIndex).getKey())
                        .update("dno", name);
                attachAdapter.notifyDataSetChanged();
                Log.e("resutl name", name);
            }else {
                Log.e("Result", "Size 0");
            }

        }
    }
}