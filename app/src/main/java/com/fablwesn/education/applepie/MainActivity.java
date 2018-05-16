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

package com.fablwesn.education.applepie;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fablwesn.education.applepie.adapters.MainRecyclerAdapter;
import com.fablwesn.education.applepie.data.RecipeListService;
import com.fablwesn.education.applepie.data.models.RecipeModel;
import com.fablwesn.education.applepie.utility.NetworkUtils;
import com.fablwesn.education.applepie.utility.SimpleIdlingResource;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Bindings and Globals */

    @BindView(R.id.refresh_lay_main)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view_main)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_main)
    ProgressBar progressBar;
    @BindView(R.id.text_empty_main)
    TextView emptyText;

    @BindString(R.string.no_connection_error)
    String noConnectionString;

    private final static String KEY_LIST = "loaded_list";
    private Context context;
    private ArrayList<RecipeModel> recipeList = new ArrayList<>();

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getIdlingResource();
        context = getApplicationContext();
        prepareSwipeRefresh();

        if (savedInstanceState == null) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                emptyText.setText(R.string.loading_data);
                loadRecipes();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                emptyText.setText(noConnectionString);
            }
        } else {
            recipeList = savedInstanceState.getParcelableArrayList(KEY_LIST);

            if (recipeList == null) {
                loadRecipes();
            } else {
                displayRecipes();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if (recipeList != null) {
            outState.putParcelableArrayList(KEY_LIST, recipeList);
        }
    }

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Helper Methods */

    /**
     * Sets an onRefreshListener for the user to update the list
     */
    private void prepareSwipeRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    loadRecipes();
                } else {
                    emptyText.setText(noConnectionString);
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * Loads the recipe list from the server and then displays it
     */
    private void loadRecipes() {
        RecipeListService recipeService = NetworkUtils.getRecipeListService();

        Call<ArrayList<RecipeModel>> recipeCall = recipeService.getAllRecipes();

        recipeCall.enqueue(new Callback<ArrayList<RecipeModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<RecipeModel>> call, @NonNull Response<ArrayList<RecipeModel>> response) {
                emptyText.setText("");
                progressBar.setVisibility(View.INVISIBLE);
                refreshLayout.setRefreshing(false);

                recipeList = response.body();

                displayRecipes();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<RecipeModel>> call, @NonNull Throwable t) {
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    emptyText.setText(noConnectionString);
                } else {
                    emptyText.setText(R.string.no_data_fetched);
                }
                progressBar.setVisibility(View.INVISIBLE);

                refreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Uses available data to display the list inside the RecyclerView
     */
    private void displayRecipes() {
        MainRecyclerAdapter recyclerAdapter = new MainRecyclerAdapter(this, recipeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        if (idlingResource != null) {
            idlingResource.setIdleState(true);
        }
    }

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Testing */

    @Nullable
    private SimpleIdlingResource idlingResource;

    /**
     * Creates and returns a new {@link SimpleIdlingResource}.
     * Testing only.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

    /*____________________________________________________________________________________________*/
}
