package com.example.fpappfront.data.repository;

import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.data.network.ApiService;
import com.example.fpappfront.data.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void login(LoginRequest request, LoginCallback callback) {

        apiService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }

                try {
                    String errorBody = response.errorBody() != null
                            ? response.errorBody().string()
                            : null;

                    String message = "Unknown error";

                    if (errorBody != null) {
                        JSONObject json = new JSONObject(errorBody);
                        message = json.optString("error", json.optString("message", message));
                    }

                    callback.onError(message);

                } catch (Exception e) {
                    callback.onError("Server error");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request, RegisterCallback callback) {

        apiService.register(request).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess();
                    return;
                }

                try {
                    String errorBody = response.errorBody().string();
                    JSONObject json = new JSONObject(errorBody);

                    String error = json.optString("error", "Unknown error");

                    callback.onError(error);

                } catch (Exception e) {
                    callback.onError("Server error");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(String error);
    }

    public interface RegisterCallback {
        void onSuccess();
        void onError(String error);
    }
}