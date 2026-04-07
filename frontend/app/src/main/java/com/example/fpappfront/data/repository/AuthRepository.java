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
                } else {
                    callback.onError("Login error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request, RegisterCallback callback) {

        apiService.register(request).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {

                    String errorMessage = "Error en registro";

                    try {
                        if (response.errorBody() != null) {

                            String errorBody = response.errorBody().string();

                            JSONObject json = new JSONObject(errorBody);

                            if (json.has("error")) {
                                errorMessage = json.getString("error");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onError(errorMessage);
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