package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.android.glass.glassdesign2.R;

public class BatchFragment extends BaseFragment{

    public static BatchFragment newInstance(int index, String key, String title) {

        final BatchFragment fragment =new BatchFragment();

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

        final View view = inflater.inflate(R.layout.list_monitor, container, false);
        if (getArguments() != null) {
            final TextView tvTitle = view.findViewById(R.id.list_monitor_title);
            final CardView cardView = view.findViewById(R.id.list_monitor_cardview);
            final TextView tvBottom = view.findViewById(R.id.list_monitor_tvBottom);

            tvTitle.setText(getArguments().getString("title", ""));

            if (getArguments().getInt("index")==0){
                cardView.setScaleX(1.3f);
                cardView.setScaleY(1.3f);
                tvBottom.setVisibility(View.VISIBLE);
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
