package com.example.fpappfront.ui.auth.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fpappfront.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin, btnGoRegister;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoRegister = view.findViewById(R.id.btnGoRegister);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            viewModel.login(user, pass);
        });

        btnGoRegister.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_login_to_register)
        );

        observeViewModel(view);
    }

    private void observeViewModel(View view) {

        viewModel.getLoginResult().observe(getViewLifecycleOwner(), response -> {

            requireContext()
                    .getSharedPreferences("auth", Context.MODE_PRIVATE)
                    .edit()
                    .putString("token", response.getToken())
                    .apply();

            Snackbar.make(view, "Login correcto", Snackbar.LENGTH_SHORT).show();

            Navigation.findNavController(view)
                    .navigate(R.id.action_login_to_home);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            Snackbar.make(view, err, Snackbar.LENGTH_LONG).show();
        });
    }
}