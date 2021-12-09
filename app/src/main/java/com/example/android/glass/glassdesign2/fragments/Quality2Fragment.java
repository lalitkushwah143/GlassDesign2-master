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

public class Quality2Fragment extends BaseFragment{

    public static Quality2Fragment newInstance(String desc, String req, String inst, String conn) {

        final Quality2Fragment fragment =new Quality2Fragment();

        final Bundle args = new Bundle();
        args.putString("desc", desc);
        args.putString("req", req);
        args.putString("inst", inst);
        args.putString("conn", conn);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_quality2, container, false);
        if (getArguments() != null){
            final TextView tvTitle = view.findViewById(R.id.list_quality2_tvTitle);
            final TextView tvPara = view.findViewById(R.id.list_quality2_tvPara);
            final TextView tvConn = view.findViewById(R.id.list_quality2_tvConn);
            final  TextView tvInst = view.findViewById(R.id.list_quality2_tvInst);
            final ConstraintLayout layout = view.findViewById(R.id.list_quality2_layout);

            layout.setBackgroundColor(SplashActivity.back_color);

            tvTitle.setText(getArguments().getString("desc", ""));
            tvPara.setText(getArguments().getString("req", ""));
            tvInst.setText(getArguments().getString("inst", ""));
            tvConn.setText(getArguments().getString("conn", ""));

        }
        return view;
    }
}
