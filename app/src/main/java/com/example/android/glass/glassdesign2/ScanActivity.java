package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataTemp;
import com.example.android.glass.glassdesign2.data.DataUsers;
import com.example.android.glass.glassdesign2.util.DropDownAlert;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private DropDownAlert dropDownAlert;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.BLUETOOTH
    };

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private String email, pass;
    private Boolean aBoolean;



    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Programmatically initialize the scanner view
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        mScannerView = new ZXingScannerView(this);

        // Set the scanner view as the content view
        setContentView(mScannerView);
        dropDownAlert = new DropDownAlert(this);
        dropDownAlert.setText("Scan QR Code");
        dropDownAlert.setTextWeight(0.5f);
        //  dropDownAlert.addImages("surfer.png", "bike.png");
        dropDownAlert.show();
        Log.e("In Scan", "now");

    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.e("result", rawResult.getText());
        Log.e("result", rawResult.getBarcodeFormat().toString());
        //If you would like to resume scanning, call this method below:
        //  mScannerView.resumeCameraPreview(this);

        String result = rawResult.getText();
        String[] arrOfStr = result.split("/", 2);

        if(arrOfStr.length == 2 && rawResult.getBarcodeFormat().toString().equals("QR_CODE")) {

            Log.e("size", arrOfStr.length + "");
            Log.e("Uid", arrOfStr[0]);
            Log.e("machine_id", arrOfStr[1]);

            Intent intent = new Intent(ScanActivity.this, LoginActivity.class);

            intent.putExtra("uid", arrOfStr[0]);
            intent.putExtra("machine_id", arrOfStr[1]);

            startActivity(intent);
            finish();

      //      firebaseAuth = FirebaseAuth.getInstance();
       //     firestore = FirebaseFirestore.getInstance();
/*
            aBoolean= firebaseAuth.getCurrentUser() != null;

            reference = FirebaseDatabase.getInstance().getReference().child("tempUserData").child(arrOfStr[0]);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)) {

                        DataTemp dataTemp = snapshot.getValue(DataTemp.class);
                        if (firebaseAuth.getCurrentUser() == null) {
                            if (dataTemp != null) {
                                email = dataTemp.getEmail().toString();
                                pass = dataTemp.getPass().toString();
                                firebaseAuth.signInWithEmailAndPassword(email, pass)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Toast.makeText(ScanActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                                                Log.e("ScanActivity", "Signed In Successfully");

                                                reference.setValue(null);

                                                if (firebaseAuth.getCurrentUser() != null) {
                                                    aBoolean = true;
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
                                                                    editor.putString("machine_id", arrOfStr[1]);
                                                                    editor.putString("role", dataUsers.getRole());
                                                                    editor.commit();
                                                                    editor.apply();
                                                                    SplashActivity.login_status = true;
                                                                    SplashActivity.machine_id = arrOfStr[1];
                                                                    SplashActivity.role = dataUsers.getRole();


                                                                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                                                                    intent.putExtra("uid", arrOfStr[0]);
                                                                    intent.putExtra("machine_id", arrOfStr[1]);
                                                                    intent.putExtra("role", dataUsers.getRole());
                                                                    setResult(RESULT_OK, intent);
                                                                    startActivity(intent);
                                                                    Log.e("ScanActivity", "initiated");

                                                                    finish();
                                                                }
                                                            });
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ScanActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                Log.e("ScanActivity", "Something Went Wrong");
                                            }
                                        });

                            } else {
                                Toast.makeText(ScanActivity.this, "QR Code Expired", Toast.LENGTH_SHORT).show();
                                Log.e("ScanActivity", "QR Code Expired");
                                startActivity(new Intent(ScanActivity.this, SplashActivity.class));
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


 */
        }else {
            mScannerView.resumeCameraPreview(this);
        }


/*
        Intent intent = new Intent(ScanActivity.this, MainActivity.class);

        switch (result) {
            case "Operator":
                intent.putExtra("menu_key", R.menu.menu_operator);
                Toast.makeText(this, "Operator Module", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case "Supervisor":
                intent.putExtra("menu_key", R.menu.menu_supervisor);
                Toast.makeText(this, "Supervisor Module", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case "Trainee":
                intent.putExtra("menu_key", R.menu.menu_trainee);
                Toast.makeText(this, "Trainee Module", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case "Validator":
                intent.putExtra("menu_key", R.menu.menu_validator);
                Toast.makeText(this, "Validator Module", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case "Maintenance":
                intent.putExtra("menu_key", R.menu.menu_maintenance);
                Toast.makeText(this, "Maintenance Module", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "No user Found", Toast.LENGTH_SHORT).show();
                Intent intent1 = getIntent();
                finish();
                startActivity(intent1);
                break;
        }
        finish();

 */
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}