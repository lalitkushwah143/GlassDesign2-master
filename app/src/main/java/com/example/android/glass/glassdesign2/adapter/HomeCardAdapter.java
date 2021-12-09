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
import com.example.android.glass.glassdesign2.data.DataHome;

import java.util.ArrayList;
import java.util.List;

public class HomeCardAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<DataHome> mData;
    private List<TextView> mTexts;
    private List<ImageView> mImages;
    private float mBaseElevation;
    private Context context;

    public HomeCardAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        mTexts = new ArrayList<>();
        mImages = new ArrayList<>();
    }

    public void addCardItem(DataHome item) {
        mViews.add(null);
        mTexts.add(null);
        mImages.add(null);
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
        return mImages.get(position);
    }

    @Override
    public TextView getHintAt(int position) {
        return null;
    }

    public DataHome getDataHome(int position){
        return mData.get(position);
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
        ImageView imageView;
        TextView textView;

                View view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.list_home, container, false);
                container.addView(view);
                bind(mData.get(position), view);
                cardView = (CardView) view.findViewById(R.id.list_home_cardview);
                textView = (TextView) view.findViewById(R.id.list_home_tvName);
                imageView = view.findViewById(R.id.list_home_imageview);
                ConstraintLayout layout = view.findViewById(R.id.list_home_layout);

      //  ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(250, 250);
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
                    textView.startAnimation(bottomUp);
                    textView.setVisibility(View.VISIBLE);
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color_black));
                }

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        mTexts.set(position, textView);
        mImages.set(position, imageView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
        mTexts.set(position, null);
        mImages.set(position, null);
    }

    private void bind(DataHome item, View view) {

        TextView titleTextView = (TextView) view.findViewById(R.id.list_home_tvName);
        ImageView imageView = view.findViewById(R.id.list_home_imageview);
        titleTextView.setText(item.getTitle());
        imageView.setImageResource(item.getImage());
    }

    public void empty(){
        mData.clear();
        mImages.clear();
        mTexts.clear();
        mViews.clear();
    }




}
