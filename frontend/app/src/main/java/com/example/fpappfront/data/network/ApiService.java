package com.example.fpappfront.data.network;

import com.example.fpappfront.data.model.FamiliesResponse;
import com.example.fpappfront.data.model.IngredientsResponse;
import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.model.PairingRequest;
import com.example.fpappfront.data.model.PairingResponse;
import com.example.fpappfront.data.model.RecipeRequest;
import com.example.fpappfront.data.model.RecipeResponse;
import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.data.model.UserResponse;
import com.example.fpappfront.data.model.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/api/auth/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register/")
    Call<Void> register(@Body RegisterRequest request);

    @POST("/api/auth/logout/")
    Call<Void> logout(
            @Header("Authorization") String token
    );

    @GET("/api/users/{id}/")
    Call<UserResponse> getUser(
            @Header("Authorization") String token,
            @Path("id") int userId
    );

    @PUT("/api/users/{id}/")
    Call<Void> updateUser(
            @Header("Authorization") String token,
            @Path("id") int userId,
            @Body UpdateUserRequest request
    );

    @DELETE("/api/users/{id}/")
    Call<Void> deleteUser(
            @Header("Authorization") String token,
            @Path("id") int userId
    );

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

    @POST("api/pairings/recipe/")
    Call<RecipeResponse> getAiRecipe(
            @Header("Authorization") String token,
            @Body RecipeRequest request
    );
}