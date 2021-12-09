package com.example.android.glass.glassdesign2.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public interface CardAdapter2 {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);
    TextView getTextViewAt(int position);
    TextView getbottomViewAt(int position);

    int getCount();
}
