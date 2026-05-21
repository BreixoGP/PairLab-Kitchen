package com.example.fpappfront.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fpappfront.data.cache.HomeCache;
import com.example.fpappfront.data.model.Combo;
import com.example.fpappfront.data.model.FamiliesResponse;
import com.example.fpappfront.data.model.Ingredient;
import com.example.fpappfront.data.model.IngredientsResponse;
import com.example.fpappfront.data.model.PairingRequest;
import com.example.fpappfront.data.model.PairingResponse;
import com.example.fpappfront.data.model.RecipeResponse;
import com.example.fpappfront.data.repository.HomeRepository;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final HomeRepository repository = new HomeRepository();

    private final MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
    private final MutableLiveData<List<String>> families = new MutableLiveData<>();
    private final MutableLiveData<List<Combo>> combos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<RecipeResponse> recipeResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Ingredient>> getIngredients() { return ingredients; }
    public LiveData<List<String>> getFamilies() { return families; }
    public LiveData<List<Combo>> getCombos() { return combos; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<RecipeResponse> getRecipeResult() { return recipeResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadInitialData(Context context, String token) {
        loadIngredients(context, token);
        loadFamilies(context, token);
    }

    private void loadIngredients(Context context, String token) {
        List<Ingredient> cached = HomeCache.getIngredients(context);
        if (cached != null) {
            ingredients.setValue(cached);
            return;
        }

        repository.getIngredients(token, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<IngredientsResponse> call, @NonNull Response<IngredientsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ingredient> data = response.body().ingredients;
                    HomeCache.saveIngredients(context, data);
                    ingredients.setValue(data);
                }
            }

            @Override
            public void onFailure(@NonNull Call<IngredientsResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void loadFamilies(Context context, String token) {
        List<String> cached = HomeCache.getFamilies(context);
        if (cached != null) {
            families.setValue(cached);
            return;
        }

        repository.getFamilies(token, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FamiliesResponse> call, @NonNull Response<FamiliesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> data = response.body().families;
                    HomeCache.saveFamilies(context, data);
                    families.setValue(data);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FamiliesResponse> call, @NonNull Throwable t) {
            }
        });
    }

    public void loadCombos(String token, int ingredientId, int comboSize, List<String> familyFilter) {
        PairingRequest request = new PairingRequest(ingredientId, comboSize, familyFilter);

        repository.getPairings(token, request, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PairingResponse> call, @NonNull Response<PairingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    combos.setValue(response.body().results);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PairingResponse> call, @NonNull Throwable t) {
            }
        });
    }

    public void generateRecipe(String token, List<String> ingredientNames) {
        isLoading.setValue(true);

        repository.getAiRecipe(token, ingredientNames, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    recipeResult.setValue(response.body());
                } else {
                    errorMessage.setValue("Error generating recipe. Try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}