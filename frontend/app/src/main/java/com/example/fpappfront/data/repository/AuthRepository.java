package com.example.fpappfront.data.repository;

import androidx.annotation.NonNull;

import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.data.network.ApiService;
import com.example.fpappfront.data.network.RetrofitClient;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void login(LoginRequest request, LoginCallback callback) {

        apiService.login(request).enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }

                try {
                    String errorBody = null;

                    try (ResponseBody errorBodyResource = response.errorBody()) {
                        if (errorBodyResource != null) {
                            errorBody = errorBodyResource.string();
                        }
                    }

                    String message = "Unknown error";

                    if (errorBody != null) {
                        JSONObject json = new JSONObject(errorBody);
                        message = json.optString("error", json.optString("message", message));
                    }

                    callback.onError(message);

                } catch (Exception e) {
                    callback.onError("Server error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request, RegisterCallback callback) {

        apiService.register(request).enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess();
                    return;
                }
                try {
                    try (ResponseBody errorBodyResource = response.errorBody()) {

                        if (errorBodyResource != null) {
                            String errorBody = errorBodyResource.string(); // Al hacer .string(), OkHttp ya lo lee y lo marca para cerrar
                            JSONObject json = new JSONObject(errorBody);

                            String error = json.optString("error", "Unknown error");
                            callback.onError(error);
                        } else {
                            callback.onError("Server returned an empty error response");
                        }
                    }

                } catch (Exception e) {
                    callback.onError("Parsing error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
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