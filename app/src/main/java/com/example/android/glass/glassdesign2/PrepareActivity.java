package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PrepareActivity extends BaseActivity {

    private String email = "admin@gmail.com";
    private String pass = "admin1234";
    private String machine = "1A5E78iMkto0tEzKFlEF";

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void login(){
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        SharedPreferences user_prefs= getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor  = user_prefs.edit();
                        editor.putBoolean("login_status", true);
                        editor.putString("machine_id", machine);
                        editor.putString("role", "Admin");
                        editor.commit();
                        editor.apply();

                        SplashActivity.login_status = true;
                        SplashActivity.role = "Admin";
                        SplashActivity.machine_id = machine;
                        Intent intent    = new Intent(PrepareActivity.this, MainActivity.class);
                        intent.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
                        intent.putExtra("machine_id", machine);
                        intent.putExtra("role", "Admin");
                           startActivity(intent);

                           Log.e("PrepareActivity", "initiated");
                           finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PrepareActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("Scan", "Something went wrong" + e.toString() );
                    }
                });
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){

            case TAP:
                login();
                Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show();
                return true;

            default:

                return super.onGesture(gesture);

        }

    }
}
