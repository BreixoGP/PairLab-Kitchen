package com.example.fpappfront.data.model;

import java.util.List;

public class RecipeRequest {
    public List<String> ingredients;

    public RecipeRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}