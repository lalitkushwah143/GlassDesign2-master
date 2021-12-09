package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountActivity extends BaseActivity {

    private TextView tvUsername, tvEmail, tvAppName, tvVersion, tvTime;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvAppName = findViewById(R.id.activity_account_tvAppName);
        tvUsername = findViewById(R.id.activity_account_tvUser);
        tvEmail = findViewById(R.id.activity_account_tvEmail);
        tvVersion = findViewById(R.id.activity_account_tvVersion);
        tvTime = findViewById(R.id.activity_account_tvTime);

        tvVersion.setText("Version : 1.2.0");

        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            firestore.collection("users")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getData().get("email") != null && document.getData().get("username") != null) {
                                        tvEmail.setText("Email ID : " + document.getData().get("email").toString());
                                        tvUsername.setText("Username : " + document.getData().get("username").toString());

                                    }else {
                                        Log.e("Account", "Missing Parameters");
                                    }
                                }
                            }else {
                                Log.e("HomeActivity", "Error");
                            }
                        }
                    });
        }


    }
}