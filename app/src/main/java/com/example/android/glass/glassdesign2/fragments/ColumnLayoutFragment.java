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
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.android.glass.glassdesign2.R;
import com.squareup.picasso.Picasso;

/**
 * Fragment with the two column layout.
 */
public class ColumnLayoutFragment extends BaseFragment {

  private static final String IMAGE_KEY = "image_key";
  private static final String TEXT_KEY = "text_key";
  private static final String FOOTER_KEY = "footer_key";
  private static final String TIMESTAMP_KEY = "timestamp_key";
  private static final int TEXT_SIZE = 30;
  private static final int IMAGE_PADDING = 40;

  /**
   * Returns new instance of {@link ColumnLayoutFragment}.
   *
   * @param image is a android image resource to create a imageView on the left column.
   * @param text is a String with the card main text.
   * @param footer is a String with the card footer text.
   * @param timestamp is a String with the card timestamp text.
   */
  public static ColumnLayoutFragment newInstance(String image, String text, String footer,
      String timestamp) {
    final ColumnLayoutFragment myFragment = new ColumnLayoutFragment();

    final Bundle args = new Bundle();
    args.putString(IMAGE_KEY, image);
    args.putString(TEXT_KEY, text);
    args.putString(FOOTER_KEY, footer);
    args.putString(TIMESTAMP_KEY, timestamp);
    myFragment.setArguments(args);

    return myFragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.list_manual, container, false);

    if (getArguments() != null) {

      final TextView tvDesc= view.findViewById(R.id.list_manual_tvDesc);
      final TextView tvStep= view.findViewById(R.id.list_manual_tvStep);
      final ImageView iv=view.findViewById(R.id.list_manual_imageview);

      tvDesc.setText(getArguments().getString(TEXT_KEY, getString(R.string.empty_string)));
      tvStep.setText(getArguments().getString(FOOTER_KEY, getString(R.string.empty_string)));
      Picasso.get().load("https:" + getArguments().getString(IMAGE_KEY)).into(iv);
    }
    return view;
  }
}
