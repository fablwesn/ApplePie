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

package com.fablwesn.education.applepie.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.fablwesn.education.applepie.R;
import com.fablwesn.education.applepie.data.RecipeListService;
import com.fablwesn.education.applepie.data.models.IngredientsModel;
import com.fablwesn.education.applepie.data.models.RecipeModel;
import com.fablwesn.education.applepie.utility.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The configuration screen for the {@link RecipeWidgetProvider} AppWidget.
 */
public class RecipeWidgetConfigureActivity extends AppCompatActivity {
    public RecipeWidgetConfigureActivity() {
        super();
    }
    
    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
           Bindings and Globals */
    
    @BindView(R.id.widget_config_header_text)
    TextView textHeader;
    @BindView(R.id.widget_config_pb)
    ProgressBar progressBar;
    @BindView(R.id.widget_config_recycler)
    RecyclerView recyclerView;

    @BindString(R.string.no_connection_error)
    String noConnectionString;

    private final String KEY_LIST = "list_loaded_widget";
    private ArrayList<RecipeModel> recipeList = new ArrayList<>();
    private static int appWidgetId;

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        setContentView(R.layout.recipe_widget_configure);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        //Load list and display it so the user can chose which recipe to add
        if (bundle == null) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                loadList();
            } else {
                Toast.makeText(this, noConnectionString, Toast.LENGTH_LONG).show();
            }
        } else {
            recipeList = bundle.getParcelableArrayList(KEY_LIST);
            if(recipeList != null && !recipeList.isEmpty()){
                displayList();
            }else {
                loadList();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(recipeList != null && !recipeList.isEmpty()){
            outState.putParcelableArrayList(KEY_LIST, recipeList);
        }
    }

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Helper methods */

    /**
     * Loads the list from the server
     */
    private void loadList() {
        RecipeListService recipeService = NetworkUtils.getRecipeListService();

        Call<ArrayList<RecipeModel>> recipeCall = recipeService.getAllRecipes();

        recipeCall.enqueue(new Callback<ArrayList<RecipeModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<RecipeModel>> call, @NonNull Response<ArrayList<RecipeModel>> response) {
                recipeList = response.body();

                displayList();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<RecipeModel>> call, @NonNull Throwable t) {
                Context context = getApplicationContext();
                progressBar.setVisibility(View.GONE);
                textHeader.setText(R.string.no_info_available);
                if (NetworkUtils.isNetworkAvailable(context)) {
                    Toast.makeText(context, R.string.no_data_fetched, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, noConnectionString, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Displays a list of available data inside the recyclerview
     */
    private void displayList(){
        progressBar.setVisibility(View.GONE);
        textHeader.setText(R.string.configure_widget_header);

        WidgetRecyclerAdapter recyclerAdapter = new WidgetRecyclerAdapter(this, recipeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }

    /**
     * Styles and enables the home-screen widget, triggered by the recycler on click
     *
     * @param activity that starts this
     * @param recipe clicked, to display
     */
    public static void startWidget(Activity activity, RecipeModel recipe){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);

        RemoteViews views = new RemoteViews(activity.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.widget_header_text, recipe.getName());
        List<IngredientsModel> ingredientsList = recipe.getIngredientsModels();
        String ingredients = "";

        for(IngredientsModel ingredientsModel : ingredientsList){
            ingredients = ingredients + "\n" + activity.getResources().getString(R.string.widget_ingredients, String.valueOf(ingredientsModel.getQuantity()), ingredientsModel.getMeasure(), ingredientsModel.getIngredient());
        }
        views.setTextViewText(R.id.widget_ingredients_text, ingredients);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        activity.setResult(RESULT_OK, resultValue);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        activity.finish();
    }

    /*____________________________________________________________________________________________*/
}

