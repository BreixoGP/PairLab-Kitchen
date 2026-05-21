package com.example.fpappfront.ui.auth.register;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.fpappfront.R;
import com.example.fpappfront.data.model.RegisterRequest;
import com.example.fpappfront.utils.ViewUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {

    private RegisterViewModel viewModel;

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;

    private MaterialButton btnRegister, btnBackToLogin;


    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        initViews(view);
        setupListeners(view);
        observeViewModel(view);
    }

    private void initViews(View view) {

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        tilUsername = view.findViewById(R.id.tilUsername);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);

        btnRegister = view.findViewById(R.id.btnRegister);
        btnBackToLogin = view.findViewById(R.id.btnBackToLogin);
    }

    private void setupListeners(View view) {

        btnRegister.setOnClickListener(v -> attemptRegister(view));
        btnBackToLogin.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_register_to_login)
        );
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilEmail.setError(null);
        });
        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilUsername.setError(null);
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilPassword.setError(null);
        });
    }

    private void attemptRegister(View view) {

        String user = String.valueOf(etUsername.getText());
        String email = String.valueOf(etEmail.getText());
        String pass = String.valueOf(etPassword.getText());
        String confirm = String.valueOf(etConfirmPassword.getText());

        ViewUtils.clearErrors(
                tilUsername,
                tilEmail,
                tilPassword,
                tilConfirmPassword
        );

        boolean valid = true;

        if (user.isEmpty()) {
            tilUsername.setError(getString(R.string.empty_fields));
            valid = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.empty_fields));
            valid = false;
        }

        if (pass.isEmpty()) {
            tilPassword.setError(getString(R.string.empty_fields));
            valid = false;
        }

        if (!pass.equals(confirm)) {
            tilPassword.setError(getString(R.string.password_mismatch));
            tilConfirmPassword.setError(getString(R.string.password_mismatch));
            valid = false;
        }

        if (!valid) return;

        ViewUtils.hideKeyboard(requireContext(), view);

        RegisterRequest request = new RegisterRequest(user, email, pass);

        viewModel.register(request);
    }

    private void observeViewModel(View view) {

        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), res -> {

            Snackbar.make(
                    view,
                    getString(R.string.register_success),
                    Snackbar.LENGTH_SHORT
            ).show();

            Navigation.findNavController(view)
                    .navigate(R.id.action_register_to_login);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {

            if (err == null) return;

            if (err.toLowerCase().contains("username")) {
                tilUsername.setError(err);
                return;
            }

            if (err.toLowerCase().contains("email")) {
                tilEmail.setError(err);
                return;
            }

            if (err.toLowerCase().contains("password")) {
                tilPassword.setError(err);
                tilConfirmPassword.setError(err);
                return;
            }

            Snackbar.make(view, err, Snackbar.LENGTH_LONG).show();
        });
    }
}