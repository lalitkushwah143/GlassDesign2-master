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

public class JobFragment extends BaseFragment{

    public static JobFragment newInstance(String key, String title, String recipe_title, String desc, Boolean status) {

        final JobFragment fragment =new JobFragment();

        final Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("title", title);
        args.putString("recipe_title", recipe_title);
        args.putString("desc", desc);
        args.putBoolean("status", status);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_job, container, false);
        if (getArguments() != null){
            final TextView tvTitle = view.findViewById(R.id.list_job_tvTitle);
            final TextView tvDesc = view.findViewById(R.id.list_job_tvDesc);
            final TextView tvStatus = view.findViewById(R.id.list_job_tvStatus);
            final TextView tvRecipe = view.findViewById(R.id.list_job_tvRecipe);
            final ImageView imageView = view.findViewById(R.id.list_job_status);
            final ConstraintLayout layout = view.findViewById(R.id.list_job_layout);

            layout.setBackgroundColor(SplashActivity.back_color);

            tvTitle.setText(getArguments().getString("title", ""));
            tvDesc.setText(getArguments().getString("desc", ""));
            tvRecipe.setText(getArguments().getString("recipe_title", ""));
            if (getArguments().getBoolean("status")){
                tvStatus.setText("Completed");
                imageView.setColorFilter(getResources().getColor(R.color.color_normal));
                //imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_normal), android.graphics.PorterDuff.Mode.MULTIPLY);
            }else {
                tvStatus.setText("Not Completed");
                imageView.setColorFilter(getResources().getColor(R.color.color_critical));

                // imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_critical), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        return view;
    }
}
