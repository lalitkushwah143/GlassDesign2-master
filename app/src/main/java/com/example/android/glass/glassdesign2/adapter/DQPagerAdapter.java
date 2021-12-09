package com.example.android.glass.glassdesign2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.viewpager.widget.PagerAdapter;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.data.DataModule;

import java.util.ArrayList;
import java.util.List;

public class DQPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<DataModule> mData;
    private List<TextView> mTexts;
    private List<TextView> mHints;
    private float mBaseElevation;
    private Context context;

    public DQPagerAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        mTexts = new ArrayList<>();
        mHints = new ArrayList<>();
    }

    public void addCardItem(DataModule item) {
        mViews.add(null);
        mTexts.add(null);
        mHints.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public TextView getTextViewAt(int position) {
        return mTexts.get(position);
    }

    @Override
    public ImageView getImageViewAt(int position) {
        return null;
    }

    @Override
    public TextView getHintAt(int position) {
        return mHints.get(position);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CardView cardView;
        TextView textView;
        TextView tvBottom;
        TextView tvModule;

        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.list_dq, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        cardView = (CardView) view.findViewById(R.id.list_dq_cardview);
        textView = (TextView) view.findViewById(R.id.list_dq_title);
        tvBottom = (TextView) view.findViewById(R.id.list_dq_tvBottom);
        tvModule = view.findViewById(R.id.list_dq_tvModule);
        ConstraintLayout layout = view.findViewById(R.id.list_dq_layout);
        tvModule.setText("Module "+ (position + 1));


       // cardView.setLayoutParams(new ConstraintLayout.LayoutParams(250, 250));
       // ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(250, 250);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(cardView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
        constraintSet.connect(cardView.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(cardView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        constraintSet.connect(cardView.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(layout);

        if (position == 0){
            Animation bottomUp = AnimationUtils.loadAnimation(context,
                    R.anim.bottom_up);
            tvBottom.startAnimation(bottomUp);
            textView.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        mTexts.set(position, textView);
        mHints.set(position, tvBottom);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
        mTexts.set(position, null);
        mHints.set(position, null);
    }

    private void bind(DataModule item, View view) {

        TextView titleTextView = (TextView) view.findViewById(R.id.list_dq_title);
        titleTextView.setText(item.getTitle());
    }


}
