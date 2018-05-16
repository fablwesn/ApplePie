/*
 * Copyright (C) 2018 Darijo Barucic, Seventoes
 *
 *  Licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fablwesn.education.applepie.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fablwesn.education.applepie.R;
import com.fablwesn.education.applepie.RecipeActivity;
import com.fablwesn.education.applepie.data.models.RecipeModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    /* ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */

    public final static String KEY_EXTRA_RECIPE = "recipe_selected";

    private final Activity activity;
    private final ArrayList<RecipeModel> recipes;

    public MainRecyclerAdapter(Activity activity, ArrayList<RecipeModel> recipes) {
        this.activity = activity;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Checking feed size to handle IndexOutOfBoundsException error
        if (position >= getItemCount())
            return;

        final RecipeModel recipeModel = recipes.get(position);

        String imageUrl = recipeModel.getImage();
        if(imageUrl.isEmpty()){
            holder.previewImage.setImageResource(R.drawable.ic_no_img);
        } else {
            Glide.with(activity).load(imageUrl).into(holder.previewImage);
        }

        holder.nameText.setText(recipeModel.getName());
        holder.servingsText.setText(activity.getString(R.string.list_servings, recipeModel.getServings()));

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RecipeActivity.class);
                intent.putExtra(KEY_EXTRA_RECIPE, recipeModel);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /* __________________________________________________________________________________________ */

    /* ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
      ViewHolder */

    // Single list item, get everything we need to display the results correctly
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_list_item_main)
        ImageView previewImage;
        @BindView(R.id.text_list_item_name_main)
        TextView nameText;
        @BindView(R.id.text_list_item_servings_main)
        TextView servingsText;
        @BindView(R.id.card_list_item_main)
        CardView parentView;

        ViewHolder(View listItem) {
            super(listItem);
            ButterKnife.bind(this, listItem);
        }
    }

    /* __________________________________________________________________________________________ */
}
