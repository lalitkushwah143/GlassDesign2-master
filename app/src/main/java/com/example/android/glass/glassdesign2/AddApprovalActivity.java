package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataApproval;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Time;
import java.util.List;

public class AddApprovalActivity extends BaseActivity {

    private String key, name, url;
    private Boolean FLAG;
    private Uri photoURI;
    private Timestamp timestamp;
    private int type;
    private FirebaseFirestore firestore;
    private StorageReference storageReference, ref1;
    private ImageView imageView;
    private TextView tvName, tvDate;

    private static final int REQUEST_CODE = 799;
    private static final int REQUEST_VOICE_CODE = 998;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 128;
    private String MENU_KEY="menu_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_approval);

        tvName = findViewById(R.id.add_approval_tvName);
        tvDate = findViewById(R.id.add_approval_tvDate);
        imageView = findViewById(R.id.add_approval_imageview);


        key = getIntent().getStringExtra("key");
        type = getIntent().getIntExtra("type", 0);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        if (TextUtils.isEmpty(key)){
            Toast.makeText(this, "no key", Toast.LENGTH_SHORT).show();
            Log.e("Add Approval","No key");
            finish();
        }else {
            name = "";
            url = "";
            FLAG = false;
            timestamp = Timestamp.now();
            tvDate.setText(timestamp.toDate().toString().substring(0,20));
        }
    }


    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (!TextUtils.isEmpty(key)){
                    Intent intent = new Intent(AddApprovalActivity.this, MenuActivity.class);
                    intent.putExtra(MENU_KEY, R.menu.menu_add_approval);
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

            switch (id) {
                case R.id.bAddApprovalName:
                    requestVoiceRecognition();

                    break;
                case R.id.bAddApprovalImage:
                    if (checkCameraHardware(AddApprovalActivity.this)){
                        Intent intent = new Intent(AddApprovalActivity.this, Camera2Activity.class);
                        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                    }else {
                        Toast.makeText(AddApprovalActivity.this, "No camera", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.bAddApprovalSave:
                    if (photoURI != null && !TextUtils.isEmpty(name)){
                        switch (type){
                            case 0:

                                firestore.collection("DQNewReport").document(key).collection("content").document("approval").collection("vendor")
                                        .add(new DataApproval(name, "", timestamp))
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference reference) {
                                                Log.e("Add Approval", "step 1 Complete");
                                                ref1 = storageReference.child("media").child("approval").child(reference.getId());

                                                UploadTask uploadTask = ref1.putFile(photoURI);
                                                Toast.makeText(AddApprovalActivity.this, "Uploading", Toast.LENGTH_SHORT).show();

                                                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        return ref1.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            Uri uri1 = task.getResult();
                                                            //      ref2.setValue(new DataRecord(key, now.toString(), uri1.toString()));

                                                            reference.update("url", uri1.toString());
                                                            //   documentReference.update("timestamp", firebase.firestore.ServerValue.TIMESTAMP )
                                                            finish();
                                                            Toast.makeText(AddApprovalActivity.this, "image Upload Succesful", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            reference.delete();
                                                            Log.e(task.getException() + "s", "this");
                                                            Toast.makeText(AddApprovalActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.e("Add Approval", "failed to add Approval");
                                    }
                                });

                                break;

                            case 1:
                                firestore.collection("DQNewReport").document(key).collection("content").document("approval").collection("customer")
                                        .add(new DataApproval(name, "", timestamp))
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference reference) {
                                                Log.e("Add Approval", "step 1 Complete");
                                                ref1 = storageReference.child("media").child("approval").child(reference.getId());

                                                UploadTask uploadTask = ref1.putFile(photoURI);
                                                Toast.makeText(AddApprovalActivity.this, "Uploading", Toast.LENGTH_SHORT).show();

                                                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        return ref1.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            Uri uri1 = task.getResult();
                                                            //      ref2.setValue(new DataRecord(key, now.toString(), uri1.toString()));

                                                            reference.update("url", uri1.toString());
                                                            //   documentReference.update("timestamp", firebase.firestore.ServerValue.TIMESTAMP )
                                                            finish();
                                                            Toast.makeText(AddApprovalActivity.this, "image Upload Succesful", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            reference.delete();
                                                            Log.e(task.getException() + "s", "this");
                                                            Toast.makeText(AddApprovalActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.e("Add Approval", "failed to add Approval");
                                    }
                                });
                                break;
                        }
                    }else {
                        Toast.makeText(this, "Data Incomplete", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }else  if ( requestCode == REQUEST_VOICE_CODE && resultCode == RESULT_OK && data != null){
            List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
            if (results != null && results.size() > 0) {
                name = "";
                for (int i=0; i<results.size() ; i++){
                    Log.d("Add Approval", "result: " + String.valueOf(results.get(i).toString()));
                    name +=  " " + results.get(i).toString();
                    tvName.setText(name);
                }
                Log.e("resutl name", name);
            }else {
                Log.e("Result", "Size 0");
            }

        }else if (requestCode == SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            String file = data.getStringExtra("file");

            if (file != null){
                Log.e("FILE", "is: " + file);
                FLAG = true;
                photoURI = Uri.fromFile(new File(file));
                imageView.setImageURI(photoURI);
                imageView.setColorFilter(null);
                imageView.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Image Saved to gallery", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "No Image Received, Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, REQUEST_VOICE_CODE);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}