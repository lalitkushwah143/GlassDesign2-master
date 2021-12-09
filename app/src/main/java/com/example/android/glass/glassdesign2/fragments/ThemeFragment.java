package com.example.android.glass.glassdesign2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.glass.glassdesign2.R;

public class ThemeFragment extends BaseFragment{

    private TextView tvResponse;


    public static ThemeFragment newInstance(String title, int color_id) {

        final ThemeFragment fragment =new ThemeFragment();

        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("color_id", color_id);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.list_theme, container, false);
        if (getArguments() != null){
            final TextView textView = view.findViewById(R.id.list_theme_tv);
            final ImageView imageView = view.findViewById(R.id.list_theme_imageview);
            textView.setText(getArguments().getString("title"));

            switch (getArguments().getInt("color_id")){
                case 1:
                    textView.setTextColor(getResources().getColor(R.color.color_white));
                    imageView.setBackgroundColor(getResources().getColor(R.color.color_black));
                    break;

                case 2:
                    textView.setTextColor(getResources().getColor(R.color.color_black));
                    imageView.setBackgroundColor(getResources().getColor(R.color.color_white));
                    break;
            }

        }
        return view;
    }


}
