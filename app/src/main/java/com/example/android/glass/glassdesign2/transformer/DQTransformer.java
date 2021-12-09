package com.example.android.glass.glassdesign2.transformer;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;
import com.example.android.glass.glassdesign2.adapter.CardAdapter;
import com.example.android.glass.glassdesign2.adapter.CardAdapter2;

public class DQTransformer implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;
    private Context context;

    public DQTransformer(Context context, ViewPager viewPager, CardAdapter adapter) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
        this.context = context;

    }

    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            // shrink main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        }else if(!mScalingEnabled && enable){
            // grow main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }

        mScalingEnabled = enable;
    }

    @Override
    public void transformPage(View page, float position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = mAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft = mLastOffset > positionOffset;

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }

        // Avoid crash on overscroll
        if (nextPosition > mAdapter.getCount() - 1
                || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }

        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (mScalingEnabled) {
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));

            }
            currentCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
        }

        CardView nextCard = mAdapter.getCardViewAt(nextPosition);
        TextView nextTextView = mAdapter.getTextViewAt(nextPosition);
        TextView nextHint = mAdapter.getHintAt(nextPosition);


        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
                nextTextView.setVisibility(View.VISIBLE);
                Animation bottomUp = AnimationUtils.loadAnimation(context,
                        R.anim.bottom_up);
                nextHint.startAnimation(bottomUp);
                nextHint.setVisibility(View.VISIBLE);

            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
/*
        int count =mAdapter.getCount() -1;

        if (position == 0){
            TextView textView = mAdapter.getTextViewAt(position);
            TextView tvBottom = mAdapter.getHintAt(position);
            CardView cardView = mAdapter.getCardViewAt(position);
            Animation bottomUp = AnimationUtils.loadAnimation(context,
                    R.anim.bottom_up);
            tvBottom.startAnimation(bottomUp);
            textView.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
            for (int i = 1; i < 3; i++){
                TextView textView1 = mAdapter.getTextViewAt(i);
                CardView cardView1 = mAdapter.getCardViewAt(i);
                TextView tvBottom1 = mAdapter.getHintAt(i);
                tvBottom1.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);

            }
        }else if (position == count){
            TextView textView = mAdapter.getTextViewAt(position);
            TextView tvBottom = mAdapter.getHintAt(position);
            CardView cardView = mAdapter.getCardViewAt(position);
            Animation bottomUp = AnimationUtils.loadAnimation(context,
                    R.anim.bottom_up);
            tvBottom.startAnimation(bottomUp);
            textView.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.VISIBLE);

            for (int i = count-1; i > count - 3; i--){
                TextView textView1 = mAdapter.getTextViewAt(i);
                CardView cardView1 = mAdapter.getCardViewAt(i);
                TextView tvBottom1 = mAdapter.getHintAt(i);
                tvBottom1.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);

            }
        }else {
            for (int i=position -1; i<position+2; i++){
                TextView textView = mAdapter.getTextViewAt(i);
                TextView tvBottom = mAdapter.getHintAt(i);
                CardView cardView = mAdapter.getCardViewAt(i);
                if (i == position){
                    Animation bottomUp = AnimationUtils.loadAnimation(context,
                            R.anim.bottom_up);
                    tvBottom.startAnimation(bottomUp);
                    textView.setVisibility(View.VISIBLE);
                    tvBottom.setVisibility(View.VISIBLE);

                }else {
                    textView.setVisibility(View.GONE);
                    tvBottom.setVisibility(View.GONE);

                }
            }
        }

 */
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
