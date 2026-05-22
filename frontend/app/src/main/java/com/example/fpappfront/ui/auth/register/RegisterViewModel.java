package com.example.fpappfront.ui.auth.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.data.repository.AuthRepository;

public class RegisterViewModel extends ViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<Boolean> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public RegisterViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<Boolean> getRegisterResult() {
        return registerResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void register(RegisterRequest request) {
        isLoading.postValue(true);

        repository.register(request, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                registerResult.postValue(true);
            }

            @Override
            public void onError(String err) {
                isLoading.postValue(false);
                error.postValue(err);
            }
        });
    }
}