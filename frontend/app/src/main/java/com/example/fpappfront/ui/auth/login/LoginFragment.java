package com.example.fpappfront.ui.auth.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.fpappfront.R;
import com.example.fpappfront.utils.ViewUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;

    private TextInputEditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;

    private MaterialButton btnLogin, btnGoRegister;
    private ProgressBar progressBar;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews(view);
        setupListeners(view);
        observeViewModel(view);
    }

    private void initViews(View view) {
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);

        tilUsername = view.findViewById(R.id.tilUsername);
        tilPassword = view.findViewById(R.id.tilPassword);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoRegister = view.findViewById(R.id.btnGoRegister);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupListeners(View view) {
        btnLogin.setOnClickListener(v -> attemptLogin(view));

        btnGoRegister.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_login_to_register)
        );

        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilUsername.setError(null);
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilPassword.setError(null);
        });
    }

    private void attemptLogin(View view) {
        String user = String.valueOf(etUsername.getText());
        String pass = String.valueOf(etPassword.getText());

        ViewUtils.clearErrors(tilUsername, tilPassword);

        boolean valid = true;

        if (user.isEmpty()) {
            tilUsername.setError(getString(R.string.empty_fields));
            valid = false;
        }

        if (pass.isEmpty()) {
            tilPassword.setError(getString(R.string.empty_fields));
            valid = false;
        }

        if (!valid) return;

        ViewUtils.hideKeyboard(requireContext(), view);

        viewModel.login(user, pass);
    }

    private void observeViewModel(View view) {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    btnGoRegister.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    btnGoRegister.setEnabled(true);
                }
            }
        });

        viewModel.getLoginResult().observe(getViewLifecycleOwner(), response -> {
            requireContext()
                    .getSharedPreferences("auth", Context.MODE_PRIVATE)
                    .edit()
                    .putString("token", response.getToken())
                    .putInt("user_id", response.getUserId())
                    .apply();

            Snackbar.make(
                    view,
                    getString(R.string.login_success),
                    Snackbar.LENGTH_SHORT
            ).show();

            Navigation.findNavController(view)
                    .navigate(R.id.action_login_to_home);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err.toLowerCase().contains("user")) {
                tilUsername.setError(err);
            } else if (err.toLowerCase().contains("password")) {
                tilPassword.setError(err);
            } else {
                Snackbar.make(requireView(), err, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}