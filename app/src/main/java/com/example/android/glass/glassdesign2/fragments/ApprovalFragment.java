package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

public class ApprovalFragment extends BaseFragment{

    public static ApprovalFragment newInstance(String key, String name, String date, String url) {

        final ApprovalFragment fragment =new ApprovalFragment();

        final Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("name", name);
        args.putString("date", date);
        args.putString("url", url);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_approval, container, false);
        if (getArguments() != null){
            final TextView tvName = view.findViewById(R.id.list_approval_tvName);
            final TextView tvDate = view.findViewById(R.id.list_approval_tvDate);
            final ImageView imageView = view.findViewById(R.id.list_approval_imageview);


            tvName.setText("Name: " + getArguments().getString("name", ""));
            tvDate.setText("Date: " + getArguments().getString("date", ""));

            if (!TextUtils.isEmpty(getArguments().getString("url"))){
              //  Picasso.get().load(getArguments().getString("url")).into(imageView);
                Glide.with(this).load(getArguments().getString("url")).into(imageView);

            }
        }
        return view;
    }


}
