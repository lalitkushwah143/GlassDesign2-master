package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataAbbreviation;
import com.example.android.glass.glassdesign2.data.DataAttach;
import com.example.android.glass.glassdesign2.data.DataDQNew;
import com.example.android.glass.glassdesign2.data.DataDesc;
import com.example.android.glass.glassdesign2.data.DataDesign;
import com.example.android.glass.glassdesign2.data.DataModuleNew;
import com.example.android.glass.glassdesign2.data.DataPoint;
import com.example.android.glass.glassdesign2.data.DataSafety;
import com.example.android.glass.glassdesign2.data.DataSpec1;
import com.example.android.glass.glassdesign2.data.DataSpecs;
import com.example.android.glass.glassdesign2.data.DataTitle;
import com.example.android.glass.glassdesign2.fragments.BaseFragment;
import com.example.android.glass.glassdesign2.fragments.SingleFragment;
import com.example.glass.ui.GlassGestureDetector;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContentActivity extends BaseActivity {

    private TextView tvData, tvTitle, tvTime;
    private ViewPager viewPager;
    private TabLayout indicator;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference, updateRef, ref;
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<DataDQNew> arrayList = new ArrayList<>();

    private String title, key, update_key;
    private int action = -1;


    final ScreenSlidePagerAdapter screenSliderPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        viewPager = findViewById(R.id.activity_content_viewpager);
        indicator = findViewById(R.id.activity_content_indicator);
        tvData = findViewById(R.id.activity_content_tvData);
        tvTitle = findViewById(R.id.activity_content_tvTitle);
        tvTime = findViewById(R.id.activity_content_tvTime);

        firestore = FirebaseFirestore.getInstance();


        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);
        
        title = getIntent().getStringExtra("title");
        key = getIntent().getStringExtra("key");
        action = getIntent().getIntExtra("action", -1);
        
        if (!TextUtils.isEmpty(title) &&  !TextUtils.isEmpty(key)){
            tvTitle.setText(title);
            Log.e("key", key);

            switch (action){
                case 0:
                    createReport(key);
                    break;

                case 1:
                    // Only assign update key
                    update_key = key;
                    break;

                default:
                    finish();
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    Log.e("Content", "Something went wrong / No action added");
                    break;
            }

         //   createReport(key);

        }else {
            finish();
            Toast.makeText(this, "No data Received", Toast.LENGTH_SHORT).show();
        }
        
        indicator.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(screenSliderPagerAdapter);

        if (fragments.size() == 0){
            fragments.add(SingleFragment.newInstance("Zero", "Approval"));
            fragments.add(SingleFragment.newInstance("One", "Purpose"));
            fragments.add(SingleFragment.newInstance("Two", "General Information"));
            fragments.add(SingleFragment.newInstance("Three", "Specifications"));
            fragments.add(SingleFragment.newInstance("Four", "Configuration"));
            fragments.add(SingleFragment.newInstance("Five", "Design Specifications"));
            fragments.add(SingleFragment.newInstance("Six", "List of Safety Interlocks"));
            fragments.add(SingleFragment.newInstance("Seven", "Attachments"));
            fragments.add(SingleFragment.newInstance("Eight", "Abbreviations"));
        }
        screenSliderPagerAdapter.notifyDataSetChanged();

    }

    private void createReport(String key){

        updateRef = firestore.collection("DQNew").document(key);
        updateRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                firestore.collection("DQNewReport").add(new DataDQNew( value.get("mid").toString(), value.get("name").toString(), value.get("desc").toString()))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e("Report", "Generated Successfully");
                                documentReference.update("timestamp", Timestamp.now());
                                update_key = documentReference.getId();

                                completeProcess(documentReference);

                            }
                        });
            }
        });

    }

    private void completeProcess(DocumentReference reference){
        updateApproval(reference);
        updatePurpose(reference);
        updateGeneral(reference);
        updateSpecs(reference);
       // updateConfig(reference);
        updateConfig2(reference);
        updateDesign(reference);
        updateSafety(reference);
        updateAttachments(reference);
        updateAbbreviation(reference);
    }

    private void updateApproval(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("approval");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("approval").set(new DataDesc(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "approval Added");
                                }
                            });
                }else {
                    Log.e("Content", "No approval Data");
                }
            }
        });
    }

    private void updatePurpose(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("purpose");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("purpose").set(new DataDesc(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "Purpose Added");
                                }
                            });
                }else {
                    Log.e("Content", "No purpose Data");
                }
            }
        });
    }
    private void updateGeneral(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("general");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("general").set(new DataDesc(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "General Added");
                                }
                            });
                }else {
                    Log.e("Content", "No General Data");
                }
            }
        });
    }
    private void updateSpecs(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("specifications");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("name") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("specifications").set(new DataSpec1(documentSnapshot.get("name").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "Specs 1 Added");

                                    firestore.collection("DQNew").document(key).collection("content").document("specifications").collection("specDetails")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                                    for (QueryDocumentSnapshot snapshot : value) {
                                                        if (snapshot.getData().get("desc") != null && snapshot.getData().get("index") != null){
                                                            reference.collection("content").document("specifications").collection("specDetails")
                                                                    .add(new DataSpecs(snapshot.get("index", Integer.class), snapshot.getData().get("desc").toString(), ""));
                                                        }
                                                    }
                                                    Log.e("Content", "Added Specs");
                                                    }
                                            });
                                }
                            });
                }else {
                    Log.e("Content", "No purpose Data");
                }
            }
        });
    }
  /*  private void updateConfig(DocumentReference reference_main){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("configuration");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("name") != null && documentSnapshot.get("desc") != null){
                    reference_main.collection("content").document("configuration").set(new DataSpec1(documentSnapshot.get("name").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "Config 1 Added");
                                    firestore.collection("DQNew").document(key).collection("content").document("configuration").collection("modules")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("index") != null){
                                                    reference_main.collection("content").document("configuration").collection("modules")
                                                            .add(new DataModuleNew( snapshot.getData().get("title").toString(), snapshot.getData().get("desc").toString(), snapshot.get("index", Integer.class)))
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference reference) {
                                                                    firestore.collection("DQNew").document(key).collection("content").document("configuration").collection("components")
                                                                            .whereEqualTo("module_id", snapshot.getId())
                                                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                                                if (snapshot.getData().get("title") != null && snapshot.getData().get("value") != null && snapshot.getData().get("index") != null && snapshot.getData().get("module_id") != null){
                                                                                    reference_main.collection("content").document("configuration").collection("components")
                                                                                            .add(new DataComponentNew( snapshot.get("index", Integer.class) , snapshot.getData().get("title").toString(),
                                                                                                    snapshot.getData().get("value").toString(), reference.getId(), 0, ""));
                                                                                    new DataComponentNew(1, "", "", "", 1, "");
                                                                                }
                                                                            }
                                                                            Log.e("Content", "Added Config 3");
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }
                                            }
                                            Log.e("Content", "Added Config 2");
                                        }
                                    });
                                }
                            });
                }else {
                    Log.e("Content", "No purpose Data");
                }
            }
        });
    }


   */
    private void updateConfig2(DocumentReference reference_main){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("config");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference_main.collection("content").document("config").set(new DataSpec1(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "Config 1 Added");
                                    firestore.collection("DQNew").document(key).collection("content").document("config").collection("module")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("index") != null && snapshot.getData().get("type") != null){
                                                    reference_main.collection("content").document("config").collection("modules")
                                                            .add(new DataModuleNew( snapshot.getData().get("title").toString(), snapshot.getData().get("desc").toString(), snapshot.get("index", Integer.class), snapshot.get("type", Integer.class)))
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference reference) {
                                                                    firestore.collection("DQNew").document(key).collection("content").document("config").collection("components")
                                                                            .whereEqualTo("module_id", snapshot.getId())
                                                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                                                if (snapshot.exists()) {
                                                                                    reference_main.collection("content").document("config").collection("components")
                                                                                            .add(snapshot.getData())
                                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                @Override
                                                                                                public void onSuccess(DocumentReference reference1) {
                                                                                                    reference1.update("module_id", reference.getId());
                                                                                                    reference1.update("response", 0);
                                                                                                    reference1.update("issue_id", "");
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                            Log.e("Content", "Added Config 3");
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }
                                            }
                                            Log.e("Content", "Added Config 2");
                                        }
                                    });
                                }
                            });
                }else {
                    Log.e("Content", "No purpose Data");
                }
            }
        });
    }


    private void updateDesign(DocumentReference reference_main){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("designSpecs");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference_main.collection("content").document("designSpecs").set(new DataDesign(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "design 1 Added");
                                    firestore.collection("DQNew").document(key).collection("content").document("designSpecs").collection("title")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                if (snapshot.getData().get("title") != null && snapshot.getData().get("index") != null){
                                                    reference_main.collection("content").document("designSpecs").collection("title")
                                                            .add(new DataTitle( snapshot.getData().get("title").toString(), snapshot.get("index", Integer.class)))
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference reference) {
                                                                    firestore.collection("DQNew").document(key).collection("content").document("designSpecs").collection("points")
                                                                            .whereEqualTo("tid", snapshot.getId())
                                                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                                                if (snapshot.getData().get("desc") != null && snapshot.getData().get("tid") != null && snapshot.getData().get("index") != null){
                                                                                    reference_main.collection("content").document("designSpecs").collection("points")
                                                                                            .add(new DataPoint( snapshot.getData().get("desc").toString(), reference.getId(), snapshot.get("index", Integer.class) ,
                                                                                                    0, ""));
                                                                                }
                                                                            }
                                                                            Log.e("Content", "Added design 3");
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }
                                            }
                                            Log.e("Content", "Added design 2");
                                        }
                                    });
                                }
                            });
                }else {
                    Log.e("Content", "No design Data");
                }
            }
        });
    }

    private void updateSafety(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("safety");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("safety").set(new DataDesign(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "Safety 1 Added");

                                    firestore.collection("DQNew").document(key).collection("content").document("safety").collection("details")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                                    for (QueryDocumentSnapshot snapshot : value) {
                                                        if (snapshot.getData().get("desc") != null && snapshot.getData().get("index") != null && snapshot.getData().get("action") != null && snapshot.getData().get("cause") != null){
                                                            reference.collection("content").document("safety").collection("details")
                                                                    .add(new DataSafety(snapshot.getData().get("desc").toString(), snapshot.getData().get("cause").toString(), snapshot.getData().get("action").toString(), snapshot.get("index", Integer.class), 0, ""));
                                                        }
                                                    }
                                                    Log.e("Content", "Added Safety");
                                                }
                                            });
                                }
                            });
                }else {
                    Log.e("Content", "No safety Data");
                }
            }
        });
    }

    private void updateAttachments(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("attachments");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("attachments").set(new DataDesign(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "attach 1 Added");

                                    firestore.collection("DQNew").document(key).collection("content").document("attachments").collection("details")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                                    for (QueryDocumentSnapshot snapshot : value) {
                                                        if (snapshot.getData().get("desc") != null && snapshot.getData().get("index") != null && snapshot.getData().get("dno") != null && snapshot.getData().get("rev") != null){
                                                            reference.collection("content").document("attachments").collection("details")
                                                                    .add(new DataAttach(snapshot.getData().get("desc").toString(), snapshot.getData().get("rev").toString(), snapshot.getData().get("dno").toString(), snapshot.get("index", Integer.class)));
                                                        }
                                                    }
                                                    Log.e("Content", "Added attch");
                                                }
                                            });
                                }
                            });
                }else {
                    Log.e("Content", "No safety Data");
                }
            }
        });
    }

    private void updateAbbreviation(DocumentReference reference){
        documentReference = firestore.collection("DQNew").document(key).collection("content").document("abbreviations");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                    reference.collection("content").document("abbreviations").set(new DataDesign(documentSnapshot.get("title").toString(), documentSnapshot.get("desc").toString()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("Content", "attach 1 Added");

                                    firestore.collection("DQNew").document(key).collection("content").document("abbreviations").collection("details")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                                    for (QueryDocumentSnapshot snapshot : value) {
                                                        if (snapshot.getData().get("full") != null && snapshot.getData().get("index") != null && snapshot.getData().get("short") != null){
                                                            reference.collection("content").document("abbreviations").collection("details")
                                                                    .add(new DataAbbreviation(snapshot.getData().get("short").toString(), snapshot.getData().get("full").toString(), snapshot.get("index", Integer.class)));
                                                            new DataAbbreviation("","", 1);
                                                        }
                                                    }
                                                    Log.e("Content", "Added attch");
                                                }
                                            });
                                }
                            });
                }else {
                    Log.e("Content", "No safety Data");
                }
            }
        });
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        
        switch (gesture){
            case TAP:

                switch (action){
                    case 0:
                        ref = firestore.collection("DQNew").document(key);
                        break;

                    case 1:
                        ref = firestore.collection("DQNewReport").document(key);
                        break;

                    default:
                        // Do nothing
                        finish();
                        break;
                }

                switch (indicator.getSelectedTabPosition()){

                    case 0:
                        documentReference = ref.collection("content").document("approval");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent intent = new Intent(ContentActivity.this, ApprovalActivity.class);
                                    intent.putExtra("key", update_key);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Approval Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;

                    case 1:
                        documentReference = ref.collection("content").document("purpose");
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            Intent intent = new Intent(ContentActivity.this, DescActivity.class);
                                            intent.putExtra("title", 0);
                                            intent.putExtra("key", update_key);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(ContentActivity.this, "No purpose Data Added", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;
                    case 2:
                        documentReference = ref.collection("content").document("general");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent intent1 = new Intent(ContentActivity.this, DescActivity.class);
                                    intent1.putExtra("title", 1);
                                    intent1.putExtra("key", update_key);
                                    startActivity(intent1);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No General Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;
                        
                    case 3:
                        documentReference = ref.collection("content").document("specifications");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent intent2 = new Intent(ContentActivity.this, SpecsActivity.class);
                                    intent2.putExtra("key", update_key);
                                    startActivity(intent2);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Specification Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;
                        
                    case 4:
                        documentReference = ref.collection("content").document("config");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent moduleIntent = new Intent(ContentActivity.this, ModuleActivity.class);
                                    moduleIntent.putExtra("key", update_key);
                                    startActivity(moduleIntent);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Configuration Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                    case 5:
                        documentReference = ref.collection("content").document("designSpecs");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent moduleIntent = new Intent(ContentActivity.this, DesignActivity.class);
                                    moduleIntent.putExtra("key", update_key);
                                    startActivity(moduleIntent);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Deisgn Specification Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                    case 6:
                        documentReference = ref.collection("content").document("safety");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent moduleIntent = new Intent(ContentActivity.this, SafetyActivity.class);
                                    moduleIntent.putExtra("key", update_key);
                                    startActivity(moduleIntent);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Safety Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                    case 7:
                        documentReference = ref.collection("content").document("attachments");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent moduleIntent = new Intent(ContentActivity.this, AttachActivity.class);
                                    moduleIntent.putExtra("key", update_key);
                                    startActivity(moduleIntent);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Safety Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                    case 8:
                        documentReference = ref.collection("content").document("abbreviations");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    Intent moduleIntent = new Intent(ContentActivity.this, AbbreviationActivity.class);
                                    moduleIntent.putExtra("key", update_key);
                                    startActivity(moduleIntent);
                                }else {
                                    Toast.makeText(ContentActivity.this, "No Abbreviation Data Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;


                    default:
                        Toast.makeText(this, "Nothing Found", Toast.LENGTH_SHORT).show();
                        break;
                }
                
                return  true;
                
            default:

                return super.onGesture(gesture);
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
}