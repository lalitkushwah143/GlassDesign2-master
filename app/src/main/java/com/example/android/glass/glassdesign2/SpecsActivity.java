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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataSpecs;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.MainLayoutFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.android.glass.glassdesign2.fragments.SpecFragment;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SpecsActivity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime, tvPage;
    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private ArrayList<DataSpecs> arrayList = new ArrayList<>();
    private static final int REQUEST_CODE = 349;
    private String MENU_KEY="menu_key";
    private static final int REQUEST_VOICE_CODE = 999;
    private SpecsAdapter adapter;
    private int currentMenuItemIndex;

    private String title, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specs);

        tvData = findViewById(R.id.activity_specs_tvData);
        tvTime = findViewById(R.id.activity_specs_tvTime);
        tvTitle = findViewById(R.id.activity_specs_tvTitle);
        constraintLayout = findViewById(R.id.activity_specs_layout);
        tvPage = findViewById(R.id.activity_specs_tvCount);
        recyclerView = findViewById(R.id.activity_specs_rcView);

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

        if (!TextUtils.isEmpty(key)){
            tvTitle.setText("Specifications Summary");

            adapter = new SpecsAdapter(this, arrayList);
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

            firestore.collection("DQNewReport").document(key).collection("content").document("specifications").collection("specDetails")
                    .orderBy("index")
                    .addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("index") != null && snapshot.getData().get("title") != null && snapshot.getData().get("input") != null) {
                                    arrayList.add(new DataSpecs( snapshot.getId() , snapshot.get("index", Integer.class), snapshot.getData().get("title").toString(), snapshot.getData().get("input").toString()));
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Specs", "Missing Parameters");
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

        }else {
            finish();
            Toast.makeText(this, "No key Recieved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                if (tvData.getVisibility() !=  View.VISIBLE) {
               /*     Intent intent = new Intent(SpecsActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_machines);
                    startActivityForResult(intent, REQUEST_CODE);

                */

                    requestVoiceRecognition();
                }
                return true;

            case SWIPE_UP:
             //   arrayList.get(indicator.getSelectedTabPosition()).setInput("");
               // tvInput.setText("No Input");
                firestore.collection("DQNewReport").document(key)
                        .collection("content").document("specifications")
                        .collection("specDetails").document(arrayList.get(currentMenuItemIndex).getKey())
                        .update("input", "");
               // screenSliderPagerAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Comment Cleared", Toast.LENGTH_SHORT).show();
                return  true;

            default:
                return super.onGesture(gesture);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK && data != null){
            List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                String resultText = "";
                for (int i=0; i<results.size() ; i++){
                    Log.d("CommentActivity", "result: " + String.valueOf(results.get(i).toString()));
                    resultText +=  " " + results.get(i).toString();
                  //  arrayList.get(indicator.getSelectedTabPosition()).setInput(resultText);
                  //  tvInput.setText("Input: " + arrayList.get(indicator.getSelectedTabPosition()).getInput());
                }
                Log.e("Resulttext", resultText);
                firestore.collection("DQNewReport").document(key)
                        .collection("content").document("specifications")
                        .collection("specDetails").document(arrayList.get(currentMenuItemIndex).getKey())
                        .update("input", resultText);
             //   screenSliderPagerAdapter.notifyDataSetChanged();

            }else {
                Log.e("Result", "Size 0");
            }

        } else {
            Log.d("VoiceActivity", "Result not OK");
        }
    }

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, this.REQUEST_VOICE_CODE);
    }


    public class SpecsAdapter extends RecyclerView.Adapter<SpecsAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataSpecs> list=new ArrayList<>();

        public SpecsAdapter(Context context, ArrayList<DataSpecs> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public SpecsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_spec, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SpecsAdapter.ViewHolder holder, final int position) {

            holder.tvDesc.setText(list.get(position).getTitle());
            String s = list.get(position).getInput();
            if (TextUtils.isEmpty(s)){
                holder.tvInput.setText("N/A");
            }else {
                holder.tvInput.setText(s);
            }

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc, tvInput;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvInput = itemView.findViewById(R.id.list_spec_tvInput);
                tvDesc = itemView.findViewById(R.id.list_spec_tvTitle);
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        constraintLayout.setBackgroundColor(SplashActivity.back_color);
    }
}