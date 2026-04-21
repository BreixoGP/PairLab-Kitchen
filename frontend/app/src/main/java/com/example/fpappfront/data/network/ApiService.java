package com.example.fpappfront.data.network;
import com.example.fpappfront.data.model.FamiliesResponse;
import com.example.fpappfront.data.model.IngredientsResponse;
import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.model.PairingRequest;
import com.example.fpappfront.data.model.PairingResponse;
import com.example.fpappfront.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    // AUTH (ya lo tienes)
    @POST("/api/auth/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register/")
    Call<Void> register(@Body RegisterRequest request);

    @GET("/api/ingredients/")
    Call<IngredientsResponse> getIngredients(
            @Header("Authorization") String token
    );

    @GET("/api/ingredients/families/")
    Call<FamiliesResponse> getFamilies(
            @Header("Authorization") String token
    );

    @POST("/api/pairings/")
    Call<PairingResponse> getPairings(
            @Header("Authorization") String token,
            @Body PairingRequest request
    );
}