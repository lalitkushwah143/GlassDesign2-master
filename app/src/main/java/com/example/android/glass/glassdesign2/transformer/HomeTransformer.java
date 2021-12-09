package com.example.android.glass.glassdesign2.transformer;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.example.android.glass.glassdesign2.adapter.CardAdapter;
import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;

public class HomeTransformer implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;
    private Context context;

    public HomeTransformer(Context context, ViewPager viewPager, CardAdapter adapter) {
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


        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));

            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {

        int count =mAdapter.getCount() -1;

                if (position == 0){
                    TextView textView = mAdapter.getTextViewAt(position);
                    CardView cardView = mAdapter.getCardViewAt(position);
                    Animation bottomUp = AnimationUtils.loadAnimation(context,
                            R.anim.bottom_up);
                    textView.startAnimation(bottomUp);
                    textView.setVisibility(View.VISIBLE);
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color_black));
                    for (int i = 1; i < 3; i++){
                        TextView textView1 = mAdapter.getTextViewAt(i);
                        CardView cardView1 = mAdapter.getCardViewAt(i);
                        if (textView1!= null && cardView1!= null) {
                            textView1.setVisibility(View.GONE);
                            cardView1.setCardBackgroundColor(SplashActivity.other_color);
                        }

                    }
                }else if (position == count){
                    TextView textView = mAdapter.getTextViewAt(position);
                    CardView cardView = mAdapter.getCardViewAt(position);
                    Animation bottomUp = AnimationUtils.loadAnimation(context,
                            R.anim.bottom_up);
                    textView.startAnimation(bottomUp);
                    textView.setVisibility(View.VISIBLE);
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color_black));

                    for (int i = count-1; i > count - 3; i--){
                        TextView textView1 = mAdapter.getTextViewAt(i);
                        CardView cardView1 = mAdapter.getCardViewAt(i);

                        if (textView1!= null && cardView1!= null) {
                            textView1.setVisibility(View.GONE);
                            cardView1.setCardBackgroundColor(SplashActivity.other_color);
                        }

                    }
                }else {
                    for (int i=position -1; i<position+2; i++){
                        TextView textView = mAdapter.getTextViewAt(i);
                        CardView cardView = mAdapter.getCardViewAt(i);
                        if (textView!= null && cardView!= null) {
                            if (i == position) {
                                Animation bottomUp = AnimationUtils.loadAnimation(context,
                                        R.anim.bottom_up);
                                textView.startAnimation(bottomUp);
                                textView.setVisibility(View.VISIBLE);
                                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color_black));

                            } else {
                                textView.setVisibility(View.GONE);
                                cardView.setCardBackgroundColor(SplashActivity.other_color);

                            }
                        }
                    }
                }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
