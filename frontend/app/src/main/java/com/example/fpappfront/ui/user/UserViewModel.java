package com.example.fpappfront.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fpappfront.data.model.UpdateUserRequest;
import com.example.fpappfront.data.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    private final MutableLiveData<String> username =
            new MutableLiveData<>();

    private final MutableLiveData<String> email =
            new MutableLiveData<>();

    private final MutableLiveData<String> message =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    public UserViewModel() {
        repository = new UserRepository();
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadUser(int id, String token) {

        loading.setValue(true);

        repository.getUser(id, token,
                new UserRepository.UserCallback() {

                    @Override
                    public void onSuccess(String u, String e) {

                        username.setValue(u);
                        email.setValue(e);

                        loading.setValue(false);
                    }

                    @Override
                    public void onError(String error) {

                        message.setValue(error);

                        loading.setValue(false);
                    }
                });
    }

    public void updateUser(
            int id,
            String token,
            UpdateUserRequest request
    ) {

        loading.setValue(true);

        repository.updateUser(
                id,
                token,
                request,
                new UserRepository.ActionCallback() {

                    @Override
                    public void onSuccess(String msg) {

                        message.setValue(msg);

                        loadUser(id, token);
                    }

                    @Override
                    public void onError(String error) {

                        message.setValue(error);

                        loading.setValue(false);
                    }
                });
    }

    public void deleteUser(
            int id,
            String token,
            Runnable onSuccess
    ) {

        loading.setValue(true);

        repository.deleteUser(
                id,
                token,
                new UserRepository.ActionCallback() {

                    @Override
                    public void onSuccess(String msg) {

                        loading.setValue(false);

                        onSuccess.run();
                    }

                    @Override
                    public void onError(String error) {

                        message.setValue(error);

                        loading.setValue(false);
                    }
                });
    }

    public void logout(
            String token,
            Runnable onSuccess
    ) {

        repository.logout(
                token,
                new UserRepository.ActionCallback() {

                    @Override
                    public void onSuccess(String msg) {

                        onSuccess.run();
                    }

                    @Override
                    public void onError(String error) {

                        message.setValue(error);
                    }
                });
    }
}