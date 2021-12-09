package com.example.android.glass.glassdesign2;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.glass.ui.GlassGestureDetector;

import java.io.IOException;

public class MediaActivity extends BaseActivity {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private VideoView videoView;
    private ImageView imageView;
    private TextView textView;
    private String url, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        videoView = findViewById(R.id.activity_media_videoView);
        imageView = findViewById(R.id.activity_media_imageview);
        textView = findViewById(R.id.activity_media_tv);

        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");

        if (url == null || type == null ){
            finish();
        }

        switch (type){
            case "audio":
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                } else {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            textView.setText("Playing");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }

                break;

            case "video":
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);

                if (videoView.isPlaying()){
                    videoView.stopPlayback();
                    videoView.setVisibility(View.GONE);
                }else {
                    videoView.setVideoPath(url);
                    videoView.start();
                    textView.setText("Playing");
                    //  MediaController mediaController = new MediaController(this);
                    // mediaController.setMediaPlayer(videoView);
                    //  videoView.setMediaController(mediaController);
                    videoView.requestFocus();
                }
                break;

            default:
                // Do nothing
                break;
        }
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {

        switch (gesture){
            case TAP:
                switch (type){
                    case "audio":
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                            textView.setText("Paused");
                        }else {
                            mediaPlayer.start();
                            textView.setText("Playing");
                        }
                        break;

                    case "video":
                        if (videoView.isPlaying()){
                            videoView.pause();
                            textView.setText("Paused");

                        }else {
                            videoView.start();
                            textView.setText("Playing");
                        }
                        break;

                    default:

                        break;
                }

                return true;

            case SWIPE_FORWARD:

                switch (type){
                    case "audio":
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                            Toast.makeText(this, "Forward 5s", Toast.LENGTH_SHORT).show();

                        }
                        break;

                    case "video":
                        if (videoView.isPlaying()){
                            videoView.seekTo(videoView.getCurrentPosition() + 5000);
                            Toast.makeText(this, "Forward 5s", Toast.LENGTH_SHORT).show();

                        }
                        break;

                    default:

                        break;
                }

                return true;


            case SWIPE_BACKWARD:

                switch (type){
                    case "audio":
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.seekTo(Math.max((mediaPlayer.getCurrentPosition() - 5000), 0));
                            Toast.makeText(this, "Backward 5s", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "video":
                        if (videoView.isPlaying()){
                            videoView.seekTo(Math.max(videoView.getCurrentPosition() - 5000, 0));
                            Toast.makeText(this, "Backward 5s", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:

                        break;
                }

                return true;


            case SWIPE_DOWN:

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                if (videoView.isPlaying()){
                    videoView.stopPlayback();
                }

                finish();

                return true;


            default:

                return super.onGesture(gesture);
        }


    }
}