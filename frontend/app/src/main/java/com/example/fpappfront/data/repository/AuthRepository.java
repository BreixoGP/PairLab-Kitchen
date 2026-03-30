package com.example.fpappfront.data.repository;


import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.data.network.ApiService;
import com.example.fpappfront.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // 🔐 LOGIN
    public void login(LoginRequest request, AuthCallback callback) {

        apiService.login(request).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Login error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 📝 REGISTER
    public void register(RegisterRequest request, AuthCallbackVoid callback) {

        apiService.register(request).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Register error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 🔧 CALLBACKS

    public interface AuthCallback {
        void onSuccess(Object response);
        void onError(String error);
    }

    public interface AuthCallbackVoid {
        void onSuccess();
        void onError(String error);
    }
}