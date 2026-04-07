package com.example.fpappfront.ui.auth.register;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fpappfront.R;
import com.example.fpappfront.data.model.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private RegisterViewModel viewModel;

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> attemptRegister(view));

        observeViewModel(view);
    }

    private void attemptRegister(View view) {

        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();

        // 🔥 VALIDACIÓN LOCAL
        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Snackbar.make(view, "Completa todos los campos", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(username, email, pass);
        viewModel.register(request);
    }

    private void observeViewModel(View view) {

        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), response -> {

            // 💾 guardar token (auto-login)
            requireContext()
                    .getSharedPreferences("auth", Context.MODE_PRIVATE)
                    .edit()
                    .putString("token", response.getToken())
                    .apply();

            Snackbar.make(view, "Registro correcto", Snackbar.LENGTH_SHORT).show();

            Navigation.findNavController(view)
                    .navigate(R.id.action_register_to_login);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            Snackbar.make(view, err, Snackbar.LENGTH_LONG).show();
        });
    }
}