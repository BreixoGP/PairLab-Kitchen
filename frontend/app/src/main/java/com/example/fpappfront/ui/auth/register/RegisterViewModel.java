package com.example.fpappfront.ui.auth.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.data.model.LoginResponse;
import com.example.fpappfront.data.repository.AuthRepository;

public class RegisterViewModel extends ViewModel {

    private AuthRepository repository;

    private MutableLiveData<LoginResponse> registerResult = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public RegisterViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<LoginResponse> getRegisterResult() {
        return registerResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void register(RegisterRequest request) {

        repository.register(request, new AuthRepository.RegisterCallback() {

            @Override
            public void onSuccess() {
                // 🔥 aquí asumimos auto-login simple (sin token real del backend)
                LoginResponse fakeResponse = new LoginResponse();
                registerResult.postValue(fakeResponse);
            }

            @Override
            public void onError(String err) {
                error.postValue(err);
            }
        });
    }
}