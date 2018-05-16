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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fablwesn.education.applepie.DetailsActivity;
import com.fablwesn.education.applepie.R;
import com.fablwesn.education.applepie.data.models.StepsModel;
import com.fablwesn.education.applepie.fragments.DetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsRecyclerAdapter extends RecyclerView.Adapter<StepsRecyclerAdapter.ViewHolder> {

    /* ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */

    public final static String KEY_EXTRA_STEP = "step_selected";
    private final List<StepsModel> stepsList;
    private Activity activity;
    private FragmentManager fragmentManager;

    public StepsRecyclerAdapter(Activity activity, List<StepsModel> stepsList, FragmentManager fragmentManager) {
        this.stepsList = stepsList;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.steps_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // Checking feed size to handle IndexOutOfBoundsException error
        if (position >= getItemCount())
            return;

        final StepsModel step = stepsList.get(position);

        holder.numberText.setText(activity.getString(R.string.steps_count_seperator, position +1));
        holder.titleText.setText(step.getShortDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.getResources().getBoolean(R.bool.isSw600dp)){
                    Intent intent = new Intent(activity, DetailsActivity.class);
                    intent.putParcelableArrayListExtra(DetailsFragment.KEY_STEP, new ArrayList<Parcelable>(stepsList));
                    intent.putExtra(DetailsFragment.KEY_NUMB, position);
                    activity.startActivity(intent);
                } else {
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment detailsFragment = DetailsFragment.newInstance(stepsList,position);
                    fragmentTransaction.replace(R.id.details_container, detailsFragment);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    /* __________________________________________________________________________________________ */

    /* ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
      ViewHolder */

    // Single list item, get everything we need to display the results correctly
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.steps_title_text)
        TextView titleText;
        @BindView(R.id.steps_number_text)
        TextView numberText;

        ViewHolder(View listItem) {
            super(listItem);
            ButterKnife.bind(this, listItem);
        }
    }

    /* __________________________________________________________________________________________ */
}
