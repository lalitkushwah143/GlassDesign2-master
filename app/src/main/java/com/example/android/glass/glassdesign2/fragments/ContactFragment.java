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

import com.example.android.glass.glassdesign2.ContactActivity;
import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;
import com.squareup.picasso.Picasso;

public class ContactFragment extends BaseFragment {

    private static final String TEXT_KEY = "text_key";
    private static final String FOOTER_KEY = "footer_key";
    private static final String TIMESTAMP_KEY = "timestamp_key";
    private static final String IMAGE_KEY = "image_key";
    private static final int BODY_TEXT_SIZE = 40;
    private TextView textView;
    private ImageView imageView;


    public static ContactFragment newInstance(String key, String name, String number, int url,
                                                 @Nullable Integer menu) {
        final ContactFragment myFragment = new ContactFragment();

        final Bundle args = new Bundle();
        args.putString(TEXT_KEY, key);
        args.putString(FOOTER_KEY, name);
        args.putString(TIMESTAMP_KEY, number);
        args.putInt(IMAGE_KEY, url);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_contact, container, false);

        if (getArguments() != null) {
            textView = view.findViewById(R.id.list_contact_tvName);
            imageView = view.findViewById(R.id.list_contact_imageview);

         //   Picasso.get().load(getArguments().getString(IMAGE_KEY)).into(imageView);
            imageView.setImageResource(getArguments().getInt(IMAGE_KEY));

            textView.setText(getArguments().getString(FOOTER_KEY));

            switch (SplashActivity.color_code){
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

    @Override
    public void onResume() {
        super.onResume();
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        textView.startAnimation(bottomUp);
        textView.setVisibility(View.VISIBLE);

        for (int i = 0; i< ContactActivity.num_all ; i++){
            if (i == ContactActivity.page_num){
                textView.setVisibility(View.VISIBLE);
            }else {
            }
        }

    }

}
