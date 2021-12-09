package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.android.glass.glassdesign2.ManualActivity;
import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;
import com.squareup.picasso.Picasso;

public class LyoLayoutFragment extends BaseFragment{

    public static LyoLayoutFragment newInstance(double id, String step, String type, String title, String url, String desc) {

        final LyoLayoutFragment fragment =new LyoLayoutFragment();

        final Bundle args = new Bundle();
        args.putDouble("id", id);
        args.putString("step", step);
        args.putString("type", type);
        args.putString("title", title);
        args.putString("url", url);
        args.putString("desc", desc);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_final, container, false);
        if (getArguments() != null){
            final TextView tvTitle = view.findViewById(R.id.list_final_tvTitle);
            final TextView tvDesc = view.findViewById(R.id.list_final_tvDesc);
            final ImageView imageView = view.findViewById(R.id.list_final_imageview);
          //  final LinearLayout layout = view.findViewById(R.id.list_final_layout);
            final ImageView ivAction = view.findViewById(R.id.list_final_ivAction);
            final TextView tvHint = view.findViewById(R.id.list_final_tvHint);
            final ConstraintLayout layout1 = view.findViewById(R.id.list_final_layout_back);

            layout1.setBackgroundColor(SplashActivity.back_color);

            tvTitle.setText(getArguments().getString("title", ""));
            tvDesc.setText(getArguments().getString("desc", ""));
            if (!getArguments().getString("url").equals("")){
                Picasso.get().load("https:"+getArguments().getString("url")).into(imageView);
            }
            String type = getArguments().getString("type");
            switch (type){
                case "normal":
                    ivAction.setImageResource(R.drawable.baseline_swipe_24);
                    tvHint.setText("Swipe");
               //     tvStep.setBackgroundColor(getResources().getColor(R.color.color_green_light));
                    break;

                case "critical":
                    tvHint.setText("Tap or Swipe");
                    ivAction.setImageResource(R.drawable.ic_baseline_touch_app_24);
               //     tvStep.setBackgroundColor(getResources().getColor(R.color.color_red_light));

                    break;

                case "camera":
                    tvHint.setText("Tap or Swipe");
                    ivAction.setImageResource(R.drawable.baseline_swipe_24);
                    //    tvStep.setBackgroundColor(getResources().getColor(R.color.color_yellow_light));
                    break;

                case "info":
                    tvHint.setText("Swipe");
                    ivAction.setImageResource(R.drawable.baseline_swipe_24);
                    //      tvStep.setBackgroundColor(getResources().getColor(R.color.color_blue_light));
                    break;
            }
        /*    switch (BaseActivity.theme_code) {
                case 1:
                    ivIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;

                case 2:
                    ivIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);

                    break;
            }

         */
/*
            switch (SplashActivity.color_code){

                case 1:
                    tvStep.setBackground(getResources().getDrawable(R.drawable.back_circle_green));
                    break;

                case 2:
                    tvStep.setBackground(getResources().getDrawable(R.drawable.back_circle_red));
                    break;

                case 3:
                    tvStep.setBackground(getResources().getDrawable(R.drawable.back_circle_yellow));
                    break;
                case 4:
                    tvStep.setBackground(getResources().getDrawable(R.drawable.back_circle_blue));
                    break;
                case 5:
                    tvStep.setBackground(getResources().getDrawable(R.drawable.back_circle_orange));
                    break;
                case 6:
                    tvStep.setBackground(getResources().getDrawable(R.drawable.back_circle_purple));
                    break;

            }


 */


        }


        return view;
    }
}
