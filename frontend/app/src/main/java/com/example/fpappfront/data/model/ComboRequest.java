package com.example.fpappfront.data.model;

import java.util.List;

public class ComboRequest {

    public int baseIngredientId;
    public int comboSize;
    public List<String> familyFilter;
    public ComboRequest(int baseIngredientId, int comboSize, List<String> familyFilter) {
        this.baseIngredientId = baseIngredientId;
        this.comboSize = comboSize;
        this.familyFilter = familyFilter;
    }
}