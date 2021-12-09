package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;

public class DQFragment extends BaseFragment{

    public static DQFragment newInstance(int index, String key, String title) {

        final DQFragment fragment =new DQFragment();

        final Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("key", key);
        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_dq, container, false);
        if (getArguments() != null) {
            final TextView tvTitle = view.findViewById(R.id.list_dq_title);
            final  TextView tvModule = view.findViewById(R.id.list_dq_tvModule);
            final CardView cardView = view.findViewById(R.id.list_dq_cardview);
            final TextView tvBottom = view.findViewById(R.id.list_dq_tvBottom);
            ConstraintLayout layout = view.findViewById(R.id.list_dq_layout);


            cardView.setLayoutParams(new ConstraintLayout.LayoutParams(250, 250));
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(250, 250);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            constraintSet.connect(cardView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 20);
            constraintSet.connect(cardView.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 20);
            constraintSet.connect(cardView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 20);
            constraintSet.connect(cardView.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM, 20);
            constraintSet.applyTo(layout);

            tvModule.setText("Module " + (getArguments().getInt("index")+1));
            tvTitle.setText(getArguments().getString("title", ""));

            if (getArguments().getInt("index")==0){
                cardView.setScaleX(1.3f);
                cardView.setScaleY(1.3f);
                tvBottom.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.VISIBLE);
            }

      /*      switch (SplashActivity.color_code){

                case 1:
                    tvTitle.setTextColor(getResources().getColor(R.color.design_green));
                    break;

                case 2:
                    tvTitle.setTextColor(getResources().getColor(R.color.design_red));

                    break;

                case 3:
                    tvTitle.setTextColor(getResources().getColor(R.color.design_yellow));

                    break;
                case 4:
                    tvTitle.setTextColor(getResources().getColor(R.color.design_blue));

                    break;
                case 5:
                    tvTitle.setTextColor(getResources().getColor(R.color.design_orange));

                    break;
                case 6:
                    tvTitle.setTextColor(getResources().getColor(R.color.design_purple));

                    break;

            }

       */

        }

        return view;
    }


}
