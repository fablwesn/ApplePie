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

package com.fablwesn.education.applepie.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RecipeModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("ingredients")
    @Expose
    private List<IngredientsModel> ingredientsModels = new ArrayList<>();
    @SerializedName("steps")
    @Expose
    private List<StepsModel> stepsModels = new ArrayList<>();
    @SerializedName("servings")
    @Expose
    private int servings;
    @SerializedName("image")
    @Expose
    private String image;

    public final static Parcelable.Creator<RecipeModel> CREATOR = new Creator<RecipeModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RecipeModel createFromParcel(Parcel in) {
            return new RecipeModel(in);
        }

        public RecipeModel[] newArray(int size) {
            return (new RecipeModel[size]);
        }

    };

    private RecipeModel(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.ingredientsModels, (IngredientsModel.class.getClassLoader()));
        in.readList(this.stepsModels, (StepsModel.class.getClassLoader()));
        this.servings = ((int) in.readValue((int.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public RecipeModel() {
    }

    /**
     * @param ingredientsModels
     * @param id
     * @param servings
     * @param name
     * @param image
     * @param stepsModels
     */
    public RecipeModel(int id, String name, List<IngredientsModel> ingredientsModels, List<StepsModel> stepsModels, int servings, String image) {
        super();
        this.id = id;
        this.name = name;
        this.ingredientsModels = ingredientsModels;
        this.stepsModels = stepsModels;
        this.servings = servings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IngredientsModel> getIngredientsModels() {
        return ingredientsModels;
    }

    public void setIngredientsModels(List<IngredientsModel> ingredientsModels) {
        this.ingredientsModels = ingredientsModels;
    }

    public List<StepsModel> getStepsModels() {
        return stepsModels;
    }

    public void setStepsModels(List<StepsModel> stepsModels) {
        this.stepsModels = stepsModels;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "RecipeModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ingredientsModels=" + ingredientsModels +
                ", stepsModels=" + stepsModels +
                ", servings=" + servings +
                ", image='" + image + '\'' +
                '}';
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeList(ingredientsModels);
        dest.writeList(stepsModels);
        dest.writeValue(servings);
        dest.writeValue(image);
    }

    public int describeContents() {
        return 0;
    }

}