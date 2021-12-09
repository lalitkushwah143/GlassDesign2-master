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

public class SpecFragment extends BaseFragment{

    public static SpecFragment newInstance(String key, String title, String input) {

        final SpecFragment fragment =new SpecFragment();

        final Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("title", title);
        args.putString("input", input);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_spec, container, false);
        if (getArguments() != null){
            final TextView tvTitle = view.findViewById(R.id.list_spec_tvTitle);
            final TextView tvInput = view.findViewById(R.id.list_spec_tvInput);

          //  final ConstraintLayout layout = view.findViewById(R.id.list_single_layout);

          //  layout.setBackgroundColor(SplashActivity.back_color);

            tvTitle.setText(getArguments().getString("title", ""));
            if (getArguments().getString("input", "").equals("")){
                tvInput.setText("No Input");
            }else {
                tvInput.setText(getArguments().getString("input", ""));
            }

        }
        return view;
    }

}
