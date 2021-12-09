package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataTemp;
import com.example.android.glass.glassdesign2.data.DataUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends BaseActivity {

    private String email, pass, uid, machine_id;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DatabaseReference reference;
    private DataUsers dataUsers = new DataUsers();
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Log.e("In Login", "now");


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        uid = getIntent().getStringExtra("uid");
        machine_id = getIntent().getStringExtra("machine_id");




    }

    @Override
    protected void onResume() {
        super.onResume();

        if (uid != null && machine_id != null){
            Log.e("Login", "not null");
            reference = FirebaseDatabase.getInstance().getReference().child("tempUserData");

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.getKey().equals(uid)){
                        DataTemp dataTemp = snapshot.getValue(DataTemp.class);
                        processing(dataTemp);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            reference.addChildEventListener(childEventListener);

         /*
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataTemp dataTemp = snapshot.getValue(DataTemp.class);
                  //  Log.e("Login", dataTemp.toString());
                    processing(dataTemp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            reference.addListenerForSingleValueEvent(valueEventListener);

          */

        }else {
            Log.e("Login", "uid null");
        }
    }

    private void processing(DataTemp dataTemp){
        Log.e("Login", "in processing");
        reference.removeEventListener(childEventListener);
      //  reference.removeEventListener(valueEventListener);

        if (firebaseAuth.getCurrentUser() == null) {
            Log.e("Login", "user not logged");

            if (dataTemp != null) {
                Log.e("Login", "data not null");
                login(dataTemp.getEmail().toString(), dataTemp.getPass().toString());

            } else {
                Log.e("Login", "data null");
                expired();
            }

        }else {
            Log.e("Login", "User logged in");
        }
    }

    private void expired(){
      //  reference.removeEventListener(valueEventListener);

        Toast.makeText(LoginActivity.this, "QR Code Expired", Toast.LENGTH_SHORT).show();
        Log.e("ScanActivity", "QR Code Expired");
        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
        finish();
    }

    private void login(String email, String pass){
     //   reference.removeEventListener(valueEventListener);

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Signed In Successfully");
                        reference.setValue(null);

                        if (firebaseAuth.getCurrentUser() != null) {

                            firestore.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                    if (snapshot.getData().get("email") != null && snapshot.getData().get("password") != null &&
                                                            snapshot.getData().get("firstName") != null && snapshot.getData().get("lastName") != null &&
                                                            snapshot.getData().get("phone") != null && snapshot.getData().get("role") != null) {
                                                        dataUsers.setKey(snapshot.getId());
                                                        dataUsers.setEmail(snapshot.getData().get("email").toString());
                                                        dataUsers.setPassword(snapshot.getData().get("password").toString());
                                                        dataUsers.setFirstName(snapshot.getData().get("firstName").toString());
                                                        dataUsers.setLastName(snapshot.getData().get("lastName").toString());
                                                        dataUsers.setPhone(snapshot.getData().get("phone").toString());
                                                        dataUsers.setRole(snapshot.getData().get("role").toString());
                                                        Log.e("MainActivity", dataUsers.getEmail().toString());
                                                    }
                                                }

                                                SharedPreferences sharedPreferences = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("login_status", true);
                                                editor.putString("machine_id", machine_id);
                                                editor.putString("role", dataUsers.getRole());
                                                editor.commit();
                                                editor.apply();
                                                SplashActivity.login_status = true;
                                                SplashActivity.machine_id = machine_id;
                                                SplashActivity.role = dataUsers.getRole();

                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("uid", uid);
                                                intent.putExtra("machine_id", machine_id);
                                                intent.putExtra("role", dataUsers.getRole());
                                                setResult(RESULT_OK, intent);
                                                startActivity(intent);
                                                Log.e("ScanActivity", "initiated");

                                                finish();
                                            } else {
                                                Log.d("LoginActivity", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

/*
                            firestore.collection("users")
                                    .whereEqualTo("email", email)
                                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            for (QueryDocumentSnapshot snapshot : value) {
                                                dataUsers.setKey(snapshot.getId());
                                                dataUsers.setEmail(snapshot.getData().get("email").toString());
                                                dataUsers.setPass(snapshot.getData().get("password").toString());
                                                dataUsers.setfName(snapshot.getData().get("firstName").toString());
                                                dataUsers.setlName(snapshot.getData().get("lastName").toString());
                                                dataUsers.setPhone(snapshot.getData().get("phone").toString());
                                                dataUsers.setRole(snapshot.getData().get("role").toString());
                                                Log.e("MainActivity", dataUsers.getEmail().toString());
                                            }
                                            SharedPreferences sharedPreferences = getSharedPreferences("machine_prefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("login_status", true);
                                            editor.putString("machine_id", machine_id);
                                            editor.putString("role", dataUsers.getRole());
                                            editor.commit();
                                            editor.apply();
                                            SplashActivity.login_status = true;
                                            SplashActivity.machine_id = machine_id;
                                            SplashActivity.role = dataUsers.getRole();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("uid", uid);
                                            intent.putExtra("machine_id", machine_id);
                                            intent.putExtra("role", dataUsers.getRole());
                                            setResult(RESULT_OK, intent);
                                            startActivity(intent);
                                            Log.e("ScanActivity", "initiated");

                                            finish();

                                        }
                                    });

 */
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        Log.e("ScanActivity", "Something Went Wrong");
                        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                        finish();
                    }
                });
    }
}