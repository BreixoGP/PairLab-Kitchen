package com.example.fpappfront.ui.home;

import android.content.Context;

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
import com.example.fpappfront.data.network.ApiService;
import com.example.fpappfront.data.network.RetrofitClient;

import java.util.List;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
    private final MutableLiveData<List<String>> families = new MutableLiveData<>();
    private final MutableLiveData<List<Combo>> combos = new MutableLiveData<>();

    private final ApiService api =
            RetrofitClient.getClient().create(ApiService.class);

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public LiveData<List<String>> getFamilies() {
        return families;
    }

    public LiveData<List<Combo>> getCombos() {
        return combos;
    }

    // ---------------- INIT ----------------

    public void loadInitialData(Context context, String token) {
        loadIngredients(context, token);
        loadFamilies(context, token);
    }

    // ---------------- INGREDIENTS ----------------

    private void loadIngredients(Context context, String token) {

        List<Ingredient> cached = HomeCache.getIngredients(context);

        if (cached != null) {
            ingredients.setValue(cached);
            return;
        }

        api.getIngredients("Token " + token)
                .enqueue(new Callback<IngredientsResponse>() {
                    @Override
                    public void onResponse(Call<IngredientsResponse> call,
                                           Response<IngredientsResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Ingredient> data = response.body().ingredients;

                            HomeCache.saveIngredients(context, data);
                            ingredients.setValue(data);
                        }
                    }

                    @Override
                    public void onFailure(Call<IngredientsResponse> call, Throwable t) {}
                });
    }

    // ---------------- FAMILIES ----------------

    private void loadFamilies(Context context, String token) {

        List<String> cached = HomeCache.getFamilies(context);

        if (cached != null) {
            families.setValue(cached);
            return;
        }

        api.getFamilies("Token " + token)
                .enqueue(new Callback<FamiliesResponse>() {
                    @Override
                    public void onResponse(Call<FamiliesResponse> call,
                                           Response<FamiliesResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<String> data = response.body().families;

                            HomeCache.saveFamilies(context, data);
                            families.setValue(data);
                        }
                    }

                    @Override
                    public void onFailure(Call<FamiliesResponse> call, Throwable t) {}
                });
    }

    // ---------------- COMBOS ----------------

    public void loadCombos(String token,
                           int ingredientId,
                           int comboSize,
                           List<String> familyFilter) {

        PairingRequest request =
                new PairingRequest(ingredientId, comboSize, familyFilter);

        api.getPairings("Token " + token, request)
                .enqueue(new Callback<PairingResponse>() {
                    @Override
                    public void onResponse(Call<PairingResponse> call,
                                           Response<PairingResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            combos.setValue(response.body().results);
                        }
                    }

                    @Override
                    public void onFailure(Call<PairingResponse> call, Throwable t) {}
                });
    }
}