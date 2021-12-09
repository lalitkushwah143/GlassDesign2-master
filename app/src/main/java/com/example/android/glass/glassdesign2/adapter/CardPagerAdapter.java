package com.example.android.glass.glassdesign2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.data.DataUsers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<DataUsers> mData;
    private List<TextView> mTexts;
    private List<ImageView> mImages;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        mTexts = new ArrayList<>();
        mImages = new ArrayList<>();
    }

    public void addCardItem(DataUsers item) {
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
        View view;
        CardView cardView;
        ImageView imageView;
        TextView textView;
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.list_contact, container, false);
                container.addView(view);
                bind(mData.get(position), view);
                cardView = (CardView) view.findViewById(R.id.list_contact_cardview);
                textView = view.findViewById(R.id.list_contact_tvCall);
                imageView = view.findViewById(R.id.list_contact_ivCall);
                if (position == 0){
                    textView.setVisibility(View.VISIBLE);
                 //   imageView.setVisibility(View.VISIBLE);
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

    private void bind(DataUsers item, View view) {

                TextView titleTextView = (TextView) view.findViewById(R.id.list_contact_tvName);
                ImageView imageView = view.findViewById(R.id.list_contact_imageview);
                titleTextView.setText(item.getEmail());
                if (item.getUrl().equals("")){
                    imageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
                }else {
                    Picasso.get().load(item.getUrl()).into(imageView);
                }
    }
}
