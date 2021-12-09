package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.glass.glassdesign2.R;

public class MachineFragment extends BaseFragment {


    private static final int BODY_TEXT_SIZE = 40;
    private TextView textView;

    public static MachineFragment newInstance(String key, String title, String desc, String location) {
        final MachineFragment myFragment = new MachineFragment();

        final Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("title", title);
        args.putString("desc", desc );
        args.putString("location", location);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_machine, container, false);


        if (getArguments() != null) {
            final TextView tvTitle = view.findViewById(R.id.list_machine_tvTitle);
            final TextView tvDesc = view.findViewById(R.id.list_machine_tvDesc);
            final TextView tvLocation = view.findViewById(R.id.list_machine_tvLocation);

            tvTitle.setText("Title : " + getArguments().getString("title"));
            tvDesc.setText("Description : " + getArguments().getString("desc").trim());
            tvLocation.setText("Location : " + getArguments().getString("location").trim());


        }
        return view;
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        textView.startAnimation(bottomUp);
        textView.setVisibility(View.VISIBLE);
    }



    @Override
    public void onPause() {
        super.onPause();
        textView.setVisibility(View.GONE);
    }

 */
}
