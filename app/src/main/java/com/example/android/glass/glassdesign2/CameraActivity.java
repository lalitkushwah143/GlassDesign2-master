package com.example.android.glass.glassdesign2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.android.glass.glassdesign2.data.DataBatch;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 128;

    private ImageView imageView, ivIcon;
    private StorageReference storageReference, storageReference1;
    private Date now = new Date();
    String currentPhotoPath;
    private Uri uri, photoURI;
    private int step;
    private Boolean FLAG = false;
    private TextView textView;
    private String manual_id, manual_name, machine_id;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.activity_camera_imageview);
        textView = findViewById(R.id.activity_camera_tv);
        ivIcon = findViewById(R.id.activity_camera_logo);
        textView.setText("Tap to open Camera");
        FLAG = false;

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        step = getIntent().getIntExtra("step", 0);
        manual_id = getIntent().getStringExtra("manual_id");
        manual_name = getIntent().getStringExtra("manual_name");
/*
        switch (BaseActivity.theme_code) {
            case 1:
                ivIcon.setColorFilter(ContextCompat.getColor(this, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                imageView.setColorFilter(ContextCompat.getColor(this, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;

            case 2:
                ivIcon.setColorFilter(ContextCompat.getColor(this, R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);
                imageView.setColorFilter(ContextCompat.getColor(this, R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
        }


 */
        storageReference = FirebaseStorage.getInstance().getReference();

        if (SplashActivity.machine_id_temp != null && !SplashActivity.machine_id_temp.equals("")) {
            machine_id = SplashActivity.machine_id_temp;
        }else {
            machine_id = SplashActivity.machine_id;
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                if (!FLAG){
                   // dispatchTakePictureIntent();
                    if (checkCameraHardware(CameraActivity.this)){
                        Intent intent = new Intent(CameraActivity.this, Camera2Activity.class);
                        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                    }else {
                        Toast.makeText(CameraActivity.this, "No camera", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                    Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
                    Log.e("CameraActivity", manual_name);


                        if (photoURI != null && now != null && manual_id != null && machine_id != null && firebaseAuth.getCurrentUser() != null && manual_name!= null) {

                        firestore.collection("batchReport")
                                .add(new DataBatch(firebaseAuth.getCurrentUser().getEmail(), SplashActivity.machine_id,
                                        manual_id, "", Timestamp.now(), manual_name))
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String key = documentReference.getId();
                                        storageReference1 = storageReference.child("batch").child(key);

                                        UploadTask uploadTask = storageReference1.putFile(photoURI);
                                        Toast.makeText(CameraActivity.this, "Uploading", Toast.LENGTH_SHORT).show();

                                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return storageReference1.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri uri1 = task.getResult();
                                                    //      ref2.setValue(new DataRecord(key, now.toString(), uri1.toString()));

                                                    documentReference.update("url", uri1.toString());
                                                 //   documentReference.update("timestamp", Timestamp.now());
                                                 //   documentReference.update("timestamp", firebase.firestore.ServerValue.TIMESTAMP )

                                                    for ( int i = 0; i< ViewManualActivity.dataChecks.size(); i++){
                                                        if (ViewManualActivity.dataChecks.get(i).getIndex() == step-1){
                                                            ViewManualActivity.dataChecks.get(i).setStatus(true);
                                                            ViewManualActivity.viewPager.setPagingEnabled(true);
                                                            break;
                                                        }
                                                    }
                                                    finish();
                                                    Toast.makeText(CameraActivity.this, "image Upload Succesful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    documentReference.delete();
                                                    Log.e(task.getException() + "s", "this");
                                                    Toast.makeText(CameraActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CameraActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        Log.e("CameraActivity", e.toString());
                                    }
                                });


                    }else{
                        Toast.makeText(this, "No image", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            default:
                return super.onGesture(gesture);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && photoURI != null) {
            FLAG = true;
            imageView.setImageURI(photoURI);
            imageView.setColorFilter(null);
            imageView.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.upload_tip));
            Toast.makeText(this, "Image Saved to gallery", Toast.LENGTH_SHORT).show();
        }else if (requestCode == SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            String file = data.getStringExtra("file");

            if (file != null){
                Log.e("FILE", "is: " + file);
                FLAG = true;
                photoURI = Uri.fromFile(new File(file));
                imageView.setImageURI(photoURI);
                imageView.setColorFilter(null);
                imageView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.upload_tip));
                Toast.makeText(this, "Image Saved to gallery", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "No Image Received, Try Again", Toast.LENGTH_SHORT).show();
            }


        }else {
            Toast.makeText(this, "No Image Captured", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.e("Error Creating File" , "");
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.glass.glassdesign2.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                List<String> packages = new ArrayList<>();
                List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(takePictureIntent, 0);
                for (ResolveInfo resolveInfo : resolveInfoList){
                    packages.add(resolveInfo.activityInfo.packageName);
                    Log.e("Packages: ", resolveInfo.activityInfo.packageName.toString()+"");
                }
                takePictureIntent.setPackage(packages.get(0));

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}