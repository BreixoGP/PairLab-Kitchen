package com.example.fpappfront.data.repository;

import com.example.fpappfront.data.model.FamiliesResponse;
import com.example.fpappfront.data.model.IngredientsResponse;
import com.example.fpappfront.data.model.PairingRequest;
import com.example.fpappfront.data.model.PairingResponse;
import com.example.fpappfront.data.model.RecipeRequest;
import com.example.fpappfront.data.model.RecipeResponse;
import com.example.fpappfront.data.network.ApiService;
import com.example.fpappfront.data.network.RetrofitClient;

import java.util.List;
import retrofit2.Callback;

public class HomeRepository {

    private final ApiService api;

    public HomeRepository() {
        this.api = RetrofitClient.getClient().create(ApiService.class);
    }

    public void getIngredients(String token, Callback<IngredientsResponse> callback) {
        api.getIngredients("Token " + token).enqueue(callback);
    }

    public void getFamilies(String token, Callback<FamiliesResponse> callback) {
        api.getFamilies("Token " + token).enqueue(callback);
    }

    public void getPairings(String token, PairingRequest request, Callback<PairingResponse> callback) {
        api.getPairings("Token " + token, request).enqueue(callback);
    }

    public void getAiRecipe(String token, List<String> ingredients, Callback<RecipeResponse> callback) {
        RecipeRequest request = new RecipeRequest(ingredients);
        api.getAiRecipe("Token " + token, request).enqueue(callback);
    }
}