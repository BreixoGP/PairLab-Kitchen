package com.example.fpappfront.data.repository;

import androidx.annotation.NonNull;

import com.example.fpappfront.data.model.UpdateUserRequest;
import com.example.fpappfront.data.model.UserResponse;
import com.example.fpappfront.data.network.ApiService;
import com.example.fpappfront.data.network.RetrofitClient;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;

    public UserRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void getUser(int id, String token, UserCallback callback) {

        apiService.getUser("Token " + token, id)
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            UserResponse user = response.body();

                            callback.onSuccess(
                                    user.username,
                                    user.email
                            );

                        } else {
                            callback.onError("Error loading user");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void updateUser(int id, String token, UpdateUserRequest request, ActionCallback callback) {

        apiService.updateUser("Token " + token, id, request)
                .enqueue(simpleCallback(callback));
    }

    public void deleteUser(int id, String token, ActionCallback callback) {

        apiService.deleteUser("Token " + token, id)
                .enqueue(simpleCallback(callback));
    }

    public void logout(String token, ActionCallback callback) {

        apiService.logout("Token " + token)
                .enqueue(simpleCallback(callback));
    }

    private Callback<Void> simpleCallback(ActionCallback callback) {

        return new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.isSuccessful()) {

                    callback.onSuccess("User updated successfully");
                } else {

                    try {
                        try (ResponseBody errorBodyResource = response.errorBody()) {

                            if (errorBodyResource != null) {
                                String errorBody = errorBodyResource.string();
                                JSONObject json = new JSONObject(errorBody);

                                String errorMessage = json.optString("error", "Unknown error");
                                callback.onError(errorMessage);
                            } else {
                                callback.onError("Server returned an empty error response");
                            }
                        }

                    } catch (Exception e) {
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        };
    }

    public interface UserCallback {
        void onSuccess(String username, String email);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}