package com.example.android.glass.glassdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.glass.glassdesign2.data.DataIssue;
import com.example.android.glass.glassdesign2.menu.MenuActivity;
import com.example.glass.ui.GlassGestureDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentActivity extends BaseActivity {

    private TextView tvComment;
    private static final int REQUEST_VOICE_CODE = 998;
    private String resultText = "";
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private String doc_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        tvComment = findViewById(R.id.activity_comment_tvComment);

        firestore = FirebaseFirestore.getInstance();

        doc_id = getIntent().getStringExtra("reference");
        if (doc_id == null){
            finish();
        }

    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                requestVoiceRecognition();
                return true;

            case SWIPE_FORWARD:

                resultText = "";
                tvComment.setText(resultText);
                Toast.makeText(this, "Comment Cleared", Toast.LENGTH_SHORT).show();
                return true;

            case SWIPE_DOWN:

                documentReference = firestore.collection("issueData").document(doc_id);
                documentReference.update("content", resultText)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CommentActivity.this, "updated the Comment", Toast.LENGTH_SHORT).show();
                                Log.e( "TAG" , "Updated the Comment");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                Toast.makeText(CommentActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "Update Failed");
                                finish();
                            }
                        });

                return true;

            default:

                return super.onGesture(gesture);
        }

    }

    private void requestVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.startActivityForResult(intent, this.REQUEST_VOICE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK && data != null){
                List results = (List)data.getStringArrayListExtra("android.speech.extra.RESULTS");
                if (results != null && results.size() > 0) {
                    for (int i=0; i<results.size() ; i++){
                        Log.d("CommentActivity", "result: " + String.valueOf(results.get(i).toString()));
                        resultText +=  " " + results.get(i).toString();
                        tvComment.setText(resultText);
                    }
                    Log.e("Resulttext", resultText);
                }else {
                    Log.e("Result", "Size 0");
                }

        } else {
            Log.d("VoiceActivity", "Result not OK");
        }
    }
}