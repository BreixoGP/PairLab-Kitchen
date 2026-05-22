package com.example.fpappfront.ui.auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fpappfront.data.model.LoginRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String username, String password) {
        isLoading.postValue(true);

        LoginRequest request = new LoginRequest(username, password);

        repository.login(request, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                isLoading.postValue(false);
                loginResult.postValue(response);
            }

            @Override
            public void onError(String err) {
                isLoading.postValue(false);
                error.postValue(err);
            }
        });
    }
}