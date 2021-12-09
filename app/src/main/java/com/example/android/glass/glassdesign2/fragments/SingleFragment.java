package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;

public class SingleFragment extends BaseFragment{

    public static SingleFragment newInstance(String key, String title) {

        final SingleFragment fragment =new SingleFragment();

        final Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_single, container, false);
        if (getArguments() != null){
            final TextView tvTitle = view.findViewById(R.id.list_single_tvTitle);

            final ConstraintLayout layout = view.findViewById(R.id.list_single_layout);

            layout.setBackgroundColor(SplashActivity.back_color);

            tvTitle.setText(getArguments().getString("title", ""));
        }
        return view;
    }

}
