package com.example.android.glass.glassdesign2.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.glass.glassdesign2.BaseActivity;
import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.ViewManualActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NewLyoFragment extends BaseFragment{

    public static NewLyoFragment newInstance(int index, String type, String title, String format, String url, String desc) {

        final NewLyoFragment fragment =new NewLyoFragment();

        final Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("type", type);
        args.putString("title", title);
        args.putString("format", format);
        args.putString("url", url);
        args.putString("desc", desc);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_final_new, container, false);
        if (getArguments() != null){
            final TextView tvStep = view.findViewById(R.id.list_final_new_tvStep);
            final TextView tvTitle = view.findViewById(R.id.list_final_new_tvTitle);
            final ImageView ivIcon = view.findViewById(R.id.list_final_new_ivIcon);
            final TextView tvDesc = view.findViewById(R.id.list_final_new_tvDesc);
            final ImageView imageView = view.findViewById(R.id.list_final_new_imageview);
            final LinearLayout layout = view.findViewById(R.id.list_final_new_layout);
            final TextView tvAudio = view.findViewById(R.id.list_final_new_tvAudio);
            final TextView tvVideo = view.findViewById(R.id.list_final_new_tvVideo);

            tvStep.setText((getArguments().getInt("index") + 1) + "");
            tvTitle.setText(getArguments().getString("desc", ""));
        //    tvDesc.setText(getArguments().getString("desc", ""));
            if (!getArguments().getString("url").equals("")){
                Picasso.get().load(getArguments().getString("url")).into(imageView);
            }

            switch (getArguments().getString("format")){
                case "image":
                    imageView.setVisibility(View.VISIBLE);
                    tvAudio.setVisibility(View.GONE);
                    tvVideo.setVisibility(View.GONE);
                    if (!getArguments().getString("url").equals("")){
                        Picasso.get().load(getArguments().getString("url")).into(imageView);
                    }
                    break;

                case "video":
                    imageView.setVisibility(View.VISIBLE);
                    tvAudio.setVisibility(View.GONE);
                    tvVideo.setVisibility(View.VISIBLE);

                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_movie_24));

                    long thumb = 2000;
                    RequestOptions options = new RequestOptions().frame(thumb);
                    Glide.with(this).load(getArguments().getString("url")).apply(options).into(imageView);

                 /*   if (!getArguments().getString("url").equals("")){
                        videoView.setVideoPath(getArguments().getString("url"));
                          videoView.start();
                        MediaController mediaController = new MediaController(this.getContext().getApplicationContext());
                        mediaController.setMediaPlayer(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.requestFocus();
                    }

                  */
                    break;

                case "audio":
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_audiotrack_24));
                    tvAudio.setVisibility(View.VISIBLE);
                    tvVideo.setVisibility(View.GONE);
                    break;

            }

            MediaPlayer mPlayer = new MediaPlayer();

            tvAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*

                    if (mPlayer.isPlaying()){
                        mPlayer.stop();
                    }else {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        if (!getArguments().getString("url").equals("")){
                            try {
                                mPlayer.setDataSource(getArguments().getString("url"));
                                mPlayer.prepare();
                                mPlayer.start();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

 */
                  /*  MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(list.get(position).getUrl());
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                   */
                }
            });

            String type = getArguments().getString("type");
            switch (type){
                case "normal":
                    ivIcon.setImageResource(R.drawable.ic_baseline_input_24);
                    tvStep.setBackgroundColor(getResources().getColor(R.color.color_green_light));
                    break;

                case "critical":
                    ivIcon.setImageResource(R.drawable.ic_baseline_report_problem_24);
                    tvStep.setBackgroundColor(getResources().getColor(R.color.color_red_light));
                    break;

                case "camera":
                    ivIcon.setImageResource(R.drawable.ic_outline_camera_alt_24);
                    tvStep.setBackgroundColor(getResources().getColor(R.color.color_yellow_light));
                    break;

                case "info":
                    ivIcon.setImageResource(R.drawable.ic_baseline_info_24);
                    tvStep.setBackgroundColor(getResources().getColor(R.color.color_blue_light));
                    break;
            }
            switch (BaseActivity.theme_code) {
                case 1:
                    ivIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;

                case 2:
                    ivIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);

                    break;
            }

        }

        return view;
    }
}
