package com.example.android.glass.glassdesign2.adapter;


import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);
    TextView getTextViewAt(int position);
    ImageView getImageViewAt(int position);
    TextView getHintAt(int position);

    int getCount();
}
