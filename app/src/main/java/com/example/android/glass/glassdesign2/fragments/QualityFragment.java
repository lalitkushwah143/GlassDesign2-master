package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;

public class QualityFragment extends BaseFragment{

    public static QualityFragment newInstance(String title, String value) {

        final QualityFragment fragment =new QualityFragment();

        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("value", value);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_quality, container, false);
        if (getArguments() != null){
            final TextView tvTitle = view.findViewById(R.id.list_quality_tvTitle);
            final TextView tvPara = view.findViewById(R.id.list_quality_tvPara);
            final ConstraintLayout layout = view.findViewById(R.id.list_quality_layout);

            layout.setBackgroundColor(SplashActivity.back_color);

            tvTitle.setText(getArguments().getString("title", ""));
            tvPara.setText("Expected Value:  " + getArguments().getString("value", ""));

        }
        return view;
    }
}
