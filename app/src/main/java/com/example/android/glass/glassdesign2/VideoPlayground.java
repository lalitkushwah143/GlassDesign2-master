package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoPlayground extends BaseActivity {

    private ImageView tv1, tv2, tv3, tv4;

    private DatabaseReference reference, ref1, ref2, ref3, ref4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playground);
        tv1 = findViewById(R.id.activity_playground_tv1);
        tv2 = findViewById(R.id.activity_playground_tv2);
        tv3 = findViewById(R.id.activity_playground_tv3);
        tv4 = findViewById(R.id.activity_playground_tv4);

        reference = FirebaseDatabase.getInstance().getReference().child("Testing").child("playground");

        ref1 = reference.child("1");
        ref2 = reference.child("2");
        ref3 = reference.child("3");
        ref4 = reference.child("4");

        ref1.setValue("false");
        ref2.setValue("false");
        ref3.setValue("false");
        ref4.setValue("false");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("true")){
                    tv1.setVisibility(View.VISIBLE);
                }else {
                    tv1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("true")){
                    tv2.setVisibility(View.VISIBLE);
                }else {
                    tv2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("true")){
                    tv3.setVisibility(View.VISIBLE);
                }else {
                    tv3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("true")){
                    tv4.setVisibility(View.VISIBLE);
                }else {
                    tv4.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}