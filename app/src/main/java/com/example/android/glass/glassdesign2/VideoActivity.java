package com.example.android.glass.glassdesign2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.glass.ui.GlassGestureDetector;

public class VideoActivity extends BaseActivity {

    private VideoView videoView;
    private Uri uri;
    private Boolean FLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.activity_video_view);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                if (FLAG){
                    Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
                    videoView.pause();
                    FLAG = false;
                }else {
                    Toast.makeText(this, "Resumed", Toast.LENGTH_SHORT).show();
                    videoView.start();
                    FLAG = true;
                }
                break;
        }
        return super.onGesture(gesture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void initializePlayer() {
        uri = Uri.parse("android.resource://" + getPackageName() +
                "/raw/" + "video");

        videoView.setVideoURI(uri);
        videoView.start();
        FLAG = true;
    }

    private void releasePlayer() {
        videoView.stopPlayback();
        FLAG = false;
    }
}