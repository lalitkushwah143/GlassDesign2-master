package com.example.android.glass.glassdesign2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DescActivity extends BaseActivity {

    private TextView tvTitle, tvContent, tvTime;
    private ConstraintLayout constraintLayout;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    private String key;
    private int title;

    final TextToSpeech[] textToSpeech = new TextToSpeech[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        firestore = FirebaseFirestore.getInstance();

        tvTime = findViewById(R.id.activity_desc_tvTime);
        tvTitle = findViewById(R.id.activity_desc_tvTitle);
        tvContent = findViewById(R.id.activity_desc_tvContent);
        constraintLayout = findViewById(R.id.activity_desc_layout);

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        key = getIntent().getStringExtra("key");
        title = getIntent().getIntExtra("title", 0);

        if (!TextUtils.isEmpty(key)){
            switch (title){
                case 0:

                    tvTitle.setText("Purpose");
                    documentReference = firestore.collection("DQNewReport").document(key).collection("content").document("purpose");

                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            if (value.getData().get("desc") != null){
                                tvContent.setText(value.getData().get("desc").toString());
                                TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        Toast.makeText(DescActivity.this, "Statu" + status, Toast.LENGTH_SHORT).show();
                                    }
                                };
                                textToSpeech[0] = new TextToSpeech(DescActivity.this, new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        textToSpeech[0].speak(value.getData().get("desc").toString(), TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                });
                            }
                        }
                    });
                    break;

                case 1:


                    tvTitle.setText("General Information");

                    documentReference = firestore.collection("DQNewReport").document(key).collection("content").document("general");

                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            if (value.getData().get("desc") != null){
                                tvContent.setText(value.getData().get("desc").toString());
                                TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        Toast.makeText(DescActivity.this, "Statu" + status, Toast.LENGTH_SHORT).show();
                                    }
                                };
                                textToSpeech[0] = new TextToSpeech(DescActivity.this, new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        textToSpeech[0].speak(value.getData().get("desc").toString(), TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                });
                            }
                        }
                    });
                    break;
            }
        }else {
            finish();
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        constraintLayout.setBackgroundColor(SplashActivity.back_color);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null && textToSpeech[0].isSpeaking()){
            textToSpeech[0].stop();
        }
    }
}