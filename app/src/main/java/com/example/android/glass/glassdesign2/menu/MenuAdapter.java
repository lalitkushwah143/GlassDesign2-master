/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.glass.glassdesign2.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.glass.glassdesign2.R;
import com.example.android.glass.glassdesign2.SplashActivity;
import com.example.android.glass.glassdesign2.databinding.MenuItemBinding;
import java.util.List;


/**
 * Adapter for the menu horizontal recycler view.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

  private final List<GlassMenuItem> menuItems;
  private final Context context;

  MenuAdapter(List<GlassMenuItem> menuItems, Context context) {
    this.menuItems = menuItems;
    this.context = context;
  }

  @NonNull
  @Override
  public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new MenuViewHolder(MenuItemBinding.inflate(
        LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int position) {
    menuViewHolder.bind(menuItems.get(position));
    setFadeAnimation(menuViewHolder.itemView);
    TextView textView = menuViewHolder.binding.textView;
    ImageView imageView = menuViewHolder.binding.imageView;
/*
    switch (SplashActivity.color_code){

      case 1:
        textView.setTextColor(context.getResources().getColor(R.color.design_green));
        imageView.setColorFilter(context.getResources().getColor(R.color.design_green));
        break;

      case 2:
        textView.setTextColor(context.getResources().getColor(R.color.design_red));
        imageView.setColorFilter(context.getResources().getColor(R.color.design_red));

        break;

      case 3:
        textView.setTextColor(context.getResources().getColor(R.color.design_yellow));
        imageView.setColorFilter(context.getResources().getColor(R.color.design_yellow));

        break;
      case 4:
        textView.setTextColor(context.getResources().getColor(R.color.design_blue));
        imageView.setColorFilter(context.getResources().getColor(R.color.design_blue));

        break;
      case 5:
        textView.setTextColor(context.getResources().getColor(R.color.design_orange));
        imageView.setColorFilter(context.getResources().getColor(R.color.design_orange));

        break;
      case 6:
        textView.setTextColor(context.getResources().getColor(R.color.design_purple));
        imageView.setColorFilter(context.getResources().getColor(R.color.design_purple));

        break;

    }

 */

  }

  @Override
  public int getItemCount() {
    return menuItems.size();
  }

  static class MenuViewHolder extends RecyclerView.ViewHolder {

    private MenuItemBinding binding;

    public MenuViewHolder(@NonNull MenuItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(GlassMenuItem glassMenuItem) {
      binding.setItem(glassMenuItem);
      binding.executePendingBindings();
    }
  }

  public void setFadeAnimation(View view) {
    AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
    anim.setDuration(500);
    view.startAnimation(anim);
  }
}
