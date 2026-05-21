package com.example.fpappfront.data.model;

import com.google.gson.annotations.SerializedName;

public class RecipeResponse {
    public String title;

    @SerializedName("extra_ingredients")
    public String extraIngredients;

    public String steps;
}