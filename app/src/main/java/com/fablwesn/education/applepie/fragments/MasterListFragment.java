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

package com.fablwesn.education.applepie.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fablwesn.education.applepie.R;
import com.fablwesn.education.applepie.adapters.IngredientsRecyclerAdapter;
import com.fablwesn.education.applepie.adapters.StepsRecyclerAdapter;
import com.fablwesn.education.applepie.data.models.IngredientsModel;
import com.fablwesn.education.applepie.data.models.RecipeModel;
import com.fablwesn.education.applepie.data.models.StepsModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MasterListFragment extends Fragment {

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
      Bindings and Globals */

    @BindView(R.id.ingredients_title_text)
    TextView ingrTitleText;
    @BindView(R.id.ingredients_recycler)
    RecyclerView ingrRecycler;
    @BindView(R.id.steps_recycler)
    RecyclerView stepsRecycler;


    private final static String KEY_RECIPE = "recipe_loaded";
    private final static String LOG_INTERFACE_ERROR = " must implement OnStepClickListener";

    private Unbinder viewUnbind;
    private RecipeModel recipeModel;

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */

    public static MasterListFragment newInstance(RecipeModel recipeModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_RECIPE, recipeModel);

        MasterListFragment fragment = new MasterListFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_masterlist, container, false);
        viewUnbind = ButterKnife.bind(this, rootView);

        readBundle(getArguments());

        Activity activity = getActivity();

        ingrTitleText.setText(getString(R.string.ingredients_title, recipeModel.getName()));
        displayIngredients(activity);
        displaySteps(activity);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Free binded views.
        viewUnbind.unbind();
    }

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Helper Methods */

    /**
     * Reads the bundle to get our model
     *
     * @param bundle saved
     */
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            recipeModel = bundle.getParcelable(KEY_RECIPE);
        }
    }

    /**
     * Fills the recycler with the ingredients list
     */
    private void displayIngredients(Activity activity){
        List<IngredientsModel> ingrList = recipeModel.getIngredientsModels();

        if(activity != null){
            IngredientsRecyclerAdapter recyclerAdapter = new IngredientsRecyclerAdapter(ingrList);
            ingrRecycler.setLayoutManager(new LinearLayoutManager(activity));
            ingrRecycler.addItemDecoration(new DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL));
            ingrRecycler.setAdapter(recyclerAdapter);
        }
    }

    /**
     * Fills the recycler with the steps
     */
    private void displaySteps(Activity activity){
        List<StepsModel> stepsList = recipeModel.getStepsModels();

        if(activity != null){
            StepsRecyclerAdapter recyclerAdapter = new StepsRecyclerAdapter(activity, stepsList, getFragmentManager());
            stepsRecycler.setLayoutManager(new LinearLayoutManager(activity));
            stepsRecycler.addItemDecoration(new DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL));
            stepsRecycler.setAdapter(recyclerAdapter);
        }
    }

    /*____________________________________________________________________________________________*/
}
