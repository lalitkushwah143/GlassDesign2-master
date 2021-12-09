package com.example.android.glass.glassdesign2.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.glass.glassdesign2.BaseActivity;
import com.example.android.glass.glassdesign2.R;
import com.squareup.picasso.Picasso;

public class NewReportFragment extends BaseFragment{

    public static NewReportFragment newInstance(int index, String type, String title, String format, String url, String desc) {

        final NewReportFragment fragment =new NewReportFragment();

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


           // MediaPlayer mPlayer = new MediaPlayer();


        }

        return view;
    }
}
