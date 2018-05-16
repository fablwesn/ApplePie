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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fablwesn.education.applepie.R;
import com.fablwesn.education.applepie.data.models.IngredientsModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsRecyclerAdapter extends RecyclerView.Adapter<IngredientsRecyclerAdapter.ViewHolder> {

    /* ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */
    private final List<IngredientsModel> ingredientsList;
    private final static String FLOATING_POINT_REDUCE_REGEX = "\\.?0*$";

    public IngredientsRecyclerAdapter(List<IngredientsModel> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredients_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Checking feed size to handle IndexOutOfBoundsException error
        if (position >= getItemCount())
            return;

        final IngredientsModel ingredient = ingredientsList.get(position);

        holder.nameText.setText(ingredient.getIngredient());
        holder.quantText.setText(String.valueOf(ingredient.getQuantity()).replaceAll(FLOATING_POINT_REDUCE_REGEX, ""));
        holder.unitText.setText(ingredient.getMeasure());
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    /* __________________________________________________________________________________________ */

    /* ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
      ViewHolder */

    // Single list item, get everything we need to display the results correctly
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredients_name)
        TextView nameText;
        @BindView(R.id.ingredients_quant)
        TextView quantText;
        @BindView(R.id.ingredients_unit)
        TextView unitText;

        ViewHolder(View listItem) {
            super(listItem);
            ButterKnife.bind(this, listItem);
        }
    }

    /* __________________________________________________________________________________________ */
}
