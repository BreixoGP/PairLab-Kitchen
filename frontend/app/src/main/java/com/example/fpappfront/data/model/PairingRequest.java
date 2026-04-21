package com.example.fpappfront.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PairingRequest {

    @SerializedName("ingredient_id")
    public int ingredientId;

    @SerializedName("combo_size")
    public int comboSize;

    @SerializedName("family_filter")
    public List<String> familyFilter;

    public PairingRequest(int ingredientId, int comboSize, List<String> familyFilter) {
        this.ingredientId = ingredientId;
        this.comboSize = comboSize;
        this.familyFilter = familyFilter;
    }
}