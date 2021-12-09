/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;

/**
 * Fragment with the main card layout.
 */
public class MainLayoutFragment extends BaseFragment {

  private static final String TEXT_KEY = "text_key";
  private static final String FOOTER_KEY = "footer_key";
  private static final String TIMESTAMP_KEY = "timestamp_key";
  private static final String IMAGE_KEY = "image_key";
  private static final int BODY_TEXT_SIZE = 40;
  private TextView textView;

  /**
   * Returns new instance of {@link MainLayoutFragment}.
   *
   * @param text is a String with the card main text.
   * @param footer is a String with the card footer text.
   * @param timestamp is a String with the card timestamp text.
   */
  public static MainLayoutFragment newInstance(String text, String footer, String timestamp, int image,
      @Nullable Integer menu) {
    final MainLayoutFragment myFragment = new MainLayoutFragment();

    final Bundle args = new Bundle();
    args.putString(TEXT_KEY, text);
    args.putString(FOOTER_KEY, footer);
    args.putString(TIMESTAMP_KEY, timestamp);
    args.putInt(IMAGE_KEY, image);
    if (menu != null) {
      args.putInt(MENU_KEY, menu);
    }
    myFragment.setArguments(args);

    return myFragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.list_main, container, false);

   /* switch (SplashActivity.theme_code){
      case  1:
        getActivity().setTheme(R.style.MenuTheme);
        break;

      case 2:
        getActivity().setTheme(R.style.MenuThemeWhite);
        break;
    }

    */

    if (getArguments() != null) {
      textView = view.findViewById(R.id.list_main_textview);
      ImageView imageView = view.findViewById(R.id.list_main_imageview);

      imageView.setImageResource(getArguments().getInt(IMAGE_KEY));


      textView.setText(getArguments().getString(TEXT_KEY));
  /*    switch (BaseActivity.theme_code) {
        case 1:
          imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
          break;

        case 2:
          imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_black), android.graphics.PorterDuff.Mode.MULTIPLY);

          break;
      }

   */
      /*
      switch (SplashActivity.color_code){
        case 1:
          textView.setTextColor(getResources().getColor(R.color.design_green));
          imageView.setColorFilter(getResources().getColor(R.color.design_green));
          break;

        case 2:
          textView.setTextColor(getResources().getColor(R.color.design_red));
          imageView.setColorFilter(getResources().getColor(R.color.design_red));

          break;

        case 3:
          textView.setTextColor(getResources().getColor(R.color.design_yellow));
          imageView.setColorFilter(getResources().getColor(R.color.design_yellow));

          break;
        case 4:
          textView.setTextColor(getResources().getColor(R.color.design_blue));
          imageView.setColorFilter(getResources().getColor(R.color.design_blue));

          break;
        case 5:
          textView.setTextColor(getResources().getColor(R.color.design_orange));
          imageView.setColorFilter(getResources().getColor(R.color.design_orange));

          break;
        case 6:
          textView.setTextColor(getResources().getColor(R.color.design_purple));
          imageView.setColorFilter(getResources().getColor(R.color.design_purple));

          break;
      }

       */
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
  }

  @Override
  public void onPause() {
    super.onPause();
    textView.setVisibility(View.GONE);
  }
}
