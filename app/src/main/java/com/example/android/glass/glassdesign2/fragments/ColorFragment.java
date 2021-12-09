package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.glass.glassdesign2.R;

public class ColorFragment extends BaseFragment{

    private TextView tvResponse;


    public static ColorFragment newInstance(String title, int color_id) {

        final ColorFragment fragment =new ColorFragment();

        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("color_id", color_id);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_color, container, false);
        if (getArguments() != null){
            final TextView textView = view.findViewById(R.id.list_color_textview);
            textView.setText(getArguments().getString("title"));

            switch (getArguments().getInt("color_id")){
                case 1:
                    textView.setTextColor(getResources().getColor(R.color.design_green));
                    break;

                case 2:
                    textView.setTextColor(getResources().getColor(R.color.design_red));
                    break;
                case 3:
                    textView.setTextColor(getResources().getColor(R.color.design_yellow));
                    break;

                case 4:
                    textView.setTextColor(getResources().getColor(R.color.design_blue));
                    break;
                case 5:
                    textView.setTextColor(getResources().getColor(R.color.design_orange));
                    break;

                case 6:
                    textView.setTextColor(getResources().getColor(R.color.design_purple));
                    break;

            }

        }
        return view;
    }




}
