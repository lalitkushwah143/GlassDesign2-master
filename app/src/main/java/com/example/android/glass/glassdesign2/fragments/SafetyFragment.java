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

public class SafetyFragment extends BaseFragment{

    public static SafetyFragment newInstance(String key, String desc, String cause, String action, int index) {

        final SafetyFragment fragment =new SafetyFragment();

        final Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("desc", desc);
        args.putString("cause", cause);
        args.putString("action", action);
        args.putInt("index", index);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_safety, container, false);
        if (getArguments() != null){
            final TextView tvCause = view.findViewById(R.id.list_safety_tvCause);
            final TextView tvDesc = view.findViewById(R.id.list_safety_tvDesc);
            final TextView tvAction = view.findViewById(R.id.list_safety_tvAction);

            final ConstraintLayout layout = view.findViewById(R.id.list_safety_layout);

          //  layout.setBackgroundColor(SplashActivity.back_color);

            tvDesc.setText(getArguments().getString("desc", ""));
            tvCause.setText(getArguments().getString("cause", ""));
            tvAction.setText(getArguments().getString("action", ""));

        }
        return view;
    }


}
