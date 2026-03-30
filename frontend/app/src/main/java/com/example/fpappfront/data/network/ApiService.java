package com.example.fpappfront.data.network;
import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login/")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register/")
    Call<Void> register(@Body RegisterRequest request);
}